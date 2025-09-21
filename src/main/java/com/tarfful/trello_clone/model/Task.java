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
@ToString(exclude = {"taskList", "assignees"})
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String description;

    private Integer taskOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_list_id", nullable = false)
    private TaskList taskList;

     @ManyToMany
     @JoinTable(
          name = "task_assignees",
          joinColumns = @JoinColumn(name = "task_id"),
          inverseJoinColumns = @JoinColumn(name = "user_id")
     )
     @Builder.Default
     private Set<User> assignees = new HashSet<>();

     @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
     @Builder.Default
    private List<Comment> comments = new ArrayList<>();
}
