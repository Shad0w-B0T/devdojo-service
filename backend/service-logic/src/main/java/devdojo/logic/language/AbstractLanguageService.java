package devdojo.logic.language;

import devdojo.logic.database.language.LanguageEntity;
import devdojo.logic.database.language.LanguageRepository;
import devdojo.logic.RandomStringGenerator;
import devdojo.schema.annotation.RoleRequired;
import devdojo.schema.exception.database.AlreadyExistsException;
import devdojo.schema.exception.database.DatabaseException;
import devdojo.schema.exception.database.DeletionException;
import devdojo.schema.exception.database.NotFoundException;
import devdojo.schema.user.UserRole;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.annotation.Validated;

@Transactional
@Validated
@Slf4j
public abstract class AbstractLanguageService implements ILanguageService {

  private final @NotNull LanguageRepository languageRepository;

  protected AbstractLanguageService(
    @NotNull(message = "LanguageRepository must not be null") final LanguageRepository languageRepository) {
    this.languageRepository = languageRepository;
  }

  @Cacheable(
    cacheNames = "languages",
    key = "#p0.languageId()",
    unless = "#result == null"
  )
  @Override
  @Transactional
  public LanguageEntity getLanguage(final GetLanguage getLanguage) {
    return this.languageElseThrow(getLanguage.languageId());
  }

  @Cacheable(
    cacheNames = "languages",
    key = "#p0 != null && #p0.onlyActive() ? 'active' : 'all'",
    unless = "#result == null || #result.isEmpty()"
  )
  @Override
  @Transactional
  public @NotNull List<LanguageEntity> getLanguages(final GetLanguages getLanguages) {
    if (getLanguages != null && getLanguages.onlyActive()) {
      return this.languageRepository.findAllByActiveTrue();
    }

    return this.languageRepository.findAll();
  }

  @Caching(evict = {
    @CacheEvict(cacheNames = "languages", allEntries = true)
  })
  @Override
  @Transactional
  public LanguageEntity createLanguage(final CreateLanguage createLanguage) {
    log.info("Trying to create a new language: '" + createLanguage.languageName() + "'.");

    if (this.languageRepository.existsByLanguageName(createLanguage.languageName())) {
      throw new AlreadyExistsException(createLanguage.languageName());
    }

    log.info("Created new language with name: '" + createLanguage.languageName() + "'.");

    final LanguageEntity languageEntity;
    try {
      final String token = RandomStringGenerator.generateRandomString(128);
      languageEntity = this.languageRepository.save(new LanguageEntity(token, createLanguage.languageName(),
        createLanguage.description(), createLanguage.active()));
    } catch (final DataAccessException exception) {
      throw new DatabaseException("Error while creating language in database.", exception);
    }
    return languageEntity;
  }

  @Caching(evict = {
    @CacheEvict(cacheNames = "languages", key = "#p0.languageId()"),
    @CacheEvict(cacheNames = "languages", allEntries = true)
  })
  @Override
  @Transactional
  public LanguageEntity updateLanguage(final UpdateLanguage updateLanguage) {
    final LanguageEntity languageEntity = this.languageElseThrow(updateLanguage.languageId());

    languageEntity.languageName(updateLanguage.languageName());
    languageEntity.description(updateLanguage.description());
    languageEntity.active(updateLanguage.active());

    final LanguageEntity updatedLanguageEntity;
    try {
      updatedLanguageEntity = this.languageRepository.save(languageEntity);
      log.info("User {} successfully updated.", updatedLanguageEntity.externalId());
    } catch (final DataAccessException exception) {
      throw new DatabaseException("Error while updating language.", exception);
    }

    return updatedLanguageEntity;
  }

  @Caching(evict = {
    @CacheEvict(cacheNames = "languages", key = "#p0.languageId()"),
    @CacheEvict(cacheNames = "languages", allEntries = true)
  })
  @Transactional
  @Override
  public LanguageEntity deleteLanguage(final DeleteLanguage deleteLanguage) {
    final String id = deleteLanguage.languageId();
    final LanguageEntity languageEntity = this.languageElseThrow(id);

    try {
      this.languageRepository.delete(languageEntity);
      log.info("Language {} successfully deleted.", languageEntity.id());
    } catch (final DataAccessException exception) {
      throw new DatabaseException("Error while deleting language.", exception);
    }

    // check if language was successfully deleted from db
    if (this.languageRepository.existsByExternalId(id)) {
      throw new DeletionException(deleteLanguage.languageId());
    }

    log.info("Language {} is now permanently removed.", languageEntity.id());
    return languageEntity;
  }

  private LanguageEntity languageElseThrow(final String id) {
    log.info("Trying to get language with uuid: '" + id + "'.");
    return this.languageRepository.findByExternalId(id).orElseThrow(() -> new NotFoundException(id));
  }
}
