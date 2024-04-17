package com.mevy.restfulapi.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mevy.restfulapi.models.Task;
import com.mevy.restfulapi.models.projection.TaskProjection;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<TaskProjection> findByUser_Id(Long id);

}
