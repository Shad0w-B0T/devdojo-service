package devdojo.rest.controller;

import devdojo.rest.exception.ErrorResponse;
import java.time.Instant;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class AbstractCustomErrorController implements IErrorController {

  @RequestMapping(
    value = "/**",
    method = { RequestMethod.GET, RequestMethod.HEAD, RequestMethod.OPTIONS})
  @ResponseStatus(NOT_FOUND)
  public ErrorResponse handleUnknownPath() {
    return new ErrorResponse(
      "Endpoint not found.",
      NOT_FOUND,
      Instant.now().toString()
    );
  }
}

