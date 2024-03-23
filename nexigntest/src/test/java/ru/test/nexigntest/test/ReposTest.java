package ru.test.nexigntest.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.test.nexigntest.db.entity.Customer;
import ru.test.nexigntest.db.entity.CustomerCall;
import ru.test.nexigntest.db.repo.CustomerCallRepo;
import ru.test.nexigntest.db.repo.CustomerRepo;

@SpringBootTest
public class ReposTest {
    public static final long TEST_NUM = 123L;
    @Autowired
    private CustomerCallRepo customerCallRepo;

    @Autowired
    private CustomerRepo customerRepo;

    @Test
    public void shouldSaveCustomer() {
        var newCustomer = customerRepo.save(Customer.builder().name("Test").number(TEST_NUM).build());
        var dbCustomer = customerRepo.findById(TEST_NUM);
        Assertions.assertNotNull(newCustomer);
        Assertions.assertNotNull(dbCustomer);
    }

    @Test
    public void shouldSaveCustomerCall() {
        var newCustomerCall = customerCallRepo.save(CustomerCall.builder().customerId(TEST_NUM).startTime(TEST_NUM).endTime(TEST_NUM).build());
        var dbCustomerCall = customerCallRepo.findById(newCustomerCall.getId());
        Assertions.assertNotNull(newCustomerCall);
        Assertions.assertNotNull(dbCustomerCall);
    }
}
