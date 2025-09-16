package com.tarfful.trello_clone.service.impl;

import com.tarfful.trello_clone.dto.BoardResponse;
import com.tarfful.trello_clone.dto.CreateBoardRequest;
import com.tarfful.trello_clone.model.Board;
import com.tarfful.trello_clone.model.User;
import com.tarfful.trello_clone.repository.BoardRepository;
import com.tarfful.trello_clone.repository.UserRepository;
import com.tarfful.trello_clone.service.BoardService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

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

}
