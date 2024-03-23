package ru.test.nexigntest.generator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.test.nexigntest.db.entity.Customer;
import ru.test.nexigntest.db.entity.CustomerCall;
import ru.test.nexigntest.db.repo.CustomerCallRepo;
import ru.test.nexigntest.db.repo.CustomerRepo;
import ru.test.nexigntest.util.FileUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.concurrent.ThreadLocalRandom.current;

@Service
@RequiredArgsConstructor
@Slf4j
public class Generator {
    private final CustomerRepo customerRepo;
    private final CustomerCallRepo customerCallRepo;

    /**
     * Генератор тестовых абонентов и их звонков
     */
    public void generateTestData() {
        log.info("Генерируется тестовая информация");
        var customers = generateCustomers();
        customerRepo.saveAll(customers);

        Collections.shuffle(customers);

        for (int month = 1; month <= 12; month++) {
            log.info("Генерация звонков для месяца {}", month);
            var monthCalls = new ArrayList<CustomerCall>();
            for (var cust : customers) {
                monthCalls.addAll(generateCalls(cust.getNumber(), month));
            }
            Collections.shuffle(monthCalls);
            log.info("По всем абонентам сгенерировано {} звонков за месяц", monthCalls.size());

            saveCdrToFile(monthCalls, month);

            customerCallRepo.saveAll(monthCalls);
        }
    }

    /**
     * Генерирует 10 абонентов
     *
     * @return
     */
    public List<Customer> generateCustomers() {
        var result = new ArrayList<Customer>();

        for (int i = 1; i <= 10; i++) {
            result.add(Customer.builder().name("Тест " + i).number(79876543200L + i).build());
        }

        return result;
    }

    /**
     * Генерирует абонентские вызовы для указанного номера и для указанного месяца
     *
     * @param number номер абонента
     * @param month  номер месяца
     * @return список вызовов абонента
     */
    public List<CustomerCall> generateCalls(Long number, Integer month) {
        var result = new ArrayList<CustomerCall>();

        var count = current().nextInt(10, 100); //количество вызовов от 10 до 100
        var m = LocalDateTime.now().minusMonths(12L - month); //определяем дату на нужный месяц

        for (int i = 0; i < count; i++) {
            var len = current().nextLong(1, 60); //длительность звонка от 1 до 60 секунд
            var randomSeconds = current().nextLong(1, 60);
            result.add(
                    CustomerCall.builder()
                            .callType(String.format("%02d", current().nextInt(1, 3)))
                            .customerId(number)
                            .startTime(m.plusSeconds(randomSeconds).toEpochSecond(ZoneOffset.UTC))
                            .endTime(m.plusSeconds(randomSeconds).plusSeconds(len).toEpochSecond(ZoneOffset.UTC))
                            .build()
            );
        }

        return result;
    }

    /**
     * Сохранение в файл списка вызовов
     *
     * @param calls список вызовов
     * @param month номер месяца
     */
    private void saveCdrToFile(List<CustomerCall> calls, Integer month) {
        try {
            var writer = new PrintWriter(FileUtils.generateFileName(month), StandardCharsets.UTF_8);
            calls.forEach(c -> {
                writer.print(c.getCallType());
                writer.print(", ");
                writer.print(c.getCustomerId());
                writer.print(", ");
                writer.print(c.getStartTime());
                writer.print(", ");
                writer.println(c.getEndTime());
            });
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
