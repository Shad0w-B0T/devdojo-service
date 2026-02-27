package devdojo.compiler.heartbeat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import devdojo.compiler.amqp.RabbitConnection;
import devdojo.schema.amqp.HeartbeatPayload;
import devdojo.schema.amqp.RabbitConst;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class HeartbeatProcess implements IHeartbeatProcess {

  private final int PERIOD = 5;

  private final RabbitConnection rabbitConnection;
  private final ObjectMapper objectMapper;

  private final String compilerId;
  private final String languageKey;
  private long task = 0;

  private ScheduledExecutorService scheduler;

  public HeartbeatProcess(final RabbitConnection rabbitConnection,
                          final String compilerId,
                          final String languageKey) {
    this.rabbitConnection = rabbitConnection;
    this.compilerId = compilerId;
    this.languageKey = languageKey;
    this.objectMapper = new ObjectMapper();
  }

  @Override
  public void start() {
    this.scheduler = Executors.newSingleThreadScheduledExecutor();
    this.scheduler.scheduleAtFixedRate(this::sendHeartbeat, 0, PERIOD, TimeUnit.SECONDS);

    log.info("Heartbeat process started (compilerId={}, languageKey={}, period={}s)",
      compilerId, languageKey, PERIOD);
  }

  @Override
  public void stop() {
    if (scheduler != null) {
      scheduler.shutdown();
      log.info("Heartbeat process stopped (compilerId={})", compilerId);
    } else {
      log.warn("Heartbeat process stop requested but scheduler was null (compilerId={})",
        compilerId);
    }
  }

  @Override
  public void incrementTask() {
    this.task++;
    log.debug("Task incremented. Current task count={} (compilerId={})",
      task, compilerId);
  }

  private void sendHeartbeat() {
    try {
      final HeartbeatPayload payload = new HeartbeatPayload(this.compilerId,
        this.languageKey,
        getCpuUsage(),
        Runtime.getRuntime().availableProcessors(),
        getUsedMemoryInMb(),
        getTotalMemoryInMb(),
        this.task);

      log.debug("Preparing heartbeat (compilerId={}, tasks={}, cpu={}%, usedMem={}MB)",
        compilerId,
        task,
        payload.cpuUsage(),
        payload.memoryUsageMb());

      try (final Connection connection = rabbitConnection.newConnection();
           final Channel channel = connection.createChannel()) {

        channel.exchangeDeclare(RabbitConst.EXCHANGE_HEARTBEAT, "topic", true);
        channel.basicPublish(RabbitConst.EXCHANGE_HEARTBEAT, RabbitConst.KEY_HEARTBEAT,
          null, this.objectMapper.writeValueAsBytes(payload));

        log.debug("Heartbeat published to exchange='{}' with key='{}' (compilerId={})",
          RabbitConst.EXCHANGE_HEARTBEAT,
          RabbitConst.KEY_HEARTBEAT,
          compilerId);

      } catch (IOException | TimeoutException e) {
        log.error("RabbitMQ error while sending heartbeat (compilerId={})",
          compilerId, e);
        throw new RuntimeException(e);
      }

    } catch (Exception e) {
      log.warn("Failed to send heartbeat to infrastructure (compilerId={})",
        compilerId, e);
    }
  }

  private final static double PERCENTAGE_CONST = 100.;
  private final static double MEGABYTES_CONST = 1024.;

  private double getCpuUsage() {
    final OperatingSystemMXBean osBean =
      (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    double load = osBean.getSystemLoadAverage();
    return (load >= 0) ? load * PERCENTAGE_CONST : -1.0;
  }

  private double getUsedMemoryInMb() {
    long used = Runtime.getRuntime().totalMemory()
      - Runtime.getRuntime().freeMemory();
    return used / (MEGABYTES_CONST * MEGABYTES_CONST);
  }

  private double getTotalMemoryInMb() {
    return Runtime.getRuntime().maxMemory()
      / (MEGABYTES_CONST * MEGABYTES_CONST);
  }
}