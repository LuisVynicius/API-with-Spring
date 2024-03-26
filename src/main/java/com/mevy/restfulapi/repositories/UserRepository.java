package com.mevy.restfulapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mevy.restfulapi.models.User;

public interface UserRepository extends JpaRepository<User, Long>{
    
}
