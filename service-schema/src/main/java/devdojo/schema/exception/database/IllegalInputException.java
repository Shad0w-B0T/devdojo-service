package devdojo.schema.exception.database;

import devdojo.schema.exception.AbstractException;

public class IllegalInputException extends AbstractException {
  public IllegalInputException(final String message) {
    super(message);
  }
}
