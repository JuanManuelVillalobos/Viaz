package com.viaz.dashboard.entity.role;

import com.viaz.dashboard.entity.permission.Permission;
import com.viaz.dashboard.entity.user.ViazUser;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    @EqualsAndHashCode.Include
    private Long roleId;

    @Column(name = "name", unique = true, nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "display_name")
    private String displayName;

    @ManyToMany(mappedBy = "roles")
    private Set<ViazUser> users = new HashSet<>();


    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")

    )
    private Set<Permission> permissions = new HashSet<>();

    @Column(name = "is_cosmetic")
    private boolean cosmetic = false;

    @Column(name = "system_protected")
    private boolean systemProtected = false;


    public Role(String name) {
        this.name = name;
        this.displayName = name;
        this.permissions = new HashSet<>();
    }

    public Role(String name, String description) {
        this.name = name;
        this.description = description;
        this.displayName = name;
        this.permissions = new HashSet<>();
    }

    public Role(String name, String description, String displayName) {
        this.name = name;
        this.description = description;
        this.displayName = displayName;
        this.permissions = new HashSet<>();
    }


    public void addPermission(Permission permission) {
        this.permissions.add(permission);
    }

    public void removePermission(Permission permission) {
        this.permissions.remove(permission);
    }

    public boolean hasPermission(String permissionName) {
        return permissions.stream()
                .anyMatch(permission -> permission.getName().equals(permissionName));
    }

}
