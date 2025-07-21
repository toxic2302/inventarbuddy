package de.toxic2302.inventarbuddy.base.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @JsonIgnore
    @CreatedDate
    @Column
    private LocalDateTime createdAt;

    @JsonIgnore
    @CreatedBy
    @Column(length = 20)
    private String createdBy;

    @JsonIgnore
    @LastModifiedDate
    @Column
    private LocalDateTime modifiedAt;

    @JsonIgnore
    @LastModifiedBy
    @Column(length = 20)
    private String modifiedBy;

    public BaseEntity() {}
}
