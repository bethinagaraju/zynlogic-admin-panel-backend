package conferenceadmin.conference.Service;

import conferenceadmin.conference.Entity.AuditLog;
import conferenceadmin.conference.Repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void logActivity(String username, String ipAddress, String action, String entityType,
                           String entityId, String entityName, String changes, String oldValues, String newValues, String conferenceCode) {
        AuditLog auditLog = new AuditLog(username, LocalDateTime.now(), ipAddress, action,
                                       entityType, entityId, entityName, changes, oldValues, newValues, conferenceCode);
        auditLogRepository.save(auditLog);
    }

    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAllByOrderByTimestampDesc();
    }

    public List<AuditLog> getAuditLogsByUsername(String username) {
        return auditLogRepository.findByUsernameOrderByTimestampDesc(username);
    }

    public List<AuditLog> getAuditLogsByEntityType(String entityType) {
        return auditLogRepository.findByEntityTypeOrderByTimestampDesc(entityType);
    }

    public List<AuditLog> getAuditLogsByAction(String action) {
        return auditLogRepository.findByActionOrderByTimestampDesc(action);
    }

    public List<AuditLog> getAuditLogsBetweenDates(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByTimestampBetween(startDate, endDate);
    }

    public List<AuditLog> getAuditLogsByUsernameAndDates(String username, LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByUsernameAndTimestampBetween(username, startDate, endDate);
    }

    public List<AuditLog> getAuditLogsByConferenceCode(String conferenceCode) {
        return auditLogRepository.findByConferenceCodeOrderByTimestampDesc(conferenceCode);
    }
}