package devdojo.schema.task;

import java.util.List;

public interface ITest {

  List<ITestArgument> input();

  ITestArgument output();

}
