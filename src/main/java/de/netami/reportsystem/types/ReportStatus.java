package de.netami.reportsystem.types;

public enum ReportStatus {
    OPEN("Open", "§a📂 Open"),
    IN_PROGRESS("In Progress", "§e⚠ In Progress"),
    CLOSED("Closed", "§c❌ Closed"),
    RESOLVED("Resolved", "§2✅ Resolved");
    
    private final String displayName;
    private final String coloredDisplayName;
    
    ReportStatus(String displayName, String coloredDisplayName) {
        this.displayName = displayName;
        this.coloredDisplayName = coloredDisplayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getColoredDisplayName() {
        return coloredDisplayName;
    }
    
    public static ReportStatus fromString(String str) {
        for (ReportStatus status : values()) {
            if (status.name().equalsIgnoreCase(str)) {
                return status;
            }
        }
        return null;
    }
}