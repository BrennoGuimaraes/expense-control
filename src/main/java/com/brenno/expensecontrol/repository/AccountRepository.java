package com.brenno.expensecontrol.repository;

import com.brenno.expensecontrol.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {



}
