package com.tarfful.trello_clone.dto;

public record BoardResponse(
        Long id,
        String name,
        String description,
        OwnerResponse owner
) {
    public record OwnerResponse(Long id, String username){}
}
