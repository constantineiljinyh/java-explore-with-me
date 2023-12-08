package ru.practicum.comment.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.comment.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "userName", source = "comment.user.name")
    CommentDto toDtoComment(Comment comment);
}
