package ru.practicum.comment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User user;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @NotBlank
    @Column(name = "text")
    private String text;

    @NotNull
    @Column(name = "timestamp")
    private LocalDateTime createdOn;
}
