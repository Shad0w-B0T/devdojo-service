package devdojo.compiler;

import devdojo.compiler.amqp.RabbitConnection;
import devdojo.compiler.heartbeat.HeartbeatProcess;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("HeartbeatProcessTest - Tests for the Heartbeat background process")
final class HeartbeatProcessTest {

  @Mock
  private RabbitConnection rabbitConnection;

  private HeartbeatProcess process;

  @BeforeEach
  void setUp() {

    this.process = new HeartbeatProcess(this.rabbitConnection, "id123", "java");
  }

  @Test
  @DisplayName("should start and stop the heartbeat process without exceptions")
  void shouldStartAndStopHeartbeatCleanly() {
    this.process.start();
    this.process.stop();
  }
}
