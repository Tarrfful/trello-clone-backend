package com.tarfful.trello_clone.repository;

import com.tarfful.trello_clone.model.TaskList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskListRepository extends JpaRepository<TaskList, Long> {
}
