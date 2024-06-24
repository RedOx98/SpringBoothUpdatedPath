package org.olahammed.SpringStarter.repositories;

import org.olahammed.SpringStarter.models.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorityRepository extends JpaRepository<Authority, Long>{
    
}
