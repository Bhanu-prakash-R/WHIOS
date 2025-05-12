package com.inventorymanagement.usermodule.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.inventorymanagement.usermodule.entity.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    // Custom query methods can be added here if needed
	@Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);
	
	  @Query("SELECT u FROM User u WHERE u.username = :username")
	    Optional<User> findByUsername(@Param("username") String username);
}
