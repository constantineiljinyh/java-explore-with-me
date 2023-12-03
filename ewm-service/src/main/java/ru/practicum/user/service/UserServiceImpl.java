package ru.practicum.user.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import ru.practicum.exception.NotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.dto.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers(List<Long> id, int from, int size) {
        Pageable pageable = PageRequest.of(from, size);
        Page<User> page;
        if (!CollectionUtils.isEmpty(id)) {
            page = userRepository.findAllByIdIn(id, pageable);
        } else {
            page = userRepository.findAll(pageable);
        }
        return page.isEmpty() ? Collections.emptyList() :
                page.getContent().stream()
                        .map(userMapper::toUserDto)
                        .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        User saveUser = userRepository.save(userMapper.toUser(userDto));
        return userMapper.toUserDto(saveUser);
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }

    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Юзер с таким id= " + id + " не найден."));
    }
}
