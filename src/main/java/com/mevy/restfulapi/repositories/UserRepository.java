package com.mevy.restfulapi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.mevy.restfulapi.models.User;


public interface UserRepository extends JpaRepository<User, Long>{
    
    @Transactional(readOnly = true)
    User findByUsername(String username);

}
