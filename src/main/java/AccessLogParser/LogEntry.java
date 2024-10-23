package AccessLogParser;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class LogEntry {
    private String ipAddress;
    private LocalDateTime dateTime;
    private HttpMethod requestMethod;
    private String requestPath;
    private int responseCode;
    private int dataSize;
    private String referer;
    private String userAgent;

    public LogEntry(String ipAddress, String dateTimeString, String requestMethod, String requestPath,
                    int responseCode, int dataSize, String referer, String userAgent) {
        this.ipAddress = ipAddress;

        // Создаем форматтер для парсинга строки даты
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

        try {
            // Парсим строку в OffsetDateTime и затем преобразуем в LocalDateTime
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateTimeString, formatter);
            this.dateTime = offsetDateTime.toLocalDateTime();
        } catch (DateTimeParseException e) {
            System.err.println("Ошибка парсинга даты: " + e.getMessage());
        }

        this.requestMethod = HttpMethod.valueOf(requestMethod);
        this.requestPath = requestPath;
        this.responseCode = responseCode;
        this.dataSize = dataSize;
        this.referer = referer;
        this.userAgent = userAgent;
    }

    // Геттеры для полей
    public String getIpAddress() {
        return ipAddress;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public HttpMethod getRequestMethod() {
        return requestMethod;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getDataSize() {
        return dataSize;
    }

    public String getReferer() {
        return referer;
    }

    public String getUserAgent() {
        return userAgent;
    }
}