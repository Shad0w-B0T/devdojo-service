package devdojo.logic.database;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class DatabaseSpringTest {

  public static void main(final String[] args) {
    new SpringApplicationBuilder(DatabaseSpringTest.class)
      .web(WebApplicationType.NONE)
      .run(args);
  }
}
