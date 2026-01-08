package devdojo.graphql.error;

import devdojo.schema.exception.database.NotFoundException;
import devdojo.schema.exception.permissions.UnauthorizedException;
import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.constraints.NotNull;

public interface IErrorController {

  @NotNull
  GraphQLError notFoundException(@NotNull final NotFoundException notFoundException,
                                 @NotNull final DataFetchingEnvironment dataFetchingEnvironment);

  @NotNull
  GraphQLError unauthorizedException(@NotNull final UnauthorizedException unauthorizedException,
                                     @NotNull final DataFetchingEnvironment dataFetchingEnvironment);

  /**
   * Handles a generic Throwable exception and returns a corresponding GraphQL error.
   *
   * @param exception               The Throwable to handle. Must not be null.
   * @param dataFetchingEnvironment The GraphQL environment details. Must not be null.
   * @return A GraphQLError representing the processed error.
   */
  @NotNull
  GraphQLError throwable(@NotNull final Throwable exception,
                         @NotNull final DataFetchingEnvironment dataFetchingEnvironment);
}
