package com.brenno.expensecontrol.mappers.users;

import com.brenno.expensecontrol.dto.users.UsersRequest;
import com.brenno.expensecontrol.entity.Users;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UsersMapper {

    Users usersRequestToUsersEntity(UsersRequest usersRequest);
}
