package conferenceadmin.conference.Service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private final AuditLogService auditLogService;

    public AuditService(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    public void logCreate(String username, String entityType, String entityId, String entityName, String newValues, HttpServletRequest request, String conferenceCode) {
        String ipAddress = getClientIpAddress(request);
        auditLogService.logActivity(username, ipAddress, "CREATE", entityType, entityId, truncateEntityName(entityName),
                                  truncateChanges("Created new " + entityType.toLowerCase()), null, truncateValue(newValues), conferenceCode);
    }

    public void logUpdate(String username, String entityType, String entityId, String entityName, String changes, String oldValues, String newValues, HttpServletRequest request, String conferenceCode) {
        String ipAddress = getClientIpAddress(request);
        auditLogService.logActivity(username, ipAddress, "UPDATE", entityType, entityId, truncateEntityName(entityName), truncateChanges(changes), truncateValue(oldValues), truncateValue(newValues), conferenceCode);
    }

    public void logDelete(String username, String entityType, String entityId, String entityName, HttpServletRequest request, String conferenceCode) {
        String ipAddress = getClientIpAddress(request);
        auditLogService.logActivity(username, ipAddress, "DELETE", entityType, entityId, truncateEntityName(entityName),
                                  truncateChanges("Deleted " + entityType.toLowerCase()), truncateEntityName(entityName), null, conferenceCode);
    }

    public void logAction(String username, String action, String details, HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        auditLogService.logActivity(username, ipAddress, action, "SpeakerSection", null, null, truncateChanges(details), null, null, null);
    }

    private String truncateValue(String value) {
        if (value == null) return null;
        return value.length() > 500 ? value.substring(0, 497) + "..." : value;
    }

    private String truncateChanges(String changes) {
        if (changes == null) return null;
        return changes.length() > 2000 ? changes.substring(0, 1997) + "..." : changes;
    }

    private String truncateEntityName(String entityName) {
        if (entityName == null) return null;
        return entityName.length() > 1000 ? entityName.substring(0, 997) + "..." : entityName;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }
        return request.getRemoteAddr();
    }
}