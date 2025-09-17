package com.tarfful.trello_clone.dto;

import java.util.Set;

public record BoardResponse(
        Long id,
        String name,
        String description,
        OwnerResponse owner,
        Set<MemberResponse> members
) {
    public record OwnerResponse(Long id, String username){}

    public record MemberResponse(Long id, String username){}
}
