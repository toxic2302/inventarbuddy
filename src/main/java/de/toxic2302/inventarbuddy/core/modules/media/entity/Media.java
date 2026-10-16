package de.toxic2302.inventarbuddy.core.modules.media.entity;

import de.toxic2302.inventarbuddy.base.entity.BaseEntity;
import de.toxic2302.inventarbuddy.core.modules.item.entity.Item;
import de.toxic2302.inventarbuddy.core.modules.user.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "media")
public class Media extends BaseEntity {

    @Column(name = "fileName")
    private String fileName;

    @Column(name = "contentType", nullable = false)
    private String contentType;

    @Column(name = "fileSize", nullable = false)
    private long fileSize;

    @Column(name = "data", nullable = false)
    @ToString.Exclude
    private byte[] data;

    @ManyToOne
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private Item item;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;
}
