package devdojo.logic.database.language;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import devdojo.logic.database.BaseJpaTest;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@DisplayName("LanguageRepository Tests")
final class LanguageRepositoryTest extends BaseJpaTest {

  @Autowired
  private LanguageRepository cut;

  private LanguageEntity language;

  @BeforeEach
  void setUp() {
    this.language = new LanguageEntity(
      "Java",
      "Write once run everywhere.",
      true
    );
  }

  @Test
  @DisplayName("ExistsByLanguageName - Returns true language name exists")
  void existsByLanguageNameTest() {
    final LanguageEntity language = this.language;
    this.cut.save(language);

    final boolean result = this.cut.existsByLanguageName("Java");
    assertTrue(result);
  }

  @Test
  @DisplayName("ExistsByDisplayName - Returns false language name does not exist")
  void languageNameDoesNotExist() {
    final LanguageEntity language = this.language;
    this.cut.save(language);

    final boolean result = this.cut.existsByLanguageName("Morty");
    assertFalse(result);
  }

  @Test
  @DisplayName("ExistsByDisplayName - Returns false language name is null")
  void languageNameIsNull() {
    final LanguageEntity language = this.language;
    this.cut.save(language);

    final boolean result = this.cut.existsByLanguageName(null);
    assertFalse(result);
  }

  @Test
  @DisplayName("FindById - Returns correct LanguageEntity")
  void findByIdTest() {
    final LanguageEntity language = this.language;

    final UUID LANGUAGE_ID = this.cut.save(language).id();

    final Optional<LanguageEntity> result = this.cut.findById(LANGUAGE_ID);

    assertTrue(result.isPresent());
    assertEquals(1, this.cut.count());
    assertEquals(language, result.get());
    assertEquals(language.languageName(), result.get().languageName());
    assertEquals(language.description(), result.get().description());
  }

  @Test
  @DisplayName("FindById - Returns empty id does not exist")
  void idDoesNotExist() {
    final LanguageEntity language = this.language;
    this.cut.save(language);

    final Optional<LanguageEntity> result =
      this.cut.findById(UUID.fromString("ed027438-80de-4daf-8e0e-5fa3696f6118"));

    assertTrue(result.isEmpty());
  }
}
