package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentControllerPublic {

    private final CommentService commentService;

    @GetMapping("/{eventId}")
    public List<CommentDto> getAllCommentByEvent(@PathVariable long eventId,
                                                 @Valid @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                 @Valid @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Поступил запрос на получение комментариев события с id{}", eventId);
        return commentService.getAllCommentByEvent(eventId, from, size);
    }
}
