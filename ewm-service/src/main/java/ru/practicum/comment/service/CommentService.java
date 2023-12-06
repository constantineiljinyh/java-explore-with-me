package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentNewDto;

import java.util.List;

public interface CommentService {

    List<CommentDto> getAllCommentByEvent(long eventId, int from, int size);

    List<CommentDto> getAllCommentByUser(long userId, int from, int size);

    CommentDto createComment(long userId, long eventId, CommentNewDto commentNewDto);

    CommentDto updateCommentByUser(long userId, long commentId, CommentNewDto commentNewDto);

    CommentDto updateCommentByAdmin(long commentId, CommentNewDto commentNewDto);

    void deleteCommentByUser(long userId, long commentId);

    void deleteCommentByAdmin(long commentId);
}
