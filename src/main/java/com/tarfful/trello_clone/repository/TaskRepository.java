package com.tarfful.trello_clone.repository;

import com.tarfful.trello_clone.dto.TaskResponse;
import com.tarfful.trello_clone.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT MAX(t.taskOrder) FROM Task t WHERE t.taskList.id = :listId")
    Optional<Integer> findMaxTaskOrderByTaskListId(Long listId);

    List<Task> findByTaskListIdOrderByTaskOrderAsc(Long listId);

    @Modifying
    @Query("UPDATE Task t SET t.taskOrder = t.taskOrder + 1 WHERE t.taskList.id = :listId AND t.taskOrder >= :startPosition")
    void shiftOrdersDown(Long listId, Integer startPosition);

    @Modifying
    @Query("UPDATE Task t SET t.taskOrder = t.taskOrder + 1 WHERE t.taskList.id = :listId AND t.taskOrder > :startPosition")
    void shiftOrdersUp(Long listId, Integer startPosition);

    long countByTaskListId(Long listId);
}
