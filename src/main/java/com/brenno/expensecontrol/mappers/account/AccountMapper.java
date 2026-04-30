package com.brenno.expensecontrol.mappers.account;

import com.brenno.expensecontrol.dto.account.AccountRequest;
import com.brenno.expensecontrol.entity.Account;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    Account accountRequestToAccountEntity(AccountRequest accountRequest);

//    List<AccountResponse> accountEntityToResponse(List<Account> account);


}
