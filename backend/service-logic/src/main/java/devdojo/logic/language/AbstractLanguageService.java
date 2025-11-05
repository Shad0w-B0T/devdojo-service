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
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
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

  /**
   * Creates a new {@link LanguageEntity} based on the provided {@link LanguageCreate} data.
   * <p>
   * This method validates that the language name and description are not {@code null},
   * checks for duplicate language names, and attempts to persist the new language entity
   * into the database. If a language with the same name already exists, an
   * {@link AlreadyExistsException} is thrown. In case of database errors,
   * a {@link DatabaseException} is thrown.
   * </p>
   *
   * @param languageCreate the data used to create the new language; must not be {@code null}
   * @return the newly created {@link LanguageEntity}
   * @throws NullPointerException   if the language name or description is {@code null}
   * @throws AlreadyExistsException if a language with the same name already exists
   * @throws DatabaseException      if a database access error occurs during persistence
   */
  @RoleRequired(role = UserRole.ADMIN)
  @Override
  @Transactional
  public LanguageEntity createLanguage(final LanguageCreate languageCreate) {
    log.info("Trying to create a new language: '" + languageCreate.languageName() + "'.");

    if (this.languageRepository.existsByLanguageName(languageCreate.languageName())) {
      throw new AlreadyExistsException(languageCreate.languageName());
    }

    log.info("Created new language with name: '" + languageCreate.languageName() + "'.");

    final LanguageEntity languageEntity;
    try {
      final String token = RandomStringGenerator.generateRandomString(128);
      languageEntity = this.languageRepository.save(new LanguageEntity(token, languageCreate.languageName(),
        languageCreate.description(), languageCreate.active()));
    } catch (final DataAccessException exception) {
      throw new DatabaseException("Error while creating language in database.", exception);
    }
    return languageEntity;
  }


  /**
   * Retrieves a {@link LanguageEntity} based on the provided {@link LanguageGet} request.
   * <p>
   * This method checks that the {@code languageGet} parameter is not {@code null},
   * logs the attempt to retrieve the language, and then returns the corresponding
   * {@link LanguageEntity}. If the language cannot be found, it throws an appropriate exception.
   * </p>
   *
   * @param languageGet an object containing the UUID of the language to retrieve; must not be {@code null}
   * @return the {@link LanguageEntity} associated with the provided UUID
   * @throws NullPointerException if {@code languageGet} is {@code null}
   * @throws NotFoundException    if no language entity with the given ID exists
   */
  @Override
  @Transactional
  public LanguageEntity getLanguage(final LanguageGet languageGet) {
    return this.languageElseThrow(languageGet.languageId());
  }


  /**
   * Retrieves all language entities from the repository.
   *
   * @return a non-null list of all {@link LanguageEntity} objects stored in the repository.
   */
  @Override
  @Transactional
  public @NotNull List<LanguageEntity> getLanguages(final GetLanguages getLanguages) {
    if (getLanguages != null && getLanguages.onlyActive()) {
      return this.languageRepository.findAllByActiveTrue();
    }

    return this.languageRepository.findAll();
  }

  /**
   * Updates an existing {@link LanguageEntity} based on the provided {@link LanguageUpdate} data.
   * <p>
   * This method compares the incoming values with the current ones and applies only the changes.
   * If no fields are different, the entity is not updated in the database.
   * </p>
   * <p>
   * If the language with the given ID does not exist, an exception is thrown. In case of a database
   * error during saving, a {@link DatabaseException} is thrown.
   * </p>
   *
   * @param languageUpdate the update data containing the ID and new values for the language; must not be {@code null}
   * @return the updated {@link LanguageEntity}, or the existing one if no changes were made
   * @throws NullPointerException if {@code languageUpdate} is {@code null}
   * @throws NotFoundException    if the language with the given ID does not exist
   * @throws DatabaseException    if an error occurs while updating the entity in the database
   */
  @RoleRequired(role = UserRole.ADMIN)
  @Override
  @Transactional
  public LanguageEntity updateLanguage(final LanguageUpdate languageUpdate) {
    final LanguageEntity languageEntity = this.languageElseThrow(languageUpdate.languageId());

    languageEntity.languageName(languageUpdate.languageName());
    languageEntity.description(languageUpdate.description());
    languageEntity.active(languageUpdate.active());

    final LanguageEntity updatedLanguageEntity;
    try {
      updatedLanguageEntity = this.languageRepository.save(languageEntity);
      log.info("User {} successfully updated.", updatedLanguageEntity.id());
    } catch (final DataAccessException exception) {
      throw new DatabaseException("Error while updating language.", exception);
    }

    return updatedLanguageEntity;
  }

  /**
   * Deletes an existing {@link LanguageEntity} based on the provided {@link LanguageDelete} data.
   * <p>
   * This method verifies the existence of the language entity and deletes it from the database.
   * After deletion, it performs a verification to ensure the entity was actually removed.
   * </p>
   * <p>
   * If the entity is not successfully deleted or if a database error occurs, an appropriate exception is thrown.
   * </p>
   *
   * @param languageDelete the data containing the ID of the language to delete; must not be {@code null}
   * @return {@code true} if the language was successfully deleted
   * @throws NullPointerException if {@code languageDelete} is {@code null}
   * @throws NotFoundException    if the language with the given ID does not exist
   * @throws DatabaseException    if an error occurs while deleting the entity from the database
   * @throws DeletionException    if the language still exists in the database after deletion
   */
  @RoleRequired(role = UserRole.ADMIN)
  @Transactional
  @Override
  public LanguageEntity deleteLanguage(final LanguageDelete languageDelete) {
    final UUID id = languageDelete.languageId();
    final LanguageEntity languageEntity = this.languageElseThrow(id);

    try {
      this.languageRepository.delete(languageEntity);
      log.info("Language {} successfully deleted.", languageEntity.id());
    } catch (final DataAccessException exception) {
      throw new DatabaseException("Error while deleting language.", exception);
    }

    // check if language was successfully deleted from db
    if (this.languageRepository.existsById(id)) {
      throw new DeletionException(languageDelete.languageId());
    }

    log.info("Language {} is now permanently removed.", languageEntity.id());
    return languageEntity;
  }

  /**
   * Retrieves a language entity by its UUID or throws an exception if not found.
   *
   * @param id The unique identifier of the language entity.
   * @return The {@link LanguageEntity} associated with the given UUID.
   * @throws NotFoundException If no language entity exists with the provided UUID.
   */
  private LanguageEntity languageElseThrow(final UUID id) {
    log.info("Trying to get language with uuid: '" + id + "'.");
    return this.languageRepository.findById(id).orElseThrow(() -> new NotFoundException(id));
  }
}
