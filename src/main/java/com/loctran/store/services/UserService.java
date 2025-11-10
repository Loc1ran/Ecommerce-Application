package com.loctran.store.services;

import com.loctran.store.dtos.CreateUserRequest;
import com.loctran.store.dtos.UpdateUserRequest;
import com.loctran.store.dtos.UserDTO;
import com.loctran.store.entities.User;
import com.loctran.store.exceptions.ResourceNotFoundException;
import com.loctran.store.mappers.UserMapper;
import com.loctran.store.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDTO> getAllUsers(String sortedBy) {
        if(!Set.of("name", "email").contains(sortedBy)) {
            sortedBy = "name";
        }
        return userRepository.findAll(Sort.by(sortedBy)).stream().map(userMapper::userToUserDTO).toList();
    }

    public UserDTO findUserById(Long id) {
        User user = findUserEntityById(id);

        return userMapper.userToUserDTO(user);
    }

    public UserDTO createUser(CreateUserRequest userCreateRequest) {
        User user = userMapper.toEntity(userCreateRequest);

        userRepository.save(user);

        UserDTO userDTO = userMapper.userToUserDTO(user);
        userDTO.setId(user.getId());

        return userDTO;
    }

    public UserDTO updateUser(Long id, UpdateUserRequest updateUserRequest) {
        User user = findUserEntityById(id);

        userMapper.updateUser(updateUserRequest, user);

        UserDTO userDTO = userMapper.userToUserDTO(user);
        userDTO.setId(id);

        return userDTO;
    }

    public void deleteUser(Long id) {
        User user = findUserEntityById(id);

        userRepository.delete(user);
    }

    private User findUserEntityById(Long id) {
        return userRepository.findById(id).orElseThrow(()->  new ResourceNotFoundException("No user found with id " + id));
    }

}
