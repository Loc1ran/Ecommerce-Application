package com.loctran.store.mappers;

import com.loctran.store.dtos.UserDTO;
import com.loctran.store.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDTO userToUserDTO(User user);
}
