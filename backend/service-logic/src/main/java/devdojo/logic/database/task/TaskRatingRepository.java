package devdojo.logic.database.task;

import devdojo.logic.database.EntityRepository;
import devdojo.logic.database.user.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRatingRepository
  extends EntityRepository<TaskRatingEntity> {

  Optional<TaskRatingEntity> findByTaskEntityAndUserEntity(final TaskEntity taskEntity, final UserEntity userEntity);

  @Query("""
      SELECT AVG(r.rating)
      FROM TaskRatingEntity r
      WHERE r.taskEntity.externalId = :taskId
    """)
  Double findAverageRatingByTaskId(@Param("taskId") String taskId);

}
