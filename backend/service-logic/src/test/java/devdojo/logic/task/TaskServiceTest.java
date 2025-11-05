package devdojo.logic.task;

import devdojo.logic.database.task.TaskEntity;
import devdojo.logic.database.task.TaskRepository;
import devdojo.logic.data.TestData;
import devdojo.logic.language.ILanguageService;
import devdojo.schema.exception.database.DatabaseException;
import devdojo.schema.exception.database.NotFoundException;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;

@DisplayName("TaskService Unit Tests")
@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

  @Mock
  private TaskRepository taskRepository;
  @Mock
  private ILanguageService languageService;

  @InjectMocks
  private TaskService cut;


  @Nested
  @DisplayName("GetTasks Unit Tests")
  class GetTasks {

    @Test
    @DisplayName("GetTasks - Returns every task without specifying language")
    void getAllTasks() {
      final List<TaskEntity> expected = TestData.TASK_ENTITIES_TOTAL;
      when(taskRepository.findAll()).thenReturn(expected);

      final List<TaskEntity> result = cut.getTasks(null);

      assertEquals(expected, result);
      assertEquals(result.size(), 4);
      verify(taskRepository).findAll();
    }

    @Test
    @DisplayName("GetTasks - Return only tasks of specific language")
    void getTasksJava() {

      final List<TaskEntity> expectedTasks = TestData.TASK_ENTITIES_JAVA;
      when(taskRepository.findAllByLanguageEntity(any())).thenReturn(expectedTasks);

      final List<TaskEntity> result = cut.getTasks(TestData.GET_TASKS);
      assertEquals(expectedTasks, result);
      assertEquals(result.size(), 3);
      verify(taskRepository).findAllByLanguageEntity(any());
    }
  }


  @Nested
  @DisplayName("GetTask Unit Tests")
  class GetTask {

    @DisplayName("GetTask - Returns task")
    @Test
    void getTask() {
      final TaskEntity expected = TestData.TASK_ENTITIES_TOTAL.getFirst();
      when(taskRepository.findById(any())).thenReturn(Optional.of(TestData.TASK_ENTITY_JAVA_1));

      final TaskEntity result = cut.getTask(TestData.GET_TASK);

      assertEquals(expected, result);
      assertDoesNotThrow(() -> cut.getTask(TestData.GET_TASK));
    }

    @Test
    @DisplayName("GetTask - Throws NotFoundException")
    void getTaskNotFound() {
      assertThrows(NotFoundException.class, () -> cut.getTask(TestData.GET_TASK));
    }

  }


  @Nested
  @DisplayName("CreateTask Unit Tests")
  class CreateTask {

    @Test
    @DisplayName("CreateTask - Creates a task with correct values")
    void createTask() {
      final TaskEntity expected = TestData.TASK_ENTITY_PYTHON_1;

      when(taskRepository.save(any())).thenReturn(expected);

      final TaskEntity result = cut.createTask(TestData.CREATE_TASK);

      assertEquals(expected, result);
      assertEquals(expected.name(), result.name());
      assertEquals(expected.description(), result.description());
      assertEquals(expected.difficulty(), result.difficulty());
      assertEquals(expected.template(), result.template());
      assertEquals(expected.complete(), result.complete());
      assertDoesNotThrow(() -> cut.createTask(TestData.CREATE_TASK));
    }

    @Test
    @DisplayName("CreateTask - Throws NullPointerException")
    void createTaskNull() {
      assertThrows(NullPointerException.class, () -> cut.createTask(null));
    }
  }


  @Nested
  @DisplayName("UpdateTask Unit Tests")
  class UpdateTask {

    @Test
    @DisplayName("UpdateTask - Updates a Task correctly")
    void updateTask() {
      final TaskEntity expected = TestData.UPDATED_ENTITY;

      when(taskRepository.findById(any())).thenReturn(Optional.of(expected));
      when(taskRepository.save(any())).thenReturn(expected);

      final TaskEntity result = cut.updateTask(TestData.UPDATE_TASK);

      assertEquals(expected, result);
      assertEquals(expected.name(), result.name());
      assertEquals(expected.description(), result.description());
      assertEquals(expected.difficulty(), result.difficulty());
      assertEquals(expected.template(), result.template());
      assertEquals(expected.complete(), result.complete());
      assertDoesNotThrow(() -> cut.updateTask(TestData.UPDATE_TASK));
    }

    @Test
    @DisplayName("UpdateTask - Throws NotFoundException")
    void updateTaskNotFound() {
      assertThrows(NotFoundException.class, () -> cut.updateTask(TestData.UPDATE_TASK_ID_NULL));
    }


  }


  @Nested
  @DisplayName("DeleteTask Unit Tests")
  class DeleteTask {

    @Test
    @DisplayName("DeleteTask - Deletes a task")
    void deleteTask() {
    }
  }


  @Nested
  @DisplayName("AppendUnitTest Unit Tests")
  class AppendUnitTest {

    @Test
    @DisplayName("appends unit test to task and return updated task")
    void appendUnit() {
      final TaskEntity expected = TestData.TASK_ENTITY_PYTHON_1;

      when(taskRepository.findById(any())).thenReturn(Optional.of(expected));
      when(taskRepository.save(any())).thenReturn(expected);

      final TaskEntity result = cut.appendUnitTest(TestData.APPEND_TEST_TASK);

      assertEquals(expected, result);
      assertFalse(result.testEntities().isEmpty());
      assertEquals(1, result.testEntities().size());
      verify(taskRepository).findById(any());
      verify(taskRepository).save(expected);
    }


    @Test
    @DisplayName("Throws DatabaseException when saving task fails")
    void appendUnitThrowDatabaseException() {
      final TaskEntity task = TestData.TASK_ENTITY_PYTHON_1;

      when(taskRepository.findById(any())).thenReturn(Optional.of(task));
      when(taskRepository.save(any())).thenThrow(new DataAccessResourceFailureException("DB down"));

      final DatabaseException exception = assertThrows(
        DatabaseException.class,
        () -> cut.appendUnitTest(TestData.APPEND_TEST_TASK)
      );

      assertEquals("Failed to append unit test in database", exception.getMessage());
    }
  }
}
