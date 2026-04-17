package com.brenno.expensecontrol.mappers.transaction;

import com.brenno.expensecontrol.dto.transaction.TransactionResponse;
import com.brenno.expensecontrol.entity.Transaction;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    List<TransactionResponse> trasactionEntityToTransactionResponse(List<Transaction> transaction);
}
