package devdojo.graphql.scalar;

import jakarta.validation.constraints.NotNull;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

/**
 * Interface for configuring custom scalars in a GraphQL schema.
 * This interface provides a method to define a {@link RuntimeWiringConfigurer}
 * that allows the user to configure custom scalar types within the GraphQL runtime wiring.
 *
 * @see RuntimeWiringConfigurer
 */
public interface IScalarConfig {
  /**
   * Returns a {@link RuntimeWiringConfigurer} to configure custom scalar types.
   *
   * @return the {@link RuntimeWiringConfigurer} to configure custom scalars
   */
  @NotNull
  RuntimeWiringConfigurer runtimeWiringConfigurer();
}
