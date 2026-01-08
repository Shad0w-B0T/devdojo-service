package devdojo.rest.controller;

import devdojo.rest.exception.ErrorResponse;
import jakarta.validation.constraints.NotNull;

public interface IErrorController {

  @NotNull(message = "Response must not be null")
  ErrorResponse handleUnknownPath();

}
