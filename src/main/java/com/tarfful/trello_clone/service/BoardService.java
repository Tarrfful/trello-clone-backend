package com.tarfful.trello_clone.service;

import com.tarfful.trello_clone.dto.BoardResponse;
import com.tarfful.trello_clone.dto.CreateBoardRequest;
import com.tarfful.trello_clone.model.Board;

import java.util.List;

public interface BoardService {
    BoardResponse createBoard(CreateBoardRequest request);

    List<BoardResponse> getUserBoards();
}
