package org.olahammed.SpringStarter.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.olahammed.SpringStarter.models.Account;
import org.olahammed.SpringStarter.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public Account save(Account account) {
        if(account.getId() == null){
            account.setCreatedAt(LocalDateTime.now());
        }
        return accountRepository.save(account);
    }

    public Optional<Account> getById(Long id){
        return accountRepository.findById(id);
    }


}
