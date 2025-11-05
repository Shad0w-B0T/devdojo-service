package devdojo.schema.amqp;

public interface RabbitConst {

  String EXCHANGE_HEARTBEAT = "devdojo.heartbeat.exchange";
  String QUEUE_HEARTBEAT = "devdojo.heartbeat.queue";
  String KEY_HEARTBEAT = "heartbeat";

  String EXCHANGE_COMPILER = "devdojo.compiler.exchange";
  String QUEUE_COMPILER_PREFIX = "devdojo.compiler.";
  String KEY_COMPILER_PREFIX = "devdojo.compiler.";

  String EXCHANGE_COMPILER_REPORT = "devdojo.compiler.report.exchange";
  String QUEUE_COMPILER_REPORT = "devdojo.compiler.report.queue";
  String KEY_COMPILER_REPORT = "devdojo.compiler.report";

  static String queueForCompiler(final String compilerKey) {
    return QUEUE_COMPILER_PREFIX + compilerKey;
  }

  static String routingKeyForCompiler(final String compilerKey) {
    return KEY_COMPILER_PREFIX + compilerKey;
  }
}
