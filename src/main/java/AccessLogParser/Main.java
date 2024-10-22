package AccessLogParser;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import java.text.DecimalFormat;
import java.io.File;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        DecimalFormat df = new DecimalFormat("#.##");
        Scanner scanner = new Scanner(System.in);
        int counter = 0; // Счётчик корректных файлов

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
                int googlebotCounter = 0;
                int yandexBotCounter = 0;
                int lineCounter = 0;
                while (it.hasNext()) {

                    String line = it.nextLine();
                    if (line.length() > 1024) {
                        throw new LongException("Длина строки превышает 1024 символа");
                    }
                    String[] parts = line.split(" "); // разделение строки по пробелам
                    String[] parts1 = line.split("\""); // разделение строки по кавычкам

                    String ipAddress = parts[0];
                    if (ipAddress.length()>0) {
                        lineCounter = lineCounter +1;
                    }

                    String dateTime = parts[3] + " " + parts[4].replace("[", "");
                    String requestMethod = parts[5].replace("\"", "");
                    String requestPath = parts[6];
                    int responseCode = Integer.parseInt(parts[8]);
                    int dataSize = parts[9].equals("-") ? 0 : Integer.parseInt(parts[9]);
                    String referer = parts.length > 10 ? parts[10].replace("\"", "") : "";
                    // Индекс начала User-Agent (последняя двойная кавычка перед User-Agent)
                    int secondLastQuoteIndex = line.substring(0, line.lastIndexOf("\"")).lastIndexOf("\"");
                    // Извлечение User-Agent начиная с символа следующего после предпоследней двойной кавычки
                    String userAgent = line.substring(secondLastQuoteIndex);

                    // Вывод данных
                    System.out.println("IP-адрес: " + ipAddress);
                    System.out.println("Дата и время запроса: " + dateTime);
                    System.out.println("Метод запроса: " + requestMethod);
                    System.out.println("Путь запроса: " + requestPath);
                    System.out.println("Код ответа: " + responseCode);
                    System.out.println("Размер данных: " + dataSize);
                    System.out.println("Referer: " + referer);
                    System.out.println("User-Agent: " + userAgent);
                    int startIndex = userAgent.indexOf("(");
                    int endIndex = userAgent.indexOf(")", startIndex);

                    if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                        String inParentheses = userAgent.substring(startIndex + 1, endIndex);
                        //System.out.println("Часть в скобках из User-Agent: " + inParentheses);
                        String[] parts2 = inParentheses.split(";"); // разделение строки по ;
                        String prebot = parts2.length > 2 ? parts2[1].replace(" ", ""): "";
                        String[] parts3 = prebot.split("/");
                        /*System.out.println("ПреБот :" + prebot);
                        String bot = parts3.length > 2 ? parts3[1].trim();
                        System.out.println("Бот :" + bot);*/
                        if (parts3.length >= 2) {
                            String bot = parts3[0].trim();
                            //System.out.println("ПреБот: " + prebot);
                            System.out.println("Бот: " + bot);
                            if(bot.equals("Googlebot")) {
                                googlebotCounter++;
                            } else {
                                yandexBotCounter++;
                            }
                        }
                    }

                    System.out.println("--------------------------------------------------");
                }
                System.out.println("Общее число строк: " + lineCounter);
                System.out.println("Кол-во запросов из Яндекса: " + yandexBotCounter);
                System.out.println("Кол-во запросов из Google: " + googlebotCounter);
                System.out.println("Доля запросов от Google: " + df.format(((double)googlebotCounter / lineCounter) * 100) + "%");
                System.out.println("Доля запросов от Яндекса: " + df.format(((double)yandexBotCounter / lineCounter) * 100) + "%");
                System.out.println("Файл обработан успешно");
                System.out.println("Путь указан верно. Это файл номер " + counter);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}