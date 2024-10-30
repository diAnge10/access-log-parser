package AccessLogParser;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Statistics {
    private int googlebotCount;
    private int yandexBotCount;
    private int totalTraffic; // Общее количество трафика
    private LocalDateTime minTime; // Минимальное время
    private LocalDateTime maxTime; // Максимальное время
    private Set<String> existingPages; // Существующие страницы
    private HashMap<String, Integer> osStatistics; // Частота встречаемости каждой операционной системы

    // Конструктор без параметров
    public Statistics() {
        this.googlebotCount = 0;
        this.yandexBotCount = 0;
        this.totalTraffic = 0;
        this.minTime = null;
        this.maxTime = null;
        this.existingPages = new HashSet<>();
        this.osStatistics = new HashMap<>();
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

        if (entry.getResponseCode() == 200) {
            existingPages.add(entry.getRequestPath());
        }

        // Учитываем операционную систему
        String userAgentString = entry.getUserAgent(); // Получаем строку User-Agent
        UserAgent userAgent = new UserAgent(userAgentString); // Создаем объект UserAgent
        String os = userAgent.getOsType();
        osStatistics.put(os, osStatistics.getOrDefault(os, 0) + 1);
    }

    public Set<String> getExistingPages() {
        return new HashSet<>(existingPages); // Возвращаем копию существующих страниц
    }

    public double getTrafficRate() {
        if (minTime != null && maxTime != null) {
            long hoursDifference = java.time.Duration.between(minTime, maxTime).toHours();
            if (hoursDifference > 0) {
                return (double) (totalTraffic / hoursDifference) * -1;
            }
        }
        return 0; // Возвращаем 0, если нет данных
    }

    public Map<String, Double> getOSShare() {
        Map<String, Double> osShare = new HashMap<>();
        int totalOSCount = osStatistics.values().stream().mapToInt(Integer::intValue).sum();

        for (Map.Entry<String, Integer> entry : osStatistics.entrySet()) {
            String osName = entry.getKey();
            int count = entry.getValue();
            double share = (totalOSCount > 0) ? (double) count / totalOSCount : 0.0;
            osShare.put(osName, share);
        }

        return osShare; // Возвращаем долю для каждой операционной системы
    }
}