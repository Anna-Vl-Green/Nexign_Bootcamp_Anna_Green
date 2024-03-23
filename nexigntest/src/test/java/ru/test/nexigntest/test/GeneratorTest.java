package ru.test.nexigntest.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.test.nexigntest.db.repo.CustomerRepo;
import ru.test.nexigntest.generator.Generator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class GeneratorTest {
    @MockBean
    private CustomerRepo customerRepo;

    @Autowired
    private Generator generator;

    @Test
    public void shouldGenerateTenCustomers() {
        var result = generator.generateCustomers();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.size(), 10);
    }

    @Test
    public void shouldGenerateCalls() {
        var result = generator.generateCalls(1L, 1);

        Assertions.assertNotNull(result);
        Assertions.assertNotEquals(result.size(), 0);
    }

//    @Test
//    public void shouldSaveToDb() {
//        when(customerRepo.saveAll(any())).then(null);
//        generator.generateTestData();
//    }
}
