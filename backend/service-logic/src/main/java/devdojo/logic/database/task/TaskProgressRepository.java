package devdojo.logic.database.task;

import devdojo.logic.database.EntityRepository;
import devdojo.logic.database.user.UserEntity;
import devdojo.schema.task.Difficulty;
import devdojo.schema.task.TaskProgressState;
import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskProgressRepository extends EntityRepository<TaskProgressEntity> {
  List<TaskProgressEntity> findByUserEntity(final UserEntity userEntity);

  Optional<TaskProgressEntity> findByUserEntityAndTaskEntity(final UserEntity userEntity,
                                                             final TaskEntity taskEntity);

  @Query(value = """
    SELECT 
        t.name AS name,
        t.description AS description,
        t.difficulty AS difficulty,
        COALESCE(tp.state, 0) AS state,
        COALESCE(tp.score, 0) AS score
    FROM task t
    LEFT JOIN task_user_progress tp
        ON tp.task_id = t.id AND tp.user_id = :userId
    WHERE t.language_id = :languageId
    """, nativeQuery = true)
  List<TaskWithProgressProjection> findAllTasksWithProgressByUserAndLanguage(
    @Param("userId") UUID userId,
    @Param("languageId") UUID languageId
  );

  @Query(value = """
      SELECT
        ranked.user_id     AS userId,
        ranked.total_score AS totalScore,
        ranked.rank        AS rank
      FROM (
        SELECT
          tp.user_id,
          SUM(tp.score) AS total_score,
          RANK() OVER (ORDER BY SUM(tp.score) DESC) AS rank
        FROM devdojo.task_user_progress tp
        JOIN devdojo.task t ON tp.task_id = t.id
        WHERE t.language_id = :languageId
        GROUP BY tp.user_id
      ) ranked
      WHERE ranked.user_id = :userId
    """, nativeQuery = true)
  UserRankingProjection getUserRankForLanguage(
    @Param("userId") UUID userId,
    @Param("languageId") UUID languageId
  );

  @Query(
    value = """
      SELECT ranked.user_id AS userId,   
             ranked.total_score AS totalScore,
             ranked.rank AS rank
      FROM (
        SELECT
          up.user_id,
          SUM(up.score) AS total_score,
          RANK() OVER (ORDER BY SUM(up.score) DESC) AS rank
        FROM devdojo.task_user_progress up
        JOIN devdojo.task t ON up.task_id = t.id
        WHERE t.language_id = :languageId
        GROUP BY up.user_id
      ) AS ranked
      WHERE ranked.rank BETWEEN :start AND :end
      ORDER BY ranked.rank
      """,
    nativeQuery = true
  )
  List<UserRankingProjection> findUsersWithRankByLanguage(
    @Param("languageId") UUID languageId,
    @Param("start") long start,
    @Param("end") long end
  );

  @Query("""
      SELECT t FROM TaskEntity t
      WHERE t.languageEntity.id = :languageId
      AND NOT EXISTS (
        SELECT 1 FROM TaskProgressEntity tp
        WHERE tp.taskEntity = t AND tp.userEntity.id = :userId
      )
      ORDER BY t.id ASC
    """)
  List<TaskEntity> findTasksWithoutProgress(
    @Param("userId") UUID userId,
    @Param("languageId") UUID languageId
  );

  @Query("""
      SELECT t FROM TaskEntity t
      WHERE NOT EXISTS (
        SELECT 1 FROM TaskProgressEntity tp
        WHERE tp.taskEntity = t AND tp.userEntity.id = :userId
      )
      ORDER BY t.id ASC
    """)
  List<TaskEntity> findTasksWithoutProgress(
    @Param("userId") UUID userId
  );

  @Query("""
      SELECT t FROM TaskEntity t
      WHERE t.languageEntity.id = :languageId
      AND EXISTS (
        SELECT 1 FROM TaskProgressEntity tp
        WHERE tp.taskEntity = t AND tp.userEntity.id = :userId AND tp.state = :state
      )
      ORDER BY t.id ASC
    """)
  List<TaskEntity> findTasksWithProgressByState(
    @Param("userId") UUID userId,
    @Param("languageId") UUID languageId,
    @Param("state") TaskProgressState state
  );

  @Query("""
      SELECT t FROM TaskEntity t
      WHERE EXISTS (
        SELECT 1 FROM TaskProgressEntity tp
        WHERE tp.taskEntity = t AND tp.userEntity.id = :userId AND tp.state = :state
      )
      ORDER BY t.id ASC
    """)
  List<TaskEntity> findTasksWithProgressByState(
    @Param("userId") UUID userId,
    @Param("state") TaskProgressState state
  );

  interface TaskWithProgressProjection {
    String name();

    String description();

    Difficulty difficulty();

    int state();

    int score();
  }

  record UserRankingProjection(UUID userId,
                               long totalScore,
                               long rank) implements Serializable {
  }
}
