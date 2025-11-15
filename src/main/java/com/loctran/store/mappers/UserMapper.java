package com.loctran.store.mappers;

import com.loctran.store.dtos.CreateUserRequest;
import com.loctran.store.dtos.UpdateUserRequest;
import com.loctran.store.dtos.UserDTO;
import com.loctran.store.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO userToUserDTO(User user);

    User toEntity(CreateUserRequest userCreateRequest);

    void updateUser(UpdateUserRequest request, @MappingTarget User user);

    UserDTO toUserDTO(User user);
}
