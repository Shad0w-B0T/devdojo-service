package devdojo.compiler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import devdojo.compiler.amqp.RabbitConnection;
import devdojo.compiler.config.ServerConfig;
import devdojo.compiler.heartbeat.HeartbeatProcess;
import devdojo.compiler.heartbeat.IHeartbeatProcess;
import devdojo.compiler.security.ISecurityFilter;
import devdojo.schema.amqp.CompileRequestPayload;
import devdojo.schema.amqp.CompileResponsePayload;
import devdojo.schema.amqp.RabbitConst;
import devdojo.schema.code.TestError;
import devdojo.schema.code.TestRunResult;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

/**
 * Entry point for the gRPC Code Service server.
 * <p>
 * This class initializes and starts the gRPC server that hosts the code execution service.
 * The server listens on a configurable port specified as the first command-line argument.
 * If no port is provided, it defaults to port 9090.
 */
@Slf4j
public final class CompilerServer {

  private final ObjectMapper objectMapper = new ObjectMapper();

  private static final CompilerServer INSTANCE = new CompilerServer();

  public static CompilerServer instance() {
    return INSTANCE;
  }

  private CompilerServer() {
    // Prevent instantiation
    log.debug("Initializing CompilerServer");
  }

  public void begin() throws Exception {

    final UUID id = UUID.randomUUID();
    final ServerConfig config = new ServerConfig();

    final RabbitConnection rabbitConnection = new RabbitConnection(
      config.rabbitHost(),
      config.rabbitPort(),
      config.rabbitUsername(),
      config.rabbitPassword(),
      config.rabbitVirtualHost()
    );

    final String queueName = RabbitConst.queueForCompiler(config.token());

    final Channel channel = rabbitConnection.newConnection().createChannel();
    channel.queueDeclare(queueName, true, false, false, null);

    DeliverCallback deliverCallback = (consumerTag, delivery) -> {

      try {
        final String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
        log.debug("Received compile request: {}", message);

        final CompileRequestPayload payload =
          this.objectMapper.readValue(message, CompileRequestPayload.class);

        final ICompiler compiler = CompilerContext.instance().compiler();

        if (compiler == null) {
          log.error("No compiler instance available");
          publishError(rabbitConnection, payload.requestId(),
            "Compiler not available.");
          return;
        }

        try {
          this.securityCheck(payload.methodCode());
        } catch (Exception ex) {
          log.warn("Security check failed.", ex);
          publishError(rabbitConnection, payload.requestId(),
            "Security validation failed: " + ex.getMessage());
          return;
        }

        final Object[][] argsArray = payload.arguments().stream()
          .map(innerList -> innerList.toArray(new Object[0]))
          .toArray(Object[][]::new);

        try {
          final List<TestRunResult> results =
            compiler.compileAndRun(payload.methodCode(), argsArray);

          publishSuccess(rabbitConnection, payload.requestId(), results);

        } catch (Exception compileException) {
          log.error("Compilation failed", compileException);

          final TestError error = new TestError(
            "Compiler",
            compileException.getMessage()
          );

          final List<TestRunResult> result =
            Collections.singletonList(
              new TestRunResult(
                null,
                null,
                Collections.emptyList(),
                Collections.singletonList(error),
                0,
                0
              )
            );

          publishSuccess(rabbitConnection, payload.requestId(), result);
        }

      } catch (Throwable fatal) {
        // ABSOLUTE SAFETY NET
        log.error("Fatal error in consumer callback", fatal);
      }
    };

    final CancelCallback cancelCallback = consumerTag ->
      log.warn("Consumer cancelled: {}", consumerTag);

    channel.basicConsume(queueName, true, deliverCallback, cancelCallback);

    configureHeartbeat(rabbitConnection, id, config);

    log.info("Compiler consumer started. Queue={}", queueName);
  }

  private void publishSuccess(final RabbitConnection rabbitConnection,
                              final UUID requestId,
                              final List<TestRunResult> results) {
    try (Connection connection = rabbitConnection.newConnection();
         Channel channel = connection.createChannel()) {

      CompileResponsePayload response =
        new CompileResponsePayload(requestId, results);

      String json = objectMapper.writeValueAsString(response);

      channel.basicPublish(
        RabbitConst.EXCHANGE_COMPILER_REPORT,
        RabbitConst.KEY_COMPILER_REPORT,
        null,
        json.getBytes(StandardCharsets.UTF_8)
      );

    } catch (Exception e) {
      log.error("Failed to publish success response", e);
    }
  }

  private void publishError(final RabbitConnection rabbitConnection,
                            final UUID requestId,
                            final String message) {

    try (Connection connection = rabbitConnection.newConnection();
         Channel channel = connection.createChannel()) {

      final TestError error = new TestError("Compiler", message);

      final List<TestRunResult> result =
        Collections.singletonList(
          new TestRunResult(
            null,
            null,
            Collections.emptyList(),
            Collections.singletonList(error),
            0,
            0
          )
        );

      CompileResponsePayload response =
        new CompileResponsePayload(requestId, result);

      String json = objectMapper.writeValueAsString(response);

      channel.basicPublish(
        RabbitConst.EXCHANGE_COMPILER_REPORT,
        RabbitConst.KEY_COMPILER_REPORT,
        null,
        json.getBytes(StandardCharsets.UTF_8)
      );

    } catch (Exception e) {
      log.error("Failed to publish error response", e);
    }
  }


  private void securityCheck(final String methodCode) {
    final List<ISecurityFilter> securityFilters = CompilerContext.instance().securityFilters();

    if (securityFilters == null || securityFilters.isEmpty()) {
      log.warn("No security filters found... Thats not good :c");
      return;
    }

    for (final ISecurityFilter securityFilter : securityFilters) {
      try {
        if (securityFilter.check(methodCode)) {
          continue;
        }
      } catch (final Exception exception) {
        log.warn("Error while checking security filters.", exception);
      }
      throw new RuntimeException("Security filter " + securityFilter.getClass().getName());
    }
  }

  private static IHeartbeatProcess configureHeartbeat(final RabbitConnection rabbitConnection,
                                                      final UUID id,
                                                      final ServerConfig config) {
    final IHeartbeatProcess heartbeatProcess = new HeartbeatProcess(rabbitConnection, id.toString(), config.token());
    CompilerContext.instance().heartbeatProcess(heartbeatProcess);
    heartbeatProcess.start();
    return heartbeatProcess;
  }
}

