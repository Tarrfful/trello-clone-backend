package com.tarfful.trello_clone.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"owner", "members", "taskLists"})
@Entity
@Table(name = "boards")
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

     @ManyToMany
     @JoinTable(
             name = "board_members",
             joinColumns = @JoinColumn(name = "board_id"),
             inverseJoinColumns = @JoinColumn(name = "user_id"))
     @Builder.Default
     private Set<User> members = new HashSet<>();

     @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
     @Builder.Default
     private List<TaskList> taskLists = new ArrayList<>();

}
