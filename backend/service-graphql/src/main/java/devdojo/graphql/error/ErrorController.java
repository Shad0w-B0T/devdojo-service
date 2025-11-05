package devdojo.graphql.error;

import org.springframework.web.bind.annotation.ControllerAdvice;

/**
 * ControllerAdvice for handling GraphQL errors in a centralized manner.
 * Extends AbstractErrorController to inherit predefined error handling logic.
 */
@ControllerAdvice
public class ErrorController extends AbstractErrorController {
}
