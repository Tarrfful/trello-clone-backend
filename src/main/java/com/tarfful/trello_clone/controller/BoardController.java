package com.tarfful.trello_clone.controller;

import com.tarfful.trello_clone.dto.BoardResponse;
import com.tarfful.trello_clone.dto.CreateBoardRequest;
import com.tarfful.trello_clone.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<BoardResponse> createBoard(@RequestBody CreateBoardRequest request){
        BoardResponse createdBoard = boardService.createBoard(request);
        return new ResponseEntity<>(createdBoard, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BoardResponse>> getUserBoards(){
        List<BoardResponse> boards = boardService.getUserBoards();
        return ResponseEntity.ok(boards);
    }
}
