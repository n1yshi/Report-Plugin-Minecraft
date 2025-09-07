package de.netami.reportsystem.types;

import org.bukkit.Location;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Report {
    
    private final int id;
    private final UUID reporterId;
    private final String reporterName;
    private final ReportType type;
    private final String title;
    private final String description;
    private final Location location;
    private final LocalDateTime createdAt;
    
    private ReportStatus status;
    private UUID assignedAdmin;
    private String assignedAdminName;
    private String adminResponse;
    private LocalDateTime updatedAt;
    private LocalDateTime closedAt;
    
    // For user reports
    private UUID targetPlayerId;
    private String targetPlayerName;
    
    public Report(int id, UUID reporterId, String reporterName, ReportType type, 
                 String title, String description, Location location) {
        this.id = id;
        this.reporterId = reporterId;
        this.reporterName = reporterName;
        this.type = type;
        this.title = title;
        this.description = description;
        this.location = location;
        this.createdAt = LocalDateTime.now();
        this.status = ReportStatus.OPEN;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Constructor for user reports
    public Report(int id, UUID reporterId, String reporterName, String title, 
                 String description, Location location, UUID targetPlayerId, String targetPlayerName) {
        this(id, reporterId, reporterName, ReportType.USER, title, description, location);
        this.targetPlayerId = targetPlayerId;
        this.targetPlayerName = targetPlayerName;
    }
    
    // Getters
    public int getId() {
        return id;
    }
    
    public UUID getReporterId() {
        return reporterId;
    }
    
    public String getReporterName() {
        return reporterName;
    }
    
    public ReportType getType() {
        return type;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public ReportStatus getStatus() {
        return status;
    }
    
    public UUID getAssignedAdmin() {
        return assignedAdmin;
    }
    
    public String getAssignedAdminName() {
        return assignedAdminName;
    }
    
    public String getAdminResponse() {
        return adminResponse;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public LocalDateTime getClosedAt() {
        return closedAt;
    }
    
    public UUID getTargetPlayerId() {
        return targetPlayerId;
    }
    
    public String getTargetPlayerName() {
        return targetPlayerName;
    }
    
    // Setters
    public void setStatus(ReportStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
        if (status == ReportStatus.CLOSED || status == ReportStatus.RESOLVED) {
            this.closedAt = LocalDateTime.now();
        }
    }
    
    public void setAssignedAdmin(UUID adminId, String adminName) {
        this.assignedAdmin = adminId;
        this.assignedAdminName = adminName;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setAdminResponse(String response) {
        this.adminResponse = response;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void setTargetPlayer(UUID playerId, String playerName) {
        this.targetPlayerId = playerId;
        this.targetPlayerName = playerName;
    }
    
    // Utility methods
    public String getFormattedCreatedAt() {
        return createdAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }
    
    public String getFormattedUpdatedAt() {
        return updatedAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }
    
    public String getFormattedClosedAt() {
        if (closedAt == null) return "Not closed";
        return closedAt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }
    
    public boolean isOpen() {
        return status == ReportStatus.OPEN;
    }
    
    public boolean isInProgress() {
        return status == ReportStatus.IN_PROGRESS;
    }
    
    public boolean isClosed() {
        return status == ReportStatus.CLOSED || status == ReportStatus.RESOLVED;
    }
    
    public boolean hasAssignedAdmin() {
        return assignedAdmin != null;
    }
    
    public boolean hasResponse() {
        return adminResponse != null && !adminResponse.trim().isEmpty();
    }
    
    public String getLocationString() {
        if (location == null) return "Unknown";
        return String.format("World: %s, X: %d, Y: %d, Z: %d", 
            location.getWorld().getName(),
            location.getBlockX(),
            location.getBlockY(),
            location.getBlockZ());
    }
    
    public String getTypeDisplayName() {
        switch (type) {
            case USER:
                return "§c👤 User Report";
            case BUG:
                return "§e🐛 Bug Report";
            case MAPBUG:
                return "§6🗺 Map Bug";
            default:
                return "§7❓ Unknown";
        }
    }
    
    public String getStatusDisplayName() {
        switch (status) {
            case OPEN:
                return "§a📂 Open";
            case IN_PROGRESS:
                return "§e⚠ In Progress";
            case CLOSED:
                return "§c❌ Closed";
            case RESOLVED:
                return "§2✅ Resolved";
            default:
                return "§7❓ Unknown";
        }
    }
    
    @Override
    public String toString() {
        return String.format("Report{id=%d, type=%s, status=%s, reporter=%s, title='%s'}", 
            id, type, status, reporterName, title);
    }
}