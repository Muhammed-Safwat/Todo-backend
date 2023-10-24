package com.app.todo.Repository;

import com.app.todo.modules.Customer;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Transactional
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> getByUsername(String username);

    Optional<Customer> getById(UUID id);

}
