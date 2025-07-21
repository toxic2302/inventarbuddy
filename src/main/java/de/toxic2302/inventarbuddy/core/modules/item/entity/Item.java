package de.toxic2302.inventarbuddy.core.modules.item.entity;

import de.toxic2302.inventarbuddy.base.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Entity
@Table(name = "items")
@Getter
@Setter
public class Item extends BaseEntity {

  // ---- Globales ----
  @Column
  @NotNull
  private String name;

  @Column
  @NotNull
  private String brand;

  @Column
  private String serialNumber;

  @Column(length = 10000)
  private String description;

  /*@ManyToOne
  @JoinColumn(name = "user_id")
  private User user;*/
  @Column
  private Long userId;

  @Column
  private Boolean sold;

  // ---- Constructor ----
  public Item() {}
  public Item(String name, OAuth2User user) {
    this.name = name;
    this.userId = user.getAttribute("id");
  }
}
