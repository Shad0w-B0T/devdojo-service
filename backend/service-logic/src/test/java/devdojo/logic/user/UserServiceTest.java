package devdojo.logic.user;

import devdojo.logic.database.user.UserRepository;
import devdojo.schema.user.UserRole;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.mockito.Mockito.mock;

@DisplayName("UserService Unit Tests")
class UserServiceTest {

  private UserRepository userRepository;
  private IUserService userService;
  private UUID id;
  private String displayName;
  private String updateDisplayName;
  private UserRole role;
  private UserRole updateRole;
  private boolean active;
  private boolean updateActive;
  private Date lastLogin;
  private Date updateLastLogin;

  @BeforeEach
  void setUp() {
    this.userRepository = mock(UserRepository.class);
    this.userService = new UserService(this.userRepository);
    this.id = UUID.randomUUID();
    this.displayName = "John";
    this.updateDisplayName = "Doe";
    this.role = UserRole.USER;
    this.updateRole = UserRole.ADMIN;
    this.active = true;
    this.updateActive = false;
    this.lastLogin = new Date();
    this.updateLastLogin = new Date(0);
  }
/*
  @Test
  @DisplayName("should return user when user exists")
  void shouldReturnUser_whenUserExists() {
    final UserEntity entity = new UserEntity(this.displayName, this.role, this.active, this.lastLogin);
    when(this.userRepository.findById(this.id)).thenReturn(Optional.of(entity));

    final UserEntity result = this.userService.getUser(new IUserService.UserGet() {
      @Override
      public UUID userId() {
        return id;
      }

      @Override
      public UUID supabaseId() {
        return null;
      }
    });

    assertEquals(entity, result);
    verify(this.userRepository).findById(this.id);
  }

  @Test
  @DisplayName("should throw UserNotFoundException when user does not exist")
  void shouldThrowUserNotFoundException_whenUserDoesNotExist() {
    when(userRepository.findById(this.id)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> this.userService.getUser(new IUserService.UserGet() {
      @Override
      public UUID userId() {
        return id;
      }

      @Override
      public UUID supabaseId() {
        return null;
      }
    }));
  }

  @Test
  @DisplayName("should create new user when display name is not taken")
  void shouldCreateNewUser_whenDisplayNameIsNotTaken() {
    when(this.userRepository.existsByDisplayName(this.displayName)).thenReturn(false);
    final UserEntity saved = new UserEntity(this.displayName, UserRole.USER, true, new Date());
    when(this.userRepository.save(any())).thenReturn(saved);

    final UserEntity result = this.userService.createUser(new UserCreate() {
      @Override
      public @NotNull String displayName() {
        return displayName;
      }

      @Override
      public @NotNull UserRole role() {
        return role;
      }

      @Override
      public boolean active() {
        return active;
      }
    });

    assertEquals(saved, result);
    verify(this.userRepository).save(any());
  }

  @Test
  @DisplayName("should throw UserAlreadyExistsException when display name is already taken")
  void shouldThrowUserAlreadyExistsException_whenDisplayNameAlreadyExists() {
    when(this.userRepository.existsByDisplayName("Existing")).thenReturn(true);

    assertThrows(AlreadyExistsException.class, () -> userService.createUser(new UserCreate() {
      @Override
      public @NotNull String displayName() {
        return "Existing";
      }

      @Override
      public @NotNull UserRole role() {
        return role;
      }

      @Override
      public boolean active() {
        return active;
      }
    }));
  }

  @Test
  @DisplayName("should update user fields when values have changed")
  void shouldUpdateUser_whenFieldsHaveChanged() {
    final UserEntity user = new UserEntity(this.displayName, this.role, this.active, this.lastLogin);
    user.id(this.id);

    when(this.userRepository.findById(this.id)).thenReturn(Optional.of(user));
    when(this.userRepository.save(any())).thenReturn(user);

    final UserEntity result = this.userService.updateUser(new UserUpdate() {
      @Override
      public @NotNull UUID userId() {
        return id;
      }

      @Override
      public @NotNull String displayName() {
        return updateDisplayName;
      }

      @Override
      public @NotNull UserRole role() {
        return updateRole;
      }

      @Override
      public boolean active() {
        return updateActive;
      }

      @Override
      public @NotNull Date lastLogin() {
        return updateLastLogin;
      }
    });

    assertEquals(this.updateDisplayName, result.displayName());
    assertEquals(this.updateRole, result.role());
    assertFalse(result.active());
    assertEquals(this.updateLastLogin, result.lastLogin());
    verify(this.userRepository).save(user);
  }

  @Test
  @DisplayName("should not update user when fields remain unchanged")
  void shouldNotUpdateUser_whenNoFieldsChanged() {
    final UserEntity user = new UserEntity(this.displayName, this.role, this.active, this.lastLogin);
    user.id(this.id);

    when(this.userRepository.findById(this.id)).thenReturn(Optional.of(user));

    final UserEntity result =
      this.userService.updateUser(new UserUpdate() {
        @Override
        public @NotNull UUID userId() {
          return id;
        }

        @Override
        public @NotNull String displayName() {
          return displayName;
        }

        @Override
        public @NotNull UserRole role() {
          return role;
        }

        @Override
        public boolean active() {
          return active;
        }

        @Override
        public @NotNull Date lastLogin() {
          return lastLogin;
        }
      });

    assertEquals(user, result);
    verify(this.userRepository, never()).save(any());
  }

  @Test
  @DisplayName("should delete user when deletion is successful")
  void shouldDeleteUser_whenDeletionSuccessful() {
    final UserEntity user = new UserEntity(this.displayName, this.role, this.active, this.lastLogin);
    user.id(this.id);

    when(this.userRepository.findById(this.id)).thenReturn(Optional.of(user));
    when(this.userRepository.existsById(this.id)).thenReturn(false); // simulate successful deletion

    UserEntity result = this.userService.deleteUser(() -> this.id);

    assertEquals(user, result);
    verify(this.userRepository).delete(user);
  }

  @Test
  @DisplayName("should throw UserDeletionException when user still exists after deletion attempt")
  void shouldThrowUserDeletionException_whenUserStillExistsAfterDeletion() {
    final UserEntity user = new UserEntity(this.displayName, this.role, this.active, this.lastLogin);
    user.id(this.id);

    when(this.userRepository.findById(this.id)).thenReturn(Optional.of(user));
    when(this.userRepository.existsById(this.id)).thenReturn(true); // deletion failed

    assertThrows(DeletionException.class, () -> this.userService.deleteUser(() -> this.id));
  }

  @Test
  @DisplayName("should throw NullPointerException when passing null arguments")
  void shouldThrowNullPointerException_whenArgsAreNull() {
    assertThrows(NullPointerException.class, () -> this.userService.getUser(null));
    assertThrows(NullPointerException.class, () -> this.userService.createUser(null));
    assertThrows(NullPointerException.class, () -> this.userService.updateUser(null));
    assertThrows(NullPointerException.class, () -> this.userService.deleteUser(null));
  }

 */
}
