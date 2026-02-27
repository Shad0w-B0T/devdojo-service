package devdojo.logic.database.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import devdojo.logic.database.BaseJpaTest;
import devdojo.logic.database.language.LanguageEntity;
import devdojo.logic.database.language.LanguageRepository;
import devdojo.logic.database.user.UserEntity;
import devdojo.logic.database.user.UserRepository;
import devdojo.schema.task.Difficulty;
import devdojo.schema.task.TaskProgressState;
import devdojo.schema.user.UserRole;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("TaskProgressRepository Tests")
final class TaskProgressRepositoryTest extends BaseJpaTest {

  @Autowired
  private TaskProgressRepository cut;

  @Autowired
  private LanguageRepository languageRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TaskRepository taskRepository;

  private TaskEntity task1;
  private TaskEntity task2;

  private LanguageEntity languageEntity;

  private TaskProgressEntity taskProgress1;
  private TaskProgressEntity taskProgress2;
  private TaskProgressEntity taskProgress3;
  private TaskProgressEntity taskProgress4;

  private UserEntity user1;
  private UserEntity user2;
  private UserEntity user3;


  private final Date DATE = Date.from(Instant.parse("2025-04-20T12:00:00Z"));

  @BeforeEach
  void setUp() {
    this.user1 = new UserEntity(
      "4624cae4-69bc-450e-8fc4-df5864aca748",
      "rick@hdm-stuttgart.de",
      "Rick",
      UserRole.ADMIN,
      UserRole.ADMIN,
      true,
      this.DATE,
      false
    );
    this.user2 = new UserEntity(
      "14997803-9298-471b-87ca-403520169ede",
      "morty@hdm-stuttgart.de",
      "Morty",
      UserRole.STUDENT,
      UserRole.STUDENT,
      true,
      this.DATE,
      false
    );
    this.user3 = new UserEntity(
      "e25a0f7f-6f46-412c-ac86-4f50bc5faed4",
      "summer@hdm-stuttgart.de",
      "Summer",
      UserRole.GUEST,
      UserRole.GUEST,
      true,
      this.DATE,
      false
    );
    this.userRepository.saveAll(List.of(this.user1, this.user2, this.user3));

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
      "public String { return 'Hello World!'; }",
      true
    );
    this.taskRepository.saveAll(List.of(this.task1, this.task2));

