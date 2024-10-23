package AccessLogParser;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Statistics {
    private int googlebotCount;
    private int yandexBotCount;
    private int totalTraffic; // Общее количество трафика
    private LocalDateTime minTime; // Минимальное время
    private LocalDateTime maxTime; // Максимальное время

    // Конструктор без параметров
    public Statistics() {
        this.googlebotCount = 0;
        this.yandexBotCount = 0;
        this.totalTraffic = 0;
        this.minTime = null;
        this.maxTime = null;
    }

    public void incrementGooglebotCount() {
        googlebotCount++;
    }

    public void incrementYandexBotCount() {
        yandexBotCount++;
    }

    public int getGooglebotCount() {
        return googlebotCount;
    }

    public int getYandexBotCount() {
        return yandexBotCount;
    }

    public void addEntry(LogEntry entry) {
        // Проверяем, что entry не null
        if (entry == null) {
            System.out.println("Attempted to add a null LogEntry.");
            return;
        }

        // Получаем размер данных
        int dataSize = entry.getDataSize();



        // Добавляем объем данных к общему трафику
        totalTraffic += dataSize;

        // Используем уже распарсенный LocalDateTime из LogEntry
        LocalDateTime entryTime = entry.getDateTime();

        // Устанавливаем minTime и maxTime
        if (minTime == null || entryTime.isBefore(minTime)) {
            minTime = entryTime;
        }
        if (maxTime == null || entryTime.isAfter(maxTime)) {
            maxTime = entryTime;
        }
    }


    public double getTrafficRate() {
        if (minTime != null && maxTime != null) {
            long hoursDifference = java.time.Duration.between(minTime, maxTime).toHours();
            if (hoursDifference > 0) {
                return (double) (totalTraffic / hoursDifference)*-1;
            }
        }
        return 0; // Возвращаем 0, если нет данных
    }
}