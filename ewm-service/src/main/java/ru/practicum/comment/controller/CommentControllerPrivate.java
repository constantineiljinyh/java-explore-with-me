package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentNewDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/users/{userId}/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentControllerPrivate {

    private final CommentService commentService;

    @GetMapping()
    public List<CommentDto> getAllCommentByUser(@PathVariable long userId,
                                                @Valid @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                @Valid @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Поступил запрос на получение комментариев пользователя с id{}", userId);
        return commentService.getAllCommentByUser(userId, from, size);
    }

    @PostMapping("/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable long userId,
                                    @PathVariable long eventId,
                                    @Valid @RequestBody CommentNewDto commentNewDto) {
        log.info("Поступил запрос на создание комментария пользователем с id{}", userId);
        return commentService.createComment(userId, eventId, commentNewDto);
    }

    @PatchMapping("/{commentId}")
    public CommentDto updateCommentByUser(@PathVariable long userId,
                                          @PathVariable long commentId,
                                          @Valid @RequestBody CommentNewDto commentNewDto) {
        log.info("Поступил запрос на обновления комментария с id{} пользователем с userId{}", commentId, userId);
        return commentService.updateCommentByUser(userId, commentId, commentNewDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByUser(@PathVariable long userId, @PathVariable long commentId) {
        log.info("Поступил запрос на удаление комментария с id{} пользователем с userId{}", commentId, userId);
        commentService.deleteCommentByUser(userId, commentId);
    }
}