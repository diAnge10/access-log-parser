package AccessLogParser;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Statistics {
    private int googlebotCount;
    private int yandexBotCount;
    private int totalTraffic; // Общее количество трафика
    private LocalDateTime minTime; // Минимальное время
    private LocalDateTime maxTime; // Максимальное время
    private Set<String> existingPages; // Существующие страницы
    private HashSet<String> nonExistingPages; // Несуществующие страницы
    private HashMap<String, Integer> osStatistics; // Частота встречаемости каждой операционной системы
    private HashMap<String, Integer> browserStatistics; // Частота встречаемости браузеров
    private int totalVisits = 0;
    private int errorRequests = 0; // Для подсчета ошибочных запросов
    private Set<String> uniqueUserIPs; // Уникальные IP-адреса пользователей
    private Map<Integer, Integer> visitsPerSecond = new HashMap<>();
    private Set<String> referers = new HashSet<>();
    private Map<String, Integer> userVisits = new HashMap<>();

    public Statistics() {
        this.googlebotCount = 0;
        this.yandexBotCount = 0;
        this.totalTraffic = 0;
        this.minTime = null;
        this.maxTime = null;
        this.existingPages = new HashSet<>();
        this.nonExistingPages = new HashSet<>();
        this.osStatistics = new HashMap<>();
        this.browserStatistics = new HashMap<>();
        this.uniqueUserIPs = new HashSet<>(); // Инициализация набора уникальных IP-адресов
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

        // Учитываем существующие и несуществующие страницы
        if (entry.getResponseCode() == 200) {
            existingPages.add(entry.getRequestPath());
        } else if (entry.getResponseCode() == 404) {
            nonExistingPages.add(entry.getRequestPath());
        }

        // Учитываем операционную систему
        String userAgentString = entry.getUserAgent(); // Получаем строку User-Agent
        UserAgent userAgent = new UserAgent(userAgentString); // Создаем объект UserAgent

        String os = userAgent.getOsType();
        String browser = userAgent.getBrowser(); // Получаем тип браузера

        // Обновляем статистику операционных систем
        osStatistics.put(os, osStatistics.getOrDefault(os, 0) + 1);

        // Обновляем статистику браузеров
        browserStatistics.put(browser, browserStatistics.getOrDefault(browser, 0) + 1);

        // Учитываем посещения только от реальных пользователей
        String ipAddress = entry.getIpAddress(); // Получаем IP-адрес
        if (!userAgent.isBot(userAgentString)) {
            totalVisits++;
            uniqueUserIPs.add(ipAddress); // Добавляем IP-адрес в уникальные IP
        }

        // Учитываем ошибочные запросы
        if (entry.getResponseCode() >= 400) {
            errorRequests++;
        }

        int second = entry.getDateTime().getSecond();
        visitsPerSecond.put(second, visitsPerSecond.getOrDefault(second, 0) + 1);


        String referer = entry.getReferer();
        if (referer != null && !referer.isEmpty()) {
            String domain = getDomain(referer); // Используем метод getDomain для извлечения домена
            referers.add(domain);
        }



        if (!userAgent.isBot(entry.getUserAgent())) {
            userVisits.put(ipAddress, userVisits.getOrDefault(ipAddress, 0) + 1);
        }
    }

    public Set<String> getExistingPages() {
        return new HashSet<>(existingPages); // Возвращаем копию существующих страниц
    }

    public Set<String> getNonExistingPages() {
        return new HashSet<>(nonExistingPages); // Возвращаем копию несуществующих страниц
    }

    public double getTrafficRate() {
        if (minTime != null && maxTime != null) {
            long hoursDifference = java.time.Duration.between(minTime, maxTime).toHours();
            if (hoursDifference > 0) {
                return (double) (totalTraffic / hoursDifference) * (-1);
            }
        }
        return 0; // Возвращаем 0, если нет данных
    }

    public double getAverageVisitsPerHour() {
        if (minTime != null && maxTime != null) {
            long hoursDifference = java.time.Duration.between(minTime, maxTime).toHours();
            return (hoursDifference > 0) ? (double) totalVisits / hoursDifference : 0.0;
        }
        return 0.0;
    }

    public double getAverageErrorRequestsPerHour() {
        if (minTime != null && maxTime != null) {
            long hoursDifference = java.time.Duration.between(minTime, maxTime).toHours();
            return (hoursDifference > 0) ? (double) errorRequests / hoursDifference : 0.0;
        }
        return 0.0;
    }


    public double getAverageVisitsPerUser () {
        long realUsersCount = uniqueUserIPs.size();
        return (realUsersCount > 0) ? (double) totalVisits / realUsersCount : 0.0;
    }

    public Map<String, Double> getStat(Map<String, Integer> statsMap) {
        int total = statsMap.values().stream().mapToInt(Integer::intValue).sum();

        return statsMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> total > 0 ? entry.getValue() / (double) total : 0.0
                ));
    }

    public Map<String, Double> getOSShare() {
        return getStat(osStatistics);
    }

    public Map<String, Double> getBrowserShare() {
        return getStat(browserStatistics);
    }
    private String getDomain(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            return domain != null ? domain : "";
        } catch (URISyntaxException e) {
            return "";
        }
    }
    public int getPeakVisitsPerSecond() {
        return visitsPerSecond.values().stream().max(Integer::compare).orElse(0);
    }
    public Set<String> getReferers() {
        return referers;
    }
    public int getMaxVisitsPerUser() {
        return userVisits.values().stream().max(Integer::compare).orElse(0);
    }
}