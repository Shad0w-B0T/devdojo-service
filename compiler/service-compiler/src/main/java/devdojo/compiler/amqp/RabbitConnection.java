package devdojo.compiler.amqp;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.*;
import java.util.concurrent.TimeoutException;

public final class RabbitConnection {

  private final ConnectionFactory connectionFactory;

  public RabbitConnection(final ConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  public RabbitConnection(final String host,
                          final int port,
                          final String username,
                          final String password,
                          final String virtualHost) {
    this.connectionFactory = new ConnectionFactory();
    this.connectionFactory.setHost(host);
    this.connectionFactory.setPort(port);
    this.connectionFactory.setUsername(username);
    this.connectionFactory.setPassword(password);
    this.connectionFactory.setVirtualHost(virtualHost);
  }

  public Connection newConnection() throws IOException, TimeoutException {
    return this.connectionFactory.newConnection();
  }
}
