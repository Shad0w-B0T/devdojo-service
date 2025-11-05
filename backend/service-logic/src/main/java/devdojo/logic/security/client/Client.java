package devdojo.logic.security.client;

import devdojo.schema.user.IUser;

public class Client extends AbstractClient {

  public Client(final String userAgent,
                final IUser user) {
    super(userAgent, user);
  }
}
