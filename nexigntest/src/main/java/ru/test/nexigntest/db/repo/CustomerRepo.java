package ru.test.nexigntest.db.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.test.nexigntest.db.entity.Customer;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Long> {
}
