package de.toxic2302.inventarbuddy.core.modules.item.entity;

import de.toxic2302.inventarbuddy.base.entity.BaseEntity;
import de.toxic2302.inventarbuddy.core.modules.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "items")
@Getter
@Setter
@AllArgsConstructor
public class Item extends BaseEntity {

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

  @ManyToOne
  private User user;

  @Column
  private Boolean sold;

  @Column
  private String firmwareVersion;

  public Item() {}
}
