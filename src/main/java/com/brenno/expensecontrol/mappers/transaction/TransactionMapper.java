package com.brenno.expensecontrol.mappers.transaction;

import com.brenno.expensecontrol.dto.transaction.TransactionRequest;
import com.brenno.expensecontrol.dto.transaction.TransactionResponse;
import com.brenno.expensecontrol.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    List<TransactionResponse> trasactionEntityToTransactionResponse(List<Transaction> transaction);

    @Mapping(target = "account", ignore = true)
    @Mapping(target = "date", ignore = true)
    Transaction transactionRequestToTransactionEntity(TransactionRequest transactionRequest);
}
