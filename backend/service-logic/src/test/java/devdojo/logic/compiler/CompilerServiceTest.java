package devdojo.logic.compiler;

import com.fasterxml.jackson.databind.ObjectMapper;
import devdojo.logic.cache.compiler.CompilerTaskRepository;
import devdojo.logic.cache.compiler.CompilerTaskResultRepository;
import devdojo.logic.database.compiler.CompilerHeartbeatRepository;
import devdojo.logic.database.compiler.CompilerRepository;
import devdojo.logic.database.language.LanguageRepository;
import devdojo.logic.task.ITaskService;
import devdojo.logic.task.ITasksProgressService;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@DisplayName("CompilerService tests")
class CompilerServiceTest {

  @Mock
  private ObjectMapper objectMapper;
  @Mock
  private LanguageRepository languageRepository;
  @Mock
  private ITaskService taskService;
  @Mock
  private ITasksProgressService tasksProgressService;
  @Mock
  private CompilerRepository compilerRepository;
  @Mock
  private CompilerHeartbeatRepository compilerHeartbeatRepository;
  @Mock
  private CompilerTaskRepository compilerTaskRepository;
  @Mock
  private CompilerTaskResultRepository compilerTaskResultRepository;

  @InjectMocks
  private CompilerService compilerService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  @DisplayName("should initialize AbstractCompilerService with HeartbeatRepository")
  void shouldInitializeWithHeartbeatRepository() {
    assertDoesNotThrow(
      () -> new CompilerService(this.objectMapper,
        this.languageRepository,
        this.taskService,
        this.tasksProgressService,
        this.compilerRepository,
        this.compilerHeartbeatRepository,
        this.compilerTaskRepository,
        this.compilerTaskResultRepository));
  }

  /*
  @Test
  @DisplayName("should update and save heartbeat entity")
  void shouldUpdateAndSaveHeartbeat() {
    final UUID compilerId = UUID.randomUUID();
    final ICompilerService.UpdateHeartbeat update = new ICompilerService.UpdateHeartbeatRecord(
      compilerId, "go", 30.5, 6, 700.0, 1600.0, 50L
    );

    final HeartbeatEntity expected = new HeartbeatEntity(
      compilerId, "go", 30.5, 6, 700.0, 1600.0, 50L, System.currentTimeMillis()
    );

    when(this.heartbeatRepository.save(any(HeartbeatEntity.class))).thenReturn(expected);

    final HeartbeatEntity result = this.compilerService.updateHeartbeat(update);

    assertNotNull(result);
    assertEquals("go", result.languageToken());

    verify(this.heartbeatRepository).save(any(HeartbeatEntity.class));
  }


  @Test
  @DisplayName("should throw DatabaseException when save fails")
  void shouldThrowDatabaseExceptionOnSaveFailure() {
    final ICompilerService.UpdateHeartbeat update = new ICompilerService.UpdateHeartbeatRecord(
      UUID.randomUUID(), "rust", 42.0, 8, 900.0, 1800.0, 99L
    );

    when(this.heartbeatRepository.save(any(HeartbeatEntity.class)))
      .thenThrow(new DatabaseException("DB failure"));

    assertThrows(DatabaseException.class, () -> this.compilerService.updateHeartbeat(update));
  }
   */
}
