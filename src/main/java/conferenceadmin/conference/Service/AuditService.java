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
        auditLogService.logActivity(username, ipAddress, "CREATE", entityType, entityId, entityName,
                                  "Created new " + entityType.toLowerCase(), null, newValues, conferenceCode);
    }

    public void logUpdate(String username, String entityType, String entityId, String entityName, String changes, String oldValues, String newValues, HttpServletRequest request, String conferenceCode) {
        String ipAddress = getClientIpAddress(request);
        auditLogService.logActivity(username, ipAddress, "UPDATE", entityType, entityId, entityName, changes, oldValues, newValues, conferenceCode);
    }

    public void logDelete(String username, String entityType, String entityId, String entityName, HttpServletRequest request, String conferenceCode) {
        String ipAddress = getClientIpAddress(request);
        auditLogService.logActivity(username, ipAddress, "DELETE", entityType, entityId, entityName,
                                  "Deleted " + entityType.toLowerCase(), entityName, null, conferenceCode);
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