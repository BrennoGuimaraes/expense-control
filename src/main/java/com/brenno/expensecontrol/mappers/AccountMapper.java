package com.brenno.expensecontrol.mappers;

import com.brenno.expensecontrol.dto.AccountRequest;
import com.brenno.expensecontrol.dto.AccountResponse;
import com.brenno.expensecontrol.entity.Account;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    Account accountRequestToAccountEntity(AccountRequest accountRequest);

    List<AccountResponse> accountEntityToResponse(List<Account> account);


}
