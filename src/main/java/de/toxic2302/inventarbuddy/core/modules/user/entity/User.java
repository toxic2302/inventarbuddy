package de.toxic2302.inventarbuddy.core.modules.user.entity;

import de.toxic2302.inventarbuddy.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter @Setter
@Builder
@AllArgsConstructor
public class User extends BaseEntity {

  @Column
  private boolean active;

  @Column(length = 100)
  private String username;

  @Column(length = 25)
  private String firstName;

  @Column(length = 25)
  private String lastName;

  @Email(regexp = "(^$|^.*@.*\\..*$)")
  @Column(length = 50)
  private String email;

  // ---- Constructor ----
  public User() {}

  // ---- Functions ----
  @Override
  public String toString() {
    return firstName + " " + lastName;
  }
}
