package org.olahammed.SpringStarter.repositories;

import java.util.Optional;

import org.olahammed.SpringStarter.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findOneByEmailIgnoreCase(String username);

    Optional<Account> findByToken(String token);
    
}
