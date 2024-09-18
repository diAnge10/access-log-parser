package AccessLogParser;

import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int counter = 0; // Счётчик корректных файлов

        while (true) {
            System.out.println("Введите путь к файлу");
            String path = scanner.nextLine();

            File file = new File(path);
            boolean fileExists = file.exists(); // Проверка существования файла
            boolean isDirectory = file.isDirectory(); // Проверка, является путь папкой

            // Проверка входных данных
            if (!fileExists || isDirectory) {
                System.out.println("Указанный путь не существует или это папка. Попробуйте снова.");
                System.out.println("Общее количество указанных путей " + counter);
                continue;
            }
            // Если файл существует и это файл
            counter++;
            System.out.println("Путь указан верно. Это файл номер " + counter);
        }
    }
}

