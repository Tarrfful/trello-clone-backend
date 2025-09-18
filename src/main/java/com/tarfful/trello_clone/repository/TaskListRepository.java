package com.tarfful.trello_clone.repository;

import com.tarfful.trello_clone.model.TaskList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskListRepository extends JpaRepository<TaskList, Long> {
    @Query("SELECT MAX(tl.listOrder) FROM TaskList tl WHERE tl.board.id = :boardId")
    Optional<Integer> findMaxListOrderByBoardId(Long boardId);

    List<TaskList> findByBoardIdOrderByListOrderAsc(Long boardId);
}
