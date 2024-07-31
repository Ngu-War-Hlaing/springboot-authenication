package com.springboot_authenication.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springboot_authenication.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer>{
    @Query("SELECT r FROM Role r LEFT JOIN FETCH r.users WHERE r.name = :name")
    Optional<Role> findByName(@Param("name") String name);
}
