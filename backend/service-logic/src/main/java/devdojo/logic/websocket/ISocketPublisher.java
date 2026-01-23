package devdojo.logic.websocket;

import devdojo.schema.code.TestRun;

public interface ISocketPublisher {

  void notifyTaskUpdate(final String authId,
                        final TestRun testRun);

}
