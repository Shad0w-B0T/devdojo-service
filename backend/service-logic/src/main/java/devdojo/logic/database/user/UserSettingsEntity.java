package devdojo.logic.database.user;

import devdojo.logic.database.AbstractEntity;
import devdojo.schema.user.IUserSettings;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Entity
@Table(name = "user_settings")
@NoArgsConstructor
@Getter
@Setter
@Accessors(fluent = true)
public final class UserSettingsEntity extends AbstractEntity implements IUserSettings {

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private UserEntity user;

  @Column(nullable = false)
  private boolean profilePublic = true;

  public UserSettingsEntity(@NotNull final UserEntity user,
                            @NotNull final Boolean profilePublic) {
    this.user = user;
    this.profilePublic = profilePublic;
  }
}
