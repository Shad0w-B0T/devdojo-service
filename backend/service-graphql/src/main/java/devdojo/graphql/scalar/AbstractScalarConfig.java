package devdojo.graphql.scalar;

import graphql.scalars.ExtendedScalars;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Slf4j
public abstract class AbstractScalarConfig implements IScalarConfig {
  /**
   * {@inheritDoc}
   */
  @Bean
  @Override
  public @NotNull RuntimeWiringConfigurer runtimeWiringConfigurer() {
    final RuntimeWiringConfigurer configurer =
      wiringBuilder -> wiringBuilder
        .scalar(ExtendedScalars.GraphQLLong)
        .scalar(ExtendedScalars.Json);
    log.info("The additional scalars for graphql were loaded by the configuration.");
    return configurer;
  }
}
