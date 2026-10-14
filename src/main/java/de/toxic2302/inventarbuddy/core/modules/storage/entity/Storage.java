package de.toxic2302.inventarbuddy.core.modules.storage.entity;

import de.toxic2302.inventarbuddy.base.entity.BaseEntity;
import de.toxic2302.inventarbuddy.core.modules.user.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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
@Table(name = "storages")
public class Storage extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    @ToString.Exclude
    private Storage parent;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Storage> children = new ArrayList<>();

    public void addChild(Storage child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(Storage child) {
        children.remove(child);
        child.setParent(null);
    }
}
