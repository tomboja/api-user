package com.olmaitsolutions.edu.boja.apiusers.apiusers.mapper;

import com.olmaitsolutions.edu.boja.apiusers.apiusers.dto.CreateUserDTO;
import com.olmaitsolutions.edu.boja.apiusers.apiusers.dto.UserDTO;
import com.olmaitsolutions.edu.boja.apiusers.apiusers.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "passwordHash", ignore = true)
    User toEntity(CreateUserDTO dto);

    UserDTO toDto(User user);
}
