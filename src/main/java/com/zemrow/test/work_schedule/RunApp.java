package com.zemrow.test.work_schedule;

import java.io.IOException;
import java.util.Scanner;

/**
 * Класс с main методом для запуска приложения
 *
 * @author Alexandr Polyakov on 2018.08.07
 */
public class RunApp {

    public static void main(String[] args) throws ClassNotFoundException, IOException {
        try (final AppContext appContext = new AppContext()) {
            appContext.getDbVersionController().update();
            appContext.getWebServer().start();

            try(final Scanner scanner = new Scanner(System.in)) {
                while (!"exit".equals(scanner.nextLine()));
            }
        }
    }
}
