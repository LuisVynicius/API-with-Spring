package com.mevy.restfulapi.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mevy.restfulapi.models.User;
import com.mevy.restfulapi.repositories.UserRepository;
import com.mevy.restfulapi.services.exceptions.DataBindingViolationException;
import com.mevy.restfulapi.services.exceptions.ObjectNotFoundException;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    public User findById(Long id){
        return userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(
            "User not found! id: " + id + ", type: " + User.class.getName()));
    }

    @Transactional
    public User create(User obj){
        obj.setId(null);
        obj = userRepository.save(obj);
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
            throw new DataBindingViolationException("Database integrity error. ");
        }
    }

}
