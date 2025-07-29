package de.toxic2302.inventarbuddy.core.modules.item.dto;

import de.toxic2302.inventarbuddy.core.modules.user.entity.User;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.UUID;
import lombok.Value;

@Value
public class ItemDto implements Serializable {

  UUID id;
  @NotNull
  String name;
  @NotNull
  String brand;
  String serialNumber;
  String description;
  Boolean sold;
  User user;
  String firmwareVersion;
}
