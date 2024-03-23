package ru.test.nexigntest.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.test.nexigntest.reporter.Reporter;

import static ru.test.nexigntest.reporter.Reporter.buildTotalTime;

@SpringBootTest
@Slf4j
public class ReportTest {
    @Autowired
    private Reporter reporter;

    @Test
    public void shouldGenerateReportForCustAndMonth() {

    }

    @Test
    public void shouldCountSeconds() {
        final long second = 1L;
        final long minute = 60L;
        final long tenMinutes = 600L;

        var result = buildTotalTime(second);
        log.info("Result: {}", result);
        Assertions.assertEquals("00:00:01", result);

        result = buildTotalTime(minute);
        log.info("Result: {}", result);
        Assertions.assertEquals("00:01:00", result);

        result = buildTotalTime(tenMinutes);
        log.info("Result: {}", result);
        Assertions.assertEquals("00:10:00", result);

        result = buildTotalTime(second + minute + tenMinutes);
        log.info("Result: {}", result);
        Assertions.assertEquals("00:11:01", result);
    }
}
