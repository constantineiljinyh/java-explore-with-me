package ru.practicum.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.category.model.Category;
import ru.practicum.event.location.Location;
import ru.practicum.user.model.User;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "initiator_user_id")
    private User initiator;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;

    @NotBlank
    @Column(name = "title")
    private String title;

    @NotBlank
    @Column(name = "annotation")
    private String annotation;

    @NotBlank
    @Column(name = "description")
    private String description;

    @Column(name = "date")
    @NotNull
    private LocalDateTime eventDate;

    @Column(name = "create_date")
    private LocalDateTime createdOn;

    @Column(name = "publish_date")
    private LocalDateTime publishedOn;

    @Column(name = "state")
    @NotNull
    @Enumerated(EnumType.STRING)
    private EventState state;

    @Column(name = "participant_limit")
    private Long participantLimit;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "request_moderation")
    private Boolean requestModeration;
}
