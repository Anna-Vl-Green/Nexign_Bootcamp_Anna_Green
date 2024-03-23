package ru.test.nexigntest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.test.nexigntest.generator.Generator;
import ru.test.nexigntest.reporter.Reporter;

@SpringBootApplication
@EnableJpaRepositories
public class Main {
    public static void main(String... args) {
        var context = SpringApplication.run(Main.class, args);
        if (args.length > 0) { //вызываем генератор
            if (args[0].equalsIgnoreCase("gen")) {
                context.getBean(Generator.class).generateTestData();
            } else if (args[0].equalsIgnoreCase("rep")) {
                if (args.length == 2) {
                    context.getBean(Reporter.class).generateReport(args[1]);
                }
                if (args.length == 3) {
                    context.getBean(Reporter.class).generateReport(args[1], Integer.valueOf(args[2]));
                }
            }
        } else { //вызываем отчёты по умолчанию
            context.getBean(Reporter.class).generateReport();
        }
    }
}
