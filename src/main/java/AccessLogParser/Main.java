package AccessLogParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int counter = 0; // Счётчик корректных файлов

        while (true) {
            try {
                System.out.println("Введите путь к файлу");
                String path = scanner.next(); // Замена nextLine() на next()

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

                FileReader fileReader = new FileReader(path);
                BufferedReader reader = new BufferedReader(fileReader);

                int totalLines = 0;
                int maxLength = Integer.MIN_VALUE;
                int minLength = Integer.MAX_VALUE;
                String line;
                while ((line = reader.readLine()) != null) {
                    totalLines++;
                    int length = line.length();
                    if (length > 1024) {
                        throw new LongException("Длина строки превышает 1024 символа");
                    }
                    if (length > maxLength) {
                        maxLength = length;
                    }
                    if (length < minLength) {
                        minLength = length;
                    }
                }

                System.out.println("Общее количество строк в файле: " + totalLines);
                System.out.println("Длина самой длинной строки в файле: " + maxLength);
                System.out.println("Длина самой короткой строки в файле: " + minLength);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}

