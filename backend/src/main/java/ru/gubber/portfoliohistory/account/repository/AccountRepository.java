package ru.gubber.portfoliohistory.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gubber.portfoliohistory.account.model.Account;

import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    boolean existsByNameAndNumber(String name, String number);

    Optional<Account> findByBrokerAndNumber(String broker, String number);
}
