package com.tarfful.trello_clone.repository;

import com.tarfful.trello_clone.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT MAX(t.taskOrder) FROM Task t WHERE t.taskList.id = :listId")
    Optional<Integer> findMaxTaskOrderByTaskListId(Long listId);
}
