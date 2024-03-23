package ru.test.nexigntest.reporter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.test.nexigntest.model.Cdr;
import ru.test.nexigntest.model.ReportEntry;
import ru.test.nexigntest.model.Udr;
import ru.test.nexigntest.model.UdrTime;
import ru.test.nexigntest.util.FileUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.*;
import static ru.test.nexigntest.util.FileUtils.generateFileName;
import static ru.test.nexigntest.util.FileUtils.saveReportToFile;

@Service
@Slf4j
public class Reporter {
    /**
     * сохраняет все отчеты
     * и выводит в консоль таблицу со всеми абонентами
     * и итоговым временем звонков по всему тарифицируемому периоду каждого абонента
     */
    public void generateReport() {
        log.info("Генерация отчёта по всем абонентам за год");

        for (int month = 1; month <= 12; month++) {
            log.debug("Генерируется отчёт за месяц {}", month);
            var allCalls = FileUtils.parseFile(generateFileName(month)).orElse(new ArrayList<>());
            log.debug("Прочитано {} абонентских вызовов", allCalls.size());
            log.debug("Составляется отчёт");
            //группируем все вызовы по абонентам
            var mappedCalls = allCalls.stream().collect(Collectors.groupingBy(Cdr::getMsisdn));
            for (Long number : mappedCalls.keySet()) {
                generateReport(number.toString(), month);
            }
        }

    }

    /**
     * сохраняет все отчеты и выводит в консоль таблицу по одному абоненту
     * и его итоговому времени звонков в каждом месяце
     *
     * @param msisdn номер абонента
     */
    public void generateReport(String msisdn) {
        log.info("Генерация отчёта по абоненту {} за год", msisdn);
        for (int month = 1; month <= 12; month++) {
            generateReport(msisdn, month);
        }
    }

    /**
     * сохраняет отчет и выводит в консоль таблицу по одному абоненту
     * и его итоговому времени звонков в указанном месяце
     *
     * @param msisdn номер абонента
     * @param month  номер месяца
     */
    public void generateReport(String msisdn, Integer month) {
        log.debug("Генерация отчёта по абоненту {} за месяц {}", msisdn, month);
        var consoleReport = new HashMap<Long, ReportEntry>();
        var report = new ArrayList<Udr>();
        var allCalls = FileUtils.parseFile(generateFileName(month)).orElse(new ArrayList<>());
        log.debug("Прочитано {} абонентских вызовов", allCalls.size());
        //группируем все вызовы по абоненту
        var mappedCalls = allCalls
                .stream()
                .filter(cdr -> cdr.getMsisdn().toString().equalsIgnoreCase(msisdn))
                .collect(Collectors.groupingBy(Cdr::getMsisdn));
        generateReportForNumber(consoleReport, report, mappedCalls);

        saveReportToFile(month, report);

        consoleReport.forEach((n, r) -> {
            log.info("Для абонента {} итого за месяц {} длительность входящих: {} исходящих: {}", n, month, buildTotalTime(r.getInLen()), buildTotalTime(r.getOutLen()));
        });
    }

    private void generateReportForNumber(HashMap<Long, ReportEntry> consoleReport, ArrayList<Udr> report, Map<Long, List<Cdr>> mappedCalls) {
        for (Long number : mappedCalls.keySet()) {
            log.debug("Обработка {} вызовов абонента {}", mappedCalls.get(number).size(), number);
            long inLength = 0L;
            long outLength = 0L;
            for (var cdr : mappedCalls.get(number)) {
                if (cdr.getCallType() == 1) {
                    //out
                    outLength += cdr.getEndTime() - cdr.getStartTime();
                } else {
                    //in
                    inLength += cdr.getEndTime() - cdr.getStartTime();
                }
            }

            log.debug("Длительность входящих звонков: {}", buildTotalTime(inLength));
            log.debug("Длительность исходящих звонков: {}", buildTotalTime(outLength));

            if (!consoleReport.containsKey(number)) {
                consoleReport.put(number, ReportEntry.builder().inLen(inLength).outLen(outLength).build());
            } else {
                var reportEntry = consoleReport.get(number);
                reportEntry.setInLen(reportEntry.getInLen() + inLength);
                reportEntry.setOutLen(reportEntry.getInLen() + outLength);
                consoleReport.put(number, reportEntry);
            }

            report.add(
                    Udr.builder()
                            .msisdn(number.toString())
                            .incomingCall(UdrTime.builder().totalTime(buildTotalTime(inLength)).build())
                            .outcomingCall(UdrTime.builder().totalTime(buildTotalTime(outLength)).build())
                            .build()
            );

        }
    }

    public static String buildTotalTime(long seconds) {
        long dayCount = SECONDS.toDays(seconds);
        long secondsCount = seconds - DAYS.toSeconds(dayCount);
        long hourCount = SECONDS.toHours(secondsCount);
        secondsCount -= HOURS.toSeconds(hourCount);
        long minutesCount = SECONDS.toMinutes(secondsCount);
        secondsCount -= MINUTES.toSeconds(minutesCount);
        return String.format("%02d:%02d:%02d", hourCount, minutesCount, secondsCount);
    }
}
