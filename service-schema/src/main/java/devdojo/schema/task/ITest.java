package devdojo.schema.task;

import java.util.List;

public interface ITest {

  List<? extends ITestArgument> input();

  ITestArgument output();

}
