package devdojo.logic.cache.compiler;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface CompilerTaskResultRepository extends CrudRepository<CompilerTaskResult, UUID> {
}
