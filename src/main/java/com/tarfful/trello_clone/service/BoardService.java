package com.tarfful.trello_clone.service;

import com.tarfful.trello_clone.dto.BoardResponse;
import com.tarfful.trello_clone.dto.CreateBoardRequest;

public interface BoardService {
    BoardResponse createBoard(CreateBoardRequest request);
}
