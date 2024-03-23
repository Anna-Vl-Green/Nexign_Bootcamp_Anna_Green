package ru.test.nexigntest.db.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.test.nexigntest.db.entity.CustomerCall;

import java.util.UUID;

@Repository
public interface CustomerCallRepo extends JpaRepository<CustomerCall, UUID> {
}
