package com.mevy.restfulapi.services;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mevy.restfulapi.models.Task;
import com.mevy.restfulapi.models.User;
import com.mevy.restfulapi.models.enums.ProfileEnum;
import com.mevy.restfulapi.repositories.TaskRepository;
import com.mevy.restfulapi.security.UserSpringSecurity;
import com.mevy.restfulapi.services.exceptions.AuthorizationException;
import com.mevy.restfulapi.services.exceptions.DataBindingViolationException;
import com.mevy.restfulapi.services.exceptions.ObjectNotFoundException;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task findById(Long id){
        Task task = taskRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException(
            "Task not found! id: " + id + ", type: " + Task.class.getName()));
        
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity)
                || !userSpringSecurity.hasRole(ProfileEnum.ADMIN) && !userHasTask(userSpringSecurity, task)){
                    throw new AuthorizationException("Access Denied! ");
        }
        
        return task;
    }

    public List<Task> findAllByUser(){
        UserSpringSecurity userSpringSecurity = UserService.authenticated();
        if (Objects.isNull(userSpringSecurity)){
            throw new AuthorizationException("Access Denied! ");
        }
        List<Task> tasks = taskRepository.findByUser_Id(userSpringSecurity.getId());
        return tasks;
    }

    @Transactional
    public Task create(Task obj){
        UserSpringSecurity userSpringSecurity = UserService.authenticated();;
        if (Objects.isNull(userSpringSecurity)){
            throw new AuthorizationException("Access Denied! ");
        }
        User user = userService.findById(userSpringSecurity.getId());
        obj.setId(null);
        obj.setUser(user);
        obj = taskRepository.save(obj);
        return obj;

    }

    @Transactional
    public Task update(Task obj){
        Task newObj = findById(obj.getId());
        newObj.setDescription(obj.getDescription());
        return taskRepository.save(newObj);
    }

    public void delete(Long id){
        findById(id);
        try{
            taskRepository.deleteById(id);
        } catch (Exception e){
            throw new DataBindingViolationException("Database integrity error. ");
        }
    }

    private Boolean userHasTask(UserSpringSecurity userSpringSecurity, Task task){
        return task.getUser().getId().equals(userSpringSecurity.getId());
    }

}
