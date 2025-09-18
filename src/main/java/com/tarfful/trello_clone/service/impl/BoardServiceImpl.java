package com.tarfful.trello_clone.service.impl;

import com.tarfful.trello_clone.dto.BoardResponse;
import com.tarfful.trello_clone.dto.CreateBoardRequest;
import com.tarfful.trello_clone.dto.InviteMemberRequest;
import com.tarfful.trello_clone.dto.UpdateBoardRequest;
import com.tarfful.trello_clone.exception.BoardNotFoundException;
import com.tarfful.trello_clone.exception.InvalidOperationException;
import com.tarfful.trello_clone.exception.UnauthorizedException;
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

        return mapBoardToBoardResponse(savedBoard);

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

        Set<BoardResponse.MemberResponse> memberResponses = board.getMembers().stream()
                .map(member -> new BoardResponse.MemberResponse(member.getId(), member.getUsername()))
                .collect(Collectors.toSet());

        return new BoardResponse(
                board.getId(),
                board.getName(),
                board.getDescription(),
                ownerResponse,
                memberResponses
        );
    }

    @Override
    @Transactional
    public BoardResponse updateBoard(Long boardId, UpdateBoardRequest request){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Board boardToUpdate = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found: " + boardId));

        if (!boardToUpdate.getOwner().getId().equals(currentUser.getId())){
            throw new UnauthorizedException("User is not the owner of the board");
        }

        boardToUpdate.setName(request.name());
        boardToUpdate.setDescription(request.description());

        Board updatedBoard = boardRepository.save(boardToUpdate);

        return mapBoardToBoardResponse(updatedBoard);
    }

    @Override
    @Transactional
    public void deleteBoard(Long boardId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Board boardToDelete = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found: " + boardId));

        if (!boardToDelete.getOwner().getId().equals(currentUser.getId())){
            throw new UnauthorizedException("User is not the owner of the board");
        }

        boardRepository.delete(boardToDelete);
    }

    @Override
    @Transactional
    public BoardResponse inviteMember(Long boardId, InviteMemberRequest request){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("Current user not found"));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found with id: " + boardId));

        if (!board.getOwner().getId().equals(currentUser.getId())){
            throw new UnauthorizedException("Only the board owner can invite members");
        }

        User userToInvite = userRepository.findByUsernameOrEmail(request.email(), request.email())
                .orElseThrow(() -> new UsernameNotFoundException("User to invite not found with email: " + request.email()));

        if (board.getMembers().contains(userToInvite)){
            return mapBoardToBoardResponse(board);
        }

        board.getMembers().add(userToInvite);
        Board updatedBoard = boardRepository.save(board);

        return mapBoardToBoardResponse(updatedBoard);
    }

    @Override
    @Transactional
    public BoardResponse removeMember(Long boardId, Long memberId){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException("Current user not found"));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BoardNotFoundException("Board not found with id: " + boardId));

        if (!board.getOwner().getId().equals(currentUser.getId())){
            throw new UnauthorizedException("Only the board owner can remove members");
        }

        User userToRemove = userRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("User to remove not found with id: " + memberId));

        if (board.getOwner().getId().equals(userToRemove.getId())){
            throw new InvalidOperationException("Board owner cannot remove themselves from the board");
        }

        boolean removed = board.getMembers().removeIf(member -> member.getId().equals(userToRemove.getId()));

        if (!removed){
            throw new InvalidOperationException("User is not a member of this board");
        }

        Board updatedBoard = boardRepository.save(board);

        return mapBoardToBoardResponse(updatedBoard);
    }

}
