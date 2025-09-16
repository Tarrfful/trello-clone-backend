package com.tarfful.trello_clone.repository;

import com.tarfful.trello_clone.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByMembersId(Long userId);
}
