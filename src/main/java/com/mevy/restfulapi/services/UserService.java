package com.mevy.restfulapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mevy.restfulapi.models.User;
import com.mevy.restfulapi.repositories.TaskRepository;
import com.mevy.restfulapi.repositories.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    public User findById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found. id: " + id));
    }

    @Transactional
    public User create(User obj){
        obj.setId(null);
        obj = userRepository.save(obj);
        taskRepository.saveAll(obj.getTasks());
        return obj;
    }

    @Transactional
    public User update(User obj){
        User newObj = findById(obj.getId());
        newObj.setPassword(obj.getPassword());
        return userRepository.save(newObj);
    }

    public void delete(Long id){
        try {
            userRepository.deleteById(id);
        } catch(Exception e){
            throw new RuntimeException("Database integrity error. ");
        }
    }

}
