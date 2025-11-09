package com.loctran.store.services;

import com.loctran.store.dtos.UserDTO;
import com.loctran.store.mappers.UserMapper;
import com.loctran.store.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<UserDTO> findUserById(Long id) {
        var user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userMapper.userToUserDTO(user));
    }

}
