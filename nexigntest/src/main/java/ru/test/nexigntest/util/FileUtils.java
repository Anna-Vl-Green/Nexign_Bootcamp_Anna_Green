package ru.test.nexigntest.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.test.nexigntest.model.Cdr;
import ru.test.nexigntest.model.Udr;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@UtilityClass
@Slf4j
public class FileUtils {
    /**
     * Чтение файла в коллекцию CDR-ов
     *
     * @param fileName имя файл
     * @return список CDR-ов
     */
    public static Optional<List<Cdr>> parseFile(final String fileName) {
        try {
            var lines = Files.lines(Paths.get(fileName)).collect(Collectors.toList());
            return Optional.of(
                    lines.stream().map(line -> {
                                var split = line.split(",");
                                if (split.length == 4) {
                                    try {
                                        var tp = Byte.valueOf(split[0].trim());
                                        var id = Long.valueOf(split[1].trim());
                                        var st = Long.valueOf(split[2].trim());
                                        var et = Long.valueOf(split[3].trim());
                                        return Cdr.builder().callType(tp).msisdn(id).startTime(st).endTime(et).build();
                                    } catch (NumberFormatException e) {
                                        log.warn("Ошибка чтения числа из строки: {}", line, e);
                                    }
                                } else {
                                    log.warn("Ошибочная строка в файле: {}", line);
                                }
                                return null;
                            })
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList())
            );
        } catch (IOException e) {
            log.error("Ошибка чтения файла", e);
            return Optional.empty();
        }
    }

    /**
     * Генерирует имя файла для выбранного месяца
     *
     * @param month номер меясца
     * @return строка имени файла
     */
    public static String generateFileName(int month) {
        return "cdr_" + month + "_.txt";
    }

    /**
     * Сохранение отчётов по месяцу в json файл
     *
     * @param month  месяц
     * @param report отчёты
     */
    public static void saveReportToFile(int month, ArrayList<Udr> report) {
        var objectMapper = new ObjectMapper();
        log.debug("Сохраняется отчёт в файл");
        int finalMonth = month;
        report.forEach(r -> {
            try {
                new File("reports").mkdirs();
                objectMapper.writeValue(new File("reports/" + r.getMsisdn() + "_" + finalMonth + ".json"), r);
            } catch (IOException e) {
                log.error("Ошибка записи в json файл", e);
            }
        });
    }
}
