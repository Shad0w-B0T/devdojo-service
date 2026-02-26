package devdojo.logic.database.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import devdojo.logic.database.BaseJpaTest;
import devdojo.logic.database.language.LanguageEntity;
import devdojo.logic.database.language.LanguageRepository;
import devdojo.schema.task.Difficulty;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("TaskRepository Tests")
final class TaskRepositoryTest extends BaseJpaTest {

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private LanguageRepository languageRepository;

  private LanguageEntity languageEntity;

  private TaskEntity task1;
  private TaskEntity task2;


  @BeforeEach
  void setUp() {
    this.languageEntity = new LanguageEntity("Java", "Token for Java", true);
    this.languageRepository.save(this.languageEntity);

    this.task1 = new TaskEntity(
      this.languageEntity,
      "Integer Example",
      "Sum two integers",
      Difficulty.MEDIUM,
      "public int {}",
      "public int {return 4;}",
      true
    );
    this.task2 = new TaskEntity(
      this.languageEntity,
      "String Example",
      "Concat two strings",
      Difficulty.EASY,
      "public String {}",
      "public String { return 'Hello World!';}",
      true
    );
  }

  @AfterEach
  void tearDown() {
    this.taskRepository.flush();
    this.taskRepository.deleteAll();
    this.languageRepository.flush();
    this.languageRepository.deleteAll();
  }


  @Test
  @DisplayName("Returns empty db")
  void returnsEmptyDb() {
    final long result = this.taskRepository.count();
    assertEquals(0, result);
  }


  @Test
  @DisplayName("Returns correct size of db")
  void returnsTwoDbEntries() {
    this.taskRepository.saveAll(List.of(task1, task2));

    final long result = this.taskRepository.count();

    assertEquals(2, result);
  }

  @Test
  @DisplayName("Saves and returns a Task")
  void returnsTaskCorrectly() {

    final TaskEntity taskEntity = task1;

    this.taskRepository.save(taskEntity);

    final Optional<TaskEntity> result = this.taskRepository.findById(taskEntity.id());

    assertNotNull(result);
    assertTrue(result.isPresent());
    assertEquals(result.get().languageEntity(), taskEntity.languageEntity());
    assertEquals(result.get().name(), taskEntity.name());
    assertEquals(result.get().description(), taskEntity.description());
    assertEquals(result.get().difficulty(), taskEntity.difficulty());
    assertEquals(result.get().template(), taskEntity.template());
    assertEquals(result.get().complete(), taskEntity.complete());
  }

  @Test
  @DisplayName("Returns List of TaskEntity")
  void returnsTaskListCorrectly() {
    final List<TaskEntity> taskEntityList = List.of(task1, task2);

    this.taskRepository.saveAll(List.of(task1, task2));

    this.taskRepository.findAllByLanguageEntity(this.languageEntity);

    assertNotNull(taskEntityList);
    assertEquals(2, taskEntityList.size());
  }

  @Test
  @DisplayName("Returns null when no LanguageEntity is present")
  void returnsNullWhenNoLanguageEntityPresent() {
    final List<TaskEntity> result = this.taskRepository.findAllByLanguageEntity(null);
    assertTrue(result.isEmpty());
  }

}
