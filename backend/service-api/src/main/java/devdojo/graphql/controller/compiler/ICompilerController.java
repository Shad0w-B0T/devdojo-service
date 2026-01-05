package devdojo.graphql.controller.compiler;

import devdojo.graphql.input.compiler.GetTestRun;
import devdojo.logic.compiler.ICompilerService;
import devdojo.logic.database.compiler.CompilerEntity;
import devdojo.logic.database.language.LanguageEntity;
import devdojo.schema.code.TestError;
import devdojo.schema.code.TestRun;
import devdojo.schema.code.TestRunResultChecked;
import devdojo.schema.compiler.CompilerResourceAnalytic;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public interface ICompilerController {

  // ────── Queries ──────
  @NotNull
  List<CompilerEntity> compilers();

  @NotNull
  CompilerEntity compiler(ICompilerService.GetCompiler input);

  @NotNull
  TestRun testRun(GetTestRun input);

  // ────── Schema: Compiler ──────
  @NotNull
  UUID id(@NotNull final CompilerEntity compilerEntity);

  @NotNull
  int cpuCores(@NotNull final CompilerEntity compilerEntity);

  @NotNull
  double memoryTotal(@NotNull final CompilerEntity compilerEntity);

  @NotNull
  LanguageEntity language(@NotNull final CompilerEntity compilerEntity);

  @NotNull
  List<CompilerResourceAnalytic> analytics(@NotNull final CompilerEntity compilerEntity);

  @NotNull
  long lastReportedAt(@NotNull final CompilerEntity compilerEntity);

  @NotNull
  long createdAt(@NotNull final CompilerEntity compilerEntity);


  // ────── Schema: CompilerResourceAnalytic ──────
  long timestamp(@NotNull final CompilerResourceAnalytic resourceAnalytic);

  double averageCpu(@NotNull final CompilerResourceAnalytic resourceAnalytic);

  double averageMemory(@NotNull final CompilerResourceAnalytic resourceAnalytic);

  // ────── Schema: TestRun ──────
  @NotNull
  UUID id(@NotNull final TestRun testRun);

  @NotNull
  List<TestRunResultChecked> results(@NotNull final TestRun testRun);

  boolean taskComplete(@NotNull final TestRun testRun);

  int taskScore(@NotNull final TestRun testRun);

  // ────── Schema: TestRunResult ──────
  @NotNull
  List<Object> input(@NotNull final TestRunResultChecked result);

  @NotNull
  Object output(@NotNull final TestRunResultChecked result);

  @NotNull
  List<String> logs(@NotNull final TestRunResultChecked result);

  @NotNull
  List<TestError> errors(@NotNull final TestRunResultChecked result);

  long compileTimeMs(@NotNull final TestRunResultChecked result);

  long runTimeMs(@NotNull final TestRunResultChecked result);

  boolean pass(@NotNull final TestRunResultChecked result);

  // ────── Schema: TestRunResult ──────
  @NotNull
  String type(@NotNull final TestError resultError);

  @NotNull
  String message(@NotNull final TestError resultError);

}
