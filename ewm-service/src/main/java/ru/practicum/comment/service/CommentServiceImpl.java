package ru.practicum.comment.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.CommentMapper;
import ru.practicum.comment.dto.CommentNewDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final CommentMapper commentMapper;

    @Override
    public List<CommentDto> getAllCommentByEvent(long eventId, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        return commentRepository.findAllByEventId(eventId, pageable).stream()
                .map(commentMapper::toDtoComment)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getAllCommentByUser(long userId, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        findUserById(userId);
        return commentRepository.findAllByUserId(userId, pageable).stream()
                .map(commentMapper::toDtoComment)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto createComment(long userId, long eventId, CommentNewDto commentNewDto) {
        User user = findUserById(userId);
        Event event = findEventById(eventId);

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setEvent(event);
        comment.setText(commentNewDto.getText());
        comment.setCreatedOn(LocalDateTime.now());

        return commentMapper.toDtoComment(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentDto updateCommentByUser(long userId, long commentId, CommentNewDto commentNewDto) {
        findUserById(userId);
        Comment comment = findCommentById(commentId);

        if (!comment.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Юзер с id=" + userId + " не является автором комментария с id=" + commentId);
        }
        Optional.ofNullable(commentNewDto.getText()).ifPresent(comment::setText);

        return commentMapper.toDtoComment(comment);
    }

    @Override
    @Transactional
    public CommentDto updateCommentByAdmin(long commentId, CommentNewDto commentNewDto) {
        Comment comment = findCommentById(commentId);

        Optional.ofNullable(commentNewDto.getText()).ifPresent(comment::setText);

        return commentMapper.toDtoComment(comment);
    }

    @Override
    @Transactional
    public void deleteCommentByUser(long userId, long commentId) {
        findUserById(userId);
        Comment comment = findCommentById(commentId);

        if (!comment.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Юзер с id=" + userId + " не является автором комментария с id=" + commentId);
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void deleteCommentByAdmin(long commentId) {
        findCommentById(commentId);

        commentRepository.deleteById(commentId);
    }

    private Comment findCommentById(long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Комментария с таким id=" + id + " не найдено"));
    }

    private Event findEventById(long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("События с таким id=" + id + " не найдено"));
    }

    private User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователя с таким id=" + id + " не найдено"));
    }
}
