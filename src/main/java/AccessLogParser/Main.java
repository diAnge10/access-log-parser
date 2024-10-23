package AccessLogParser;


import java.io.File;
import java.util.Scanner;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int counter = 0; // Счётчик корректных файлов
        int googlebotCounter = 0;
        int yandexBotCounter = 0;
        while (true) {
            try {
                System.out.println("Введите путь к файлу или введите 'exit', чтобы выйти");
                String input = scanner.nextLine();

                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Программа завершена.");
                    break;
                }

                File file = new File(input);
                boolean fileExists = file.exists();
                boolean isDirectory = file.isDirectory();

                if (!fileExists || isDirectory) {
                    System.out.println("Указанный путь не существует или это папка. Попробуйте снова.");
                    System.out.println("Общее количество указанных путей " + counter);
                    continue;
                }

                counter++;

                LineIterator it = FileUtils.lineIterator(file, "UTF-8");
                Statistics statistics = new Statistics();
                int lineCounter = 0;

                while (it.hasNext()) {
                    String line = it.nextLine();
                    if (line.length() > 1024) {
                        throw new LongException("Длина строки превышает 1024 символа");
                    }
                    String[] parts = line.split(" ");
                    String ipAddress = parts[0];
                    if (ipAddress.length() > 0) {
                        lineCounter++;
                    }
                    String dateTime = parts[3].substring(1).replace("[", "") + " " + parts[4].replace("]", "");
                    String requestMethod = parts[5].replace("\"", "");
                    String requestPath = parts[6];
                    int responseCode = Integer.parseInt(parts[8]);
                    int dataSize = parts[9].equals("-") ? 0 : Integer.parseInt(parts[9]);
                    String referer = parts.length > 10 ? parts[10].replace("\"", "") : "";

                    // Извлечение User-Agent
                    int secondLastQuoteIndex = line.substring(0, line.lastIndexOf("\"")).lastIndexOf("\"");
                    String userAgent = line.substring(secondLastQuoteIndex);

                    // Создание объекта LogEntry
                    LogEntry logEntry = new LogEntry(ipAddress, dateTime, requestMethod, requestPath,
                            responseCode, dataSize, referer, userAgent);
                    statistics.addEntry(logEntry);
                    // Вывод данных
                    System.out.println("IP-адрес: " + logEntry.getIpAddress());
                    System.out.println("Дата и время запроса: " + logEntry.getDateTime());
                    System.out.println("Метод запроса: " + logEntry.getRequestMethod());
                    System.out.println("Путь запроса: " + logEntry.getRequestPath());
                    System.out.println("Код ответа: " + logEntry.getResponseCode());
                    System.out.println("Размер данных: " + logEntry.getDataSize());
                    System.out.println("Referer: " + logEntry.getReferer());
                    System.out.println("User-Agent: " + logEntry.getUserAgent());
                    int startIndex = userAgent.indexOf("(");
                    int endIndex = userAgent.indexOf(")", startIndex);

                    if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                        String inParentheses = userAgent.substring(startIndex + 1, endIndex);
                        //System.out.println("Часть в скобках из User-Agent: " + inParentheses);
                        String[] parts2 = inParentheses.split(";"); // разделение строки по ;
                        String prebot = parts2.length > 2 ? parts2[1].replace(" ", "") : "";
                        String[] parts3 = prebot.split("/");
                        /*System.out.println("ПреБот :" + prebot);
                        String bot = parts3.length > 2 ? parts3[1].trim();
                        System.out.println("Бот :" + bot);*/
                        if (parts3.length >= 2) {
                            String bot = parts3[0].trim();
                            //System.out.println("ПреБот: " + prebot);
                            System.out.println("Бот: " + bot);
                            if (bot.equals("Googlebot")) {
                                googlebotCounter++;
                            } else {
                                yandexBotCounter++;
                            }
                        }
                    }

                    System.out.println("--------------------------------------------------");


                    // Обработка логики для статистики
                    String bot = extractBot(userAgent);
                    if (bot.equals("Googlebot")) {
                        statistics.incrementGooglebotCount();
                    } else if (bot.equals("YandexBot")) {
                        statistics.incrementYandexBotCount();
                    }

                    // Дополнительная обработка logEntry при необходимости
                }

                System.out.println("Количество запросов от Googlebot: " + statistics.getGooglebotCount());
                System.out.println("Количество запросов от YandexBot: " + statistics.getYandexBotCount());
                System.out.println("Общее количество обработанных строк: " + lineCounter);
                System.out.println("Объём часового траффика: " +statistics.getTrafficRate());

            } catch (Exception e) {
                System.err.println("Ошибка: " + e.getMessage());
            }
        }

        scanner.close();
    }
        private static String extractBot(String userAgent) {
            if (userAgent.contains("Googlebot")) {
                return "Googlebot";
            } else if (userAgent.contains("YandexBot")) {
                return "YandexBot";
            }
            return "Unknown";
        }
}


