package net.javaguides.springboot.banking.repository;

import net.javaguides.springboot.banking.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
	
}
