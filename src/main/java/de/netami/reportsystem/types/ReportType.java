package de.netami.reportsystem.types;

public enum ReportType {
    USER("User Report", "Report a player for misconduct"),
    BUG("Bug Report", "Report a technical issue or bug"),
    MAPBUG("Map Bug", "Report an issue with the map or world");
    
    private final String displayName;
    private final String description;
    
    ReportType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public static ReportType fromString(String str) {
        for (ReportType type : values()) {
            if (type.name().equalsIgnoreCase(str)) {
                return type;
            }
        }
        return null;
    }
}