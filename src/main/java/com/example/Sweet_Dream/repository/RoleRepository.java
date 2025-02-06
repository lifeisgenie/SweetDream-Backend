package com.example.Sweet_Dream.repository;

import com.example.Sweet_Dream.entity.Role;
import com.example.Sweet_Dream.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleName(RoleName roleName);
}
