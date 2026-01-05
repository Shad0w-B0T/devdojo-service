package devdojo.graphql.controller.compiler;

import devdojo.graphql.input.compiler.GetTestRun;
import devdojo.logic.compiler.ICompilerService;
import devdojo.logic.database.compiler.CompilerEntity;
import devdojo.logic.database.language.LanguageEntity;
import devdojo.schema.code.TestError;
import devdojo.schema.code.TestRun;
import devdojo.schema.code.TestRunResultChecked;
import devdojo.schema.compiler.CompilerResourceAnalytic;
import devdojo.schema.exception.database.NotFoundException;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;

public abstract class AbstractCompilerController implements ICompilerController {

  private final ICompilerService compilerService;

  protected AbstractCompilerController(
    @NotNull(message = "CompilerService must not be null") final ICompilerService compilerService) {
    this.compilerService = compilerService;
  }

  @QueryMapping("compilers")
  @Override
  public List<CompilerEntity> compilers() {
    return this.compilerService.getCompilers();
  }

  @QueryMapping("compiler")
  @Override
  public CompilerEntity compiler(@Argument("input") final ICompilerService.GetCompiler input) {
    return this.compilerService.getCompiler(input);
  }

  @QueryMapping("testRun")
  @Override
  public TestRun testRun(@Argument("input") final GetTestRun input) {
    try {
      return this.compilerService.getCompilerTask(new ICompilerService.GetCompilerTask(input.runId())).testRun();
    } catch (final NotFoundException exception) {
      return null;
    }
  }

  @SchemaMapping(typeName = "Compiler")
  @Override
  public UUID id(final CompilerEntity compilerEntity) {
    return compilerEntity.compilerId();
  }

  @SchemaMapping(typeName = "Compiler")
  @Override
  public int cpuCores(final CompilerEntity compilerEntity) {
    return compilerEntity.cpuCores();
  }

  @SchemaMapping(typeName = "Compiler")
  @Override
  public double memoryTotal(final CompilerEntity compilerEntity) {
    return compilerEntity.memoryTotal();
  }

  @SchemaMapping(typeName = "Compiler")
  @Override
  public LanguageEntity language(final CompilerEntity compilerEntity) {
    return compilerEntity.languageEntity();
  }


  @SchemaMapping(typeName = "Compiler")
  @Override
  public List<CompilerResourceAnalytic> analytics(final CompilerEntity compilerEntity) {
    return this.compilerService.getCompilerResourceAnalytics(
      new ICompilerService.GetCompiler(compilerEntity.compilerId()));
  }

  @SchemaMapping(typeName = "Compiler")
  @Override
  public long lastReportedAt(final CompilerEntity compilerEntity) {
    return this.compilerService.getLastCompilerHeartbeat(new ICompilerService.GetCompiler(compilerEntity.compilerId()))
      .createdAt().getTime();
  }

  @SchemaMapping(typeName = "Compiler")
  @Override
  public long createdAt(final CompilerEntity compilerEntity) {
    return compilerEntity.createdAt().getTime();
  }

  @SchemaMapping(typeName = "CompilerResourceAnalytic")
  @Override
  public long timestamp(final CompilerResourceAnalytic resourceAnalytic) {
    return resourceAnalytic.timestamp();
  }

  @SchemaMapping(typeName = "CompilerResourceAnalytic")
  @Override
  public double averageCpu(final CompilerResourceAnalytic resourceAnalytic) {
    return resourceAnalytic.averageCpu();
  }

  @SchemaMapping(typeName = "CompilerResourceAnalytic")
  @Override
  public double averageMemory(final CompilerResourceAnalytic resourceAnalytic) {
    return resourceAnalytic.averageMemory();
  }

  @SchemaMapping(typeName = "TestRun")
  @Override
  public UUID id(final TestRun testRun) {
    return testRun.id();
  }

  @SchemaMapping(typeName = "TestRun")
  @Override
  public List<TestRunResultChecked> results(final TestRun testRun) {
    return testRun.results();
  }

  @SchemaMapping(typeName = "TestRun")
  @Override
  public boolean taskComplete(final TestRun testRun) {
    return testRun.taskComplete();
  }

  @SchemaMapping(typeName = "TestRun")
  @Override
  public int taskScore(final TestRun testRun) {
    return testRun.taskScore();
  }

  @SchemaMapping(typeName = "TestRunResult")
  @Override
  public List<Object> input(final TestRunResultChecked result) {
    return Optional.ofNullable(result.input()).orElse(Collections.emptyList());
  }

  @SchemaMapping(typeName = "TestRunResult")
  @Override
  public Object output(final TestRunResultChecked result) {
    return result.output();
  }

  @SchemaMapping(typeName = "TestRunResult")
  @Override
  public List<String> logs(final TestRunResultChecked result) {
    return Optional.ofNullable(result.logs()).orElse(Collections.emptyList());
  }

  @SchemaMapping(typeName = "TestRunResult")
  @Override
  public List<TestError> errors(final TestRunResultChecked result) {
    return Optional.ofNullable(result.errors()).orElse(Collections.emptyList());
  }

  @SchemaMapping(typeName = "TestRunResult")
  @Override
  public long compileTimeMs(final TestRunResultChecked result) {
    return result.compileTimeMs();
  }

  @SchemaMapping(typeName = "TestRunResult")
  @Override
  public long runTimeMs(final TestRunResultChecked result) {
    return result.runTimeMs();
  }

  @SchemaMapping(typeName = "TestRunResult")
  @Override
  public boolean pass(final TestRunResultChecked result) {
    return result.pass();
  }

  @SchemaMapping(typeName = "TestError")
  @Override
  public String type(final TestError resultError) {
    return resultError.type();
  }

  @SchemaMapping(typeName = "TestError")
  @Override
  public String message(final TestError resultError) {
    return resultError.message();
  }

}
