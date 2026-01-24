package devdojo.logic.data;

import devdojo.logic.database.language.LanguageEntity;
import devdojo.logic.database.task.TaskEntity;
import devdojo.logic.task.ITaskService;
import devdojo.schema.task.ArgumentType;
import devdojo.schema.task.Difficulty;
import java.util.List;
import java.util.UUID;

public class TestData {

  public static final String TASK_ID = "72bf18f6-bbdf-4efa-b0e4-fe2b96b717b4";
  public static final String LANGUAGE_ID = "f2378c02-4a78-4f39-a9b8-d040f6a2001d";
  private static final List<? extends ITaskService.TestData> LANGUAGE_UNIT_TESTS = List.of();
  public static final LanguageEntity LANGUAGE_ENTITY_JAVA =
    new LanguageEntity("123456789", "Java", "Token for Java", true);
  public static final LanguageEntity LANGUAGE_ENTITY_PYTHON =
    new LanguageEntity("147258369", "Python", "Token for Python", true);
  public static final LanguageEntity LANGUAGE_ENTITY_JAVASCRIPT =
    new LanguageEntity("987654321", "JavaScript", "Token for JavaScript", true);
  public static final TaskEntity TASK_ENTITY_JAVA_1 = new TaskEntity(
    LANGUAGE_ENTITY_JAVA,
    "String",
    "Concat two strings",
    Difficulty.EASY,
    "public String {}",
    "public String { return 'Hello World!';}",
    true
  );
  public static final TaskEntity TASK_ENTITY_JAVA_2 = new TaskEntity(
    LANGUAGE_ENTITY_JAVA,
    "Integer",
    "Sum two integers",
    Difficulty.MEDIUM,
    "public int {}",
    "public int {return 4;}",
    true
  );
  public static final TaskEntity TASK_ENTITY_JAVA_3 = new TaskEntity(
    LANGUAGE_ENTITY_JAVA,
    "If Statement",
    "Return if sum > 10",
    Difficulty.HARD,
    "public int {}",
    "public int {return 5;}",
    true
  );
  public static final TaskEntity TASK_ENTITY_PYTHON_1 = new TaskEntity(
    LANGUAGE_ENTITY_PYTHON,
    "Sum two integers",
    "return sum",
    Difficulty.EASY,
    "print()",
    "print(1 + 1)",
    true
  );

  public static final TaskEntity UPDATED_ENTITY = new TaskEntity(
    LANGUAGE_ENTITY_PYTHON,
    "Update the entity",
    "Updated Task",
    Difficulty.HARD,
    "print()",
    "print('Updated!')",
    true
  );

  public static final List<TaskEntity> TASK_ENTITIES_JAVA = List.of(
    TASK_ENTITY_JAVA_1,
    TASK_ENTITY_JAVA_2,
    TASK_ENTITY_JAVA_3
  );

  public static final List<TaskEntity> TASK_ENTITIES_TOTAL = List.of(
    TASK_ENTITY_JAVA_1,
    TASK_ENTITY_JAVA_2,
    TASK_ENTITY_JAVA_3,
    TASK_ENTITY_PYTHON_1
  );


  public static final ITaskService.GetTasksByLangauge GET_TASKS = new ITaskService.GetTasksByLangauge(LANGUAGE_ID);

  public static final ITaskService.GetTask GET_TASK = new ITaskService.GetTask(TASK_ID);

  public static final ITaskService.CreateTask CREATE_TASK = new ITaskService.CreateTask(
    LANGUAGE_ID,
    "Sum two integers",
    "return sum",
    Difficulty.EASY,
    "print()",
    "print(1 + 1)",
    LANGUAGE_UNIT_TESTS,
    true,
    List.of("python", "java")
  );

  public static final ITaskService.UpdateTask UPDATE_TASK = new ITaskService.UpdateTask(
    TASK_ID,
    "Update the entity",
    "Updated Task",
    Difficulty.HARD,
    "print()",
    "print('Updated!')",
    LANGUAGE_UNIT_TESTS,
    true,
    List.of("javascript")
  );

  public static final ITaskService.UpdateTask UPDATE_TASK_ID_NULL = new ITaskService.UpdateTask(
    null,
    "Update the entity",
    "Updated Task",
    Difficulty.HARD,
    "print()",
    "print('Updated!')",
    LANGUAGE_UNIT_TESTS,
    true,
    List.of("javascript")
  );

  public static final ITaskService.DeleteTask DELETE_TASK = new ITaskService.DeleteTask(TASK_ID);


  private static final ITaskService.TestArgumentData TEST_ARGUMENT_DATA_RECORD = new ITaskService.TestArgumentData(
    ArgumentType.INPUT,
    "",
    List.of()
  );

  private static final ITaskService.TestData TEST_DATA_RECORD = new ITaskService.TestData(
    List.of(),
    TEST_ARGUMENT_DATA_RECORD
  );


  public static final ITaskService.AppendTest APPEND_TEST_TASK = new ITaskService.AppendTest(
    TASK_ID,
    TEST_DATA_RECORD
  );


  public static final ITaskService.AppendTest APPEND_TEST_TASK_EXPECTED = new ITaskService.AppendTest(
    TASK_ID,
    new ITaskService.TestData(
      List.of(),
      new ITaskService.TestArgumentData(
        ArgumentType.INPUT,
        "",
        List.of()
      )
    )
  );
}
