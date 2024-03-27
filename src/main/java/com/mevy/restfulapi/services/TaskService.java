package com.mevy.restfulapi.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mevy.restfulapi.models.Task;
import com.mevy.restfulapi.models.User;
import com.mevy.restfulapi.repositories.TaskRepository;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public Task findById(Long id){
        return taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found. id: " + id));
    }

    public List<Task> findAllVByUserId(Long userId){
        List<Task> tasks = taskRepository.findByUser_Id(userId);
        return tasks;
    }

    @Transactional
    public Task create(Task obj){
        User user = userService.findById(obj.getUser().getId());
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
            throw new RuntimeException("Database integrity error. ");
        }
    }

}
