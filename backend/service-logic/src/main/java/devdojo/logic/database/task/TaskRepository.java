package devdojo.logic.database.task;

import devdojo.logic.database.EntityRepository;
import devdojo.logic.database.ExternalEntityRepository;
import devdojo.logic.database.language.LanguageEntity;
import java.util.List;

public interface TaskRepository extends ExternalEntityRepository<TaskEntity> {

  List<TaskEntity> findAllByLanguageEntity(final LanguageEntity languageEntity);

}
