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
import devdojo.schema.code.TestRunResult;
import java.nio.charset.StandardCharsets;
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
  }

  public void begin() throws Exception {
    final UUID id = UUID.randomUUID();
    final ServerConfig config = new ServerConfig();

    final RabbitConnection rabbitConnection = new RabbitConnection(
      config.rabbitHost(),
      config.rabbitPort(),
      config.rabbitUsername(),
      config.rabbitPassword(),
      config.rabbitVirtualHost());

    final String queueName = RabbitConst.queueForCompiler(config.token());

    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
      final String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
      System.out.println(" [x] Received: " + message);
      final CompileRequestPayload payload = this.objectMapper.readValue(message, CompileRequestPayload.class);

      final ICompiler compiler = CompilerContext.instance().compiler();

      if (compiler == null) {
        System.out.println("Error");
      }

      try {
        this.securityCheck(payload.methodCode());
      } catch (final Exception exception) {
        log.warn("Error while checking security filters.", exception);
        System.out.println("error");
        return;
      }

      final Object[][] argsArray = payload.arguments().stream()
        .map(innerList -> innerList.toArray(new Object[0]))
        .toArray(Object[][]::new);


      try {
        final List<TestRunResult> results = compiler.compileAndRun(payload.methodCode(), argsArray);

        try (final Connection connection = rabbitConnection.newConnection();
             final Channel channel = connection.createChannel()) {

          final CompileResponsePayload responsePayload = new CompileResponsePayload(payload.requestId(), results);

          final String json = objectMapper.writeValueAsString(responsePayload);

          channel.basicPublish(
            RabbitConst.EXCHANGE_COMPILER_REPORT,
            RabbitConst.KEY_COMPILER_REPORT,
            null,
            json.getBytes(StandardCharsets.UTF_8)
          );
        }

        System.out.println(results);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }

    };

    CancelCallback cancelCallback = consumerTag -> {
      System.out.println(" [x] Consumer cancelled: " + consumerTag);
    };


    final Channel channel = rabbitConnection.newConnection().createChannel();

    channel.queueDeclare(queueName, true, false, false, null);
    channel.basicConsume(queueName, true, deliverCallback, cancelCallback);

    configureHeartbeat(rabbitConnection, id, config);
  }


  private void securityCheck(final String methodCode) throws Exception {
    final List<ISecurityFilter> securityFilters = CompilerContext.instance().securityFilters();

    if (securityFilters == null || securityFilters.isEmpty()) {
      log.warn("No security filters found... Thats not good :c");
    }

    for (final ISecurityFilter securityFilter : securityFilters) {
      if (securityFilter.check(methodCode)) {
        continue;
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

