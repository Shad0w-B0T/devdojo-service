package devdojo.graphql.error;

import devdojo.schema.exception.database.NotFoundException;
import devdojo.schema.exception.permissions.UnauthorizedException;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.execution.ErrorType;

@Slf4j
public abstract class AbstractErrorController implements IErrorController {

  @GraphQlExceptionHandler(NotFoundException.class)
  @Override
  public GraphQLError notFoundException(@NotNull final NotFoundException notFoundException,
                                        @NotNull final DataFetchingEnvironment dataFetchingEnvironment) {
    return new ErrorBuilder(notFoundException, ErrorType.NOT_FOUND, dataFetchingEnvironment).build();
  }

  @GraphQlExceptionHandler(UnauthorizedException.class)
  @Override
  public @NotNull GraphQLError unauthorizedException(@NotNull final UnauthorizedException unauthorizedException,
                                                     @NotNull final DataFetchingEnvironment dataFetchingEnvironment) {

    return new ErrorBuilder(unauthorizedException, ErrorType.UNAUTHORIZED, dataFetchingEnvironment)
      .message("Not authorized")
      .build();
  }

  /**
   * {@inheritDoc}
   */
  @GraphQlExceptionHandler(Throwable.class)
  @Override
  public @NotNull GraphQLError throwable(@NotNull final Throwable exception,
                                         @NotNull final DataFetchingEnvironment dataFetchingEnvironment) {
    log.warn("An internal error occurred.", exception);
    return new ErrorBuilder(exception, ErrorType.INTERNAL_ERROR, dataFetchingEnvironment)
      .message("An unexpected error occurred.")
      .build();
  }

  @Slf4j
  private static class ErrorBuilder {
    /**
     * The builder instance for configuring the GraphQL error response.
     */
    private final @NotNull GraphQLError.Builder<?> builder;

    /**
     * Map for storing custom extension fields to be added to the error response.
     */
    private Map<String, Object> extensions;

    /**
     * Constructs an instance of AbstractErrorBuilder, initializing the error builder
     * with the exception details, error type, and data fetching environment.
     *
     * @param throwable   The exception to be represented by the error. Must not be null.
     * @param errorType   The type of the error for categorization. Must not be null.
     * @param environment The environment details of the GraphQL execution context. Must not be null.
     */
    public ErrorBuilder(@NotNull final Throwable throwable,
                        @NotNull final ErrorType errorType,
                        @NotNull final DataFetchingEnvironment environment) {
      log.info("Error advice for {} exception with response type {}.",
        throwable.getClass().getName(),
        errorType.name());

      this.builder = GraphqlErrorBuilder.newError()
        .errorType(errorType)
        .message(throwable.getMessage())
        .path(environment.getExecutionStepInfo().getPath())
        .location(environment.getField().getSourceLocation());
    }


    /**
     * Sets a custom error message with optional formatting arguments.
     *
     * @param message    The custom message for the error. Can be null.
     * @param formatArgs Optional arguments for formatting the message. Can be null.
     * @return The current instance of IErrorBuilder.
     */
    public @NotNull ErrorBuilder message(final String message,
                                         final Object... formatArgs) {
      this.builder.message(message, formatArgs);
      return this;
    }

    /**
     * Adds a custom field to the error's extensions, using a key-value pair.
     *
     * @param key    The key for the extension field. Can be null.
     * @param object The value for the extension field. Can be null.
     * @return The current instance of IErrorBuilder.
     */
    public @NotNull ErrorBuilder extensionField(final String key,
                                                final Object object) {
      if (key == null || object == null) {
        return this;
      }

      if (this.extensions == null) {
        this.extensions = new HashMap<>();
      }

      this.extensions.put(key, object);
      return this;
    }

    /**
     * Builds and returns the configured GraphQL error, adding any extension fields
     * if they are defined.
     *
     * @return The constructed GraphQLError instance.
     */
    public @NotNull GraphQLError build() {
      if (this.extensions != null) {
        this.builder.extensions(this.extensions);
      }
      return this.builder.build();
    }
  }
}
