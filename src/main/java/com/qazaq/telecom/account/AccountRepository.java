package com.qazaq.telecom.account;

import jakarta.annotation.Nullable;
import lombok.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {


    Optional<Account> findAccountById(Long id);



}
