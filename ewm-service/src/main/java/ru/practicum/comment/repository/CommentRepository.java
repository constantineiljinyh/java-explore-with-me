package ru.practicum.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.comment.model.Comment;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findAllByEventId(long eventId, Pageable pageable);

    Page<Comment> findAllByUserId(long userId, Pageable pageable);
}