    this.taskProgress1 = new TaskProgressEntity(
      this.user1,
      this.task1,
      "Wubba Lubba dub-dub",
      TaskProgressState.FINISHED,
      100
    );
    this.taskProgress2 = new TaskProgressEntity(
      this.user1,
      this.task2,
      "Wubba Lubba dub-dub",
      TaskProgressState.FINISHED,
      100
    );
    this.taskProgress3 = new TaskProgressEntity(
      this.user2,
      this.task2,
      "Oh geez",
      TaskProgressState.RUNNING,
      50
    );
    this.taskProgress4 = new TaskProgressEntity(
      this.user3,
      this.task1,
      "Uhm like so my vibe",
      TaskProgressState.NOT_STARTED,
      0
    );
  }


  @Test
  @DisplayName("FindByUserEntity - Returns two because User has two completed tasks")
  void testFindByUserEntity() {
    final TaskProgressEntity taskProgress1 = this.taskProgress1;
    final TaskProgressEntity taskProgress2 = this.taskProgress2;
    this.cut.saveAll(List.of(taskProgress1, taskProgress2));

    final List<TaskProgressEntity> result = this.cut.findByUserEntity(this.user1);

    assertNotNull(result);
    assertEquals(2, result.size());
  }


  @Test
  @DisplayName("FindByUserEntity - Returns 0 because User did not complete any task")
  void noCompletedTasks() {
    final List<TaskProgressEntity> result = this.cut.findByUserEntity(this.user3);
    assertNotNull(result);
    assertEquals(0, result.size(), "The user has no completed tasks");
  }

  @Test
  @DisplayName("FindByUserEntity - Returns correct values")
  void returnCorrectValues() {
    final TaskProgressEntity taskProgress1 = this.taskProgress1;
    final TaskProgressEntity taskProgress2 = this.taskProgress2;
    this.cut.saveAll(List.of(taskProgress1, taskProgress2));

    final List<TaskProgressEntity> result = this.cut.findByUserEntity(this.user1);

    assertEquals("Wubba Lubba dub-dub", result.getFirst().input());
    assertEquals(TaskProgressState.FINISHED, result.getFirst().state());
    assertEquals(100, result.getFirst().score());
  }


  @Test
  @DisplayName("FindByUserEntityAndTaskEntity - Returns correct TaskProgressEntity for specific user and task")
  void testFindByUserEntityAndTaskEntityReturnsCorrectProgress() {
    final TaskProgressEntity taskProgressRick = this.taskProgress1;
    this.cut.save(taskProgressRick);

    final Optional<TaskProgressEntity> result = this.cut.findByUserEntityAndTaskEntity(this.user1, this.task1);

    assertNotNull(result);
    assertEquals(1, result.stream().toList().size());
    assertEquals("Wubba Lubba dub-dub", result.get().input());
    assertEquals(TaskProgressState.FINISHED, result.get().state());
    assertEquals(100, result.get().score());
  }

  @Test
  @DisplayName("FindByUserEntityAndTaskEntity - Returns empty Optional if no TaskProgressEntity exists")
  void testFindByUserEntityAndTaskEntity() {
    final Optional<TaskProgressEntity> result = this.cut.findByUserEntityAndTaskEntity(this.user1, this.task1);
    assertFalse(result.isPresent());
  }

  @Test
  @DisplayName("FindByUserEntityAndTaskEntity - Returns empty Optional if user is null")
  void testFindByUserEntityAndTaskEntityNullUser() {
    final Optional<TaskProgressEntity> result = this.cut.findByUserEntityAndTaskEntity(null, this.task1);
    assertFalse(result.isPresent());
  }

  @Test
  @DisplayName("FindByUserEntityAndTaskEntity - Returns empty Optional if task is null")
  void testFindByUserEntityAndTaskEntityNullTask() {
    final Optional<TaskProgressEntity> result = this.cut.findByUserEntityAndTaskEntity(this.user1, null);
    assertFalse(result.isPresent());
  }

  @Test
  @DisplayName("FindByUserEntityAndTaskEntity - Returns empty Optional if both user and task are null")
  void testFindByUserEntityAndTaskEntityNullUserAndTask() {
    final Optional<TaskProgressEntity> result = this.cut.findByUserEntityAndTaskEntity(null, null);
    assertFalse(result.isPresent());
  }


  @Test
  @DisplayName("FindTasksWithoutProgress - Returns tasks without progress for the given user and language")
  void findTasksWithoutProgressTest() {
    final TaskProgressEntity taskProgress = this.taskProgress4;
    this.cut.save(taskProgress);

    final List<TaskEntity> result = this.cut.findTasksWithoutProgress(this.user3.id(), this.languageEntity.id());

    assertNotNull(result);
    assertEquals(1, result.size());
    assertNotNull(result.getFirst());
    assertNotEquals(taskProgress.taskEntity().id(), result.getFirst().id());
  }


  @Test
  @DisplayName("Returns tasks without progress for the given user")
  void findTasksWithoutProgress() {
    final TaskProgressEntity taskProgress = this.taskProgress4;
    this.cut.save(taskProgress);

    final List<TaskEntity> result = this.cut.findTasksWithoutProgress(this.user3.id());

    assertNotNull(result);
    assertEquals(1, result.size());
    assertNotNull(result.getFirst());
  }


  @Test
  @DisplayName("FindTasksWithProgressByState - Returns tasks with matching progress state for the given user")
  void findTasksWithProgressByStateTest() {
    final TaskProgressEntity taskProgress1 = this.taskProgress1;
    final TaskProgressEntity taskProgress2 = this.taskProgress2;
    this.cut.saveAll(List.of(taskProgress1, taskProgress2));

    final List<TaskEntity> result = this.cut.findTasksWithProgressByState(this.user1.id(), TaskProgressState.FINISHED);

    assertNotNull(result);
    assertEquals(2, result.size());
  }

  @Test
  @DisplayName("FindTasksWithProgressByState - Returns empty list when no tasks match the state for the given user")
  void findTasksWithProgressByStateNoMatchingStateTest() {

    final TaskProgressEntity taskProgress = this.taskProgress1;
    this.cut.save(taskProgress);

    final List<TaskEntity> result = this.cut.findTasksWithProgressByState(this.user1.id(), TaskProgressState.RUNNING);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }


  @Test
  @DisplayName("FindTasksWithProgressByState - Return tasks with progress matching the specified state for the user")
  void findTasksWithProgressByStateTests() {
    final TaskProgressEntity taskProgress1 = this.taskProgress1;
    final TaskProgressEntity taskProgress2 = this.taskProgress2;
    this.cut.saveAll(List.of(taskProgress1, taskProgress2));

    final List<TaskEntity> result = this.cut.findTasksWithProgressByState(this.user1.id(), TaskProgressState.FINISHED);

    assertNotNull(result);
    assertEquals(2, result.size());
    assertFalse(result.isEmpty());
  }
}
