package devdojo.compiler.heartbeat;


public interface IHeartbeatProcess {

  void start();

  void stop();

  void incrementTask();

}
