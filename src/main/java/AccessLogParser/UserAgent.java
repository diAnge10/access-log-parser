package AccessLogParser;

public class UserAgent {
    private final String osType;
    private final String browser;

    public UserAgent(String userAgentString) {
        this.osType = extractOsType(userAgentString);
        this.browser = extractBrowser(userAgentString);
    }

    private String extractOsType(String userAgent) {
        if (userAgent.contains("Windows")) {
            return "Windows";
        } else if (userAgent.contains("Mac OS")) {
            return "macOS";
        } else if (userAgent.contains("Linux")) {
            return "Linux";
        }
        return "Unknown";
    }

    private String extractBrowser(String userAgent) {
        if (userAgent.contains("Chrome")) {
            return "Chrome";
        } else if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else if (userAgent.contains("Edge")) {
            return "Edge";
        } else if (userAgent.contains("Opera")) {
            return "Opera";
        }
        return "Other";
    }

    public boolean isBot(String userAgent) {
        return userAgent.toLowerCase().contains("bot");
    }

    // Геттеры
    public String getOsType() {
        return osType;
    }

    public String getBrowser() {
        return browser;
    }
}
