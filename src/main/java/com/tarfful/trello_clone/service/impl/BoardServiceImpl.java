package com.tarfful.trello_clone.service.impl;

import com.tarfful.trello_clone.dto.BoardResponse;
import com.tarfful.trello_clone.dto.CreateBoardRequest;
import com.tarfful.trello_clone.model.Board;
import com.tarfful.trello_clone.model.User;
import com.tarfful.trello_clone.repository.BoardRepository;
import com.tarfful.trello_clone.repository.UserRepository;
import com.tarfful.trello_clone.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BoardResponse createBoard(CreateBoardRequest request){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Board newBoard = Board.builder()
                .name(request.name())
                .description(request.description())
                .owner(currentUser)
                .members(Set.of(currentUser))
                .build();

        Board savedBoard = boardRepository.save(newBoard);

        BoardResponse.OwnerResponse ownerResponse = new BoardResponse.OwnerResponse(
                currentUser.getId(),
                currentUser.getUsername()
        );

        return new BoardResponse(
                savedBoard.getId(),
                savedBoard.getName(),
                savedBoard.getDescription(),
                ownerResponse
        );

    }

    @Override
    @Transactional(readOnly = true)
    public List<BoardResponse> getUserBoards(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User currentUser = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<Board> boards = boardRepository.findByMembersId(currentUser.getId());

        return boards.stream()
                .map(this::mapBoardToBoardResponse)
                .collect(Collectors.toList());
    }

    private BoardResponse mapBoardToBoardResponse(Board board){
        BoardResponse.OwnerResponse ownerResponse = new BoardResponse.OwnerResponse(
                board.getOwner().getId(),
                board.getOwner().getUsername()
        );

        return new BoardResponse(
                board.getId(),
                board.getName(),
                board.getDescription(),
                ownerResponse
        );
    }
}
