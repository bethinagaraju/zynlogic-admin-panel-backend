package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.AuditLog;
import conferenceadmin.conference.Service.AuditLogService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/audit")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllAuditLogs() {
        List<AuditLog> logs = auditLogService.getAllAuditLogs();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByUsername(@PathVariable String username) {
        List<AuditLog> logs = auditLogService.getAuditLogsByUsername(username);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/entity/{entityType}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByEntityType(@PathVariable String entityType) {
        List<AuditLog> logs = auditLogService.getAuditLogsByEntityType(entityType);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/action/{action}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByAction(@PathVariable String action) {
        List<AuditLog> logs = auditLogService.getAuditLogsByAction(action);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<AuditLog>> getAuditLogsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<AuditLog> logs = auditLogService.getAuditLogsBetweenDates(startDate, endDate);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/user/{username}/date-range")
    public ResponseEntity<List<AuditLog>> getAuditLogsByUsernameAndDates(
            @PathVariable String username,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<AuditLog> logs = auditLogService.getAuditLogsByUsernameAndDates(username, startDate, endDate);
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/conference/{conferenceCode}")
    public ResponseEntity<List<AuditLog>> getAuditLogsByConferenceCode(@PathVariable String conferenceCode) {
        List<AuditLog> logs = auditLogService.getAuditLogsByConferenceCode(conferenceCode);
        return ResponseEntity.ok(logs);
    }
}