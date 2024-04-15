package com.mevy.restfulapi.services;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mevy.restfulapi.models.User;
import com.mevy.restfulapi.models.enums.ProfileEnum;
import com.mevy.restfulapi.repositories.UserRepository;
import com.mevy.restfulapi.security.UserSpringSecurity;
import com.mevy.restfulapi.services.exceptions.AuthorizationException;
import com.mevy.restfulapi.services.exceptions.DataBindingViolationException;
import com.mevy.restfulapi.services.exceptions.ObjectNotFoundException;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public User findById(Long id) {
        UserSpringSecurity userSpringSecurity = authenticated();
        if (!Objects.nonNull(userSpringSecurity)
                || !userSpringSecurity.hasRole(ProfileEnum.ADMIN) && !id.equals(userSpringSecurity.getId())){
            throw new AuthorizationException("Access Denied");
        }
        
        return userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(
            "User not found! id: " + id + ", type: " + User.class.getName()));
    }

    @Transactional
    public User create(User obj){
        obj.setId(null);
        obj.setPassword(bCryptPasswordEncoder.encode(obj.getPassword()));
        obj.setProfiles(Stream.of(ProfileEnum.USER.getCode()).collect(Collectors.toSet()));
        obj = userRepository.save(obj);
        return obj;
    }

    @Transactional
    public User update(User obj){
        User newObj = findById(obj.getId());
        newObj.setPassword(obj.getPassword());
        newObj.setPassword(bCryptPasswordEncoder.encode(obj.getPassword()));
        return userRepository.save(newObj);
    }

    public void delete(Long id){
        try {
            userRepository.deleteById(id);
        } catch(Exception e){
            throw new DataBindingViolationException("Database integrity error. ");
        }
    }

    public static UserSpringSecurity authenticated(){
        try{
            return (UserSpringSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        } catch(Exception e){
            return null;
        }
    }

}
