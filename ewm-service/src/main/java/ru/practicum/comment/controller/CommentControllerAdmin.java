package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentNewDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/admin/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentControllerAdmin {

    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public CommentDto updateCommentByAdmin(@PathVariable long commentId,
                                           @Valid @RequestBody CommentNewDto commentNewDto) {
        log.info("Поступил запрос на обновления комментария с id{}", commentId);
        return commentService.updateCommentByAdmin(commentId, commentNewDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable long commentId) {
        log.info("Поступил запрос на удаление комментария с id{}", commentId);
        commentService.deleteCommentByAdmin(commentId);
    }
}