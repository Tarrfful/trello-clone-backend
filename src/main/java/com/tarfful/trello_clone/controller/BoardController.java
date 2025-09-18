package com.tarfful.trello_clone.controller;

import com.tarfful.trello_clone.dto.BoardResponse;
import com.tarfful.trello_clone.dto.CreateBoardRequest;
import com.tarfful.trello_clone.dto.InviteMemberRequest;
import com.tarfful.trello_clone.dto.UpdateBoardRequest;
import com.tarfful.trello_clone.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PutMapping("/{boardId}")
    public ResponseEntity<BoardResponse> updateBoard(
            @PathVariable Long boardId,
            @RequestBody UpdateBoardRequest request
            ) {
        BoardResponse updateBoard = boardService.updateBoard(boardId, request);
        return ResponseEntity.ok(updateBoard);
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId){
        boardService.deleteBoard(boardId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{boardId}/members")
    public  ResponseEntity<BoardResponse> inviteMember(
            @PathVariable Long boardId,
            @RequestBody InviteMemberRequest request
            ){
        BoardResponse updateBoard = boardService.inviteMember(boardId, request);
        return ResponseEntity.ok(updateBoard);
    }

    @DeleteMapping("/{boardId}/members/{memberId}")
    public ResponseEntity<BoardResponse> removeMember(
            @PathVariable Long boardId,
            @PathVariable Long memberId
    ){
        BoardResponse updatedBoard = boardService.removeMember(boardId, memberId);
        return ResponseEntity.ok(updatedBoard);
    }
}
