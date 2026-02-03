// package conferenceadmin.conference.Controller;

// import conferenceadmin.conference.Entity.Agenda;
// import conferenceadmin.conference.Service.AgendaService;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.Map;

// @RestController
// @RequestMapping("/api/agendas")
// public class AgendaController {

//     private final AgendaService agendaService;

//     public AgendaController(AgendaService agendaService) {
//         this.agendaService = agendaService;
//     }

//     @PostMapping
//     public ResponseEntity<?> addAgenda(@RequestParam("conferencecode") String conferencecode,
//                                        @RequestParam("day") String day,
//                                        @RequestParam("time") String time,
//                                        @RequestParam("title") String title,
//                                        @RequestParam("description") String description,
//                                        @RequestParam(value = "speaker", required = false) String speaker,
//                                        @RequestParam("room") String room) {
//         try {
//             Agenda saved = agendaService.saveAgenda(conferencecode, day, time, title, description, speaker, room);
//             return ResponseEntity.status(HttpStatus.CREATED).body(saved);
//         } catch (IllegalArgumentException e) {
//             return ResponseEntity.badRequest().body(e.getMessage());
//         }
//     }

//     @PutMapping("/{id}")
//     public ResponseEntity<?> updateAgenda(@PathVariable("id") Long id,
//                                           @RequestParam(value = "conferencecode", required = false) String conferencecode,
//                                           @RequestParam(value = "day", required = false) String day,
//                                           @RequestParam(value = "time", required = false) String time,
//                                           @RequestParam(value = "title", required = false) String title,
//                                           @RequestParam(value = "description", required = false) String description,
//                                           @RequestParam(value = "speaker", required = false) String speaker,
//                                           @RequestParam(value = "room", required = false) String room) {
//         try {
//             Agenda updated = agendaService.updateAgenda(id, conferencecode, day, time, title, description, speaker, room);
//             return ResponseEntity.ok(updated);
//         } catch (IllegalArgumentException e) {
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//         }
//     }

//     @GetMapping("/{id}")
//     public ResponseEntity<?> getAgendaById(@PathVariable("id") Long id) {
//         try {
//             Agenda agenda = agendaService.getAgendaById(id);
//             return ResponseEntity.ok(agenda);
//         } catch (IllegalArgumentException e) {
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//         }
//     }

//     @GetMapping("/conference/{conferencecode}")
//     public ResponseEntity<?> getAgendasByConferencecode(@PathVariable("conferencecode") String conferencecode) {
//         return ResponseEntity.ok(agendaService.getAgendasByConferencecode(conferencecode));
//     }

//     @GetMapping("/conference/{conferencecode}/{day}")
//     public ResponseEntity<?> getAgendasByConferencecodeAndDay(@PathVariable("conferencecode") String conferencecode, @PathVariable("day") String day) {
//         return ResponseEntity.ok(agendaService.getAgendasByConferencecodeAndDay(conferencecode, day));
//     }

//     @DeleteMapping("/{id}")
//     public ResponseEntity<?> deleteAgenda(@PathVariable("id") Long id) {
//         try {
//             agendaService.deleteAgenda(id);
//             return ResponseEntity.noContent().build();
//         } catch (IllegalArgumentException e) {
//             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//         }
//     }

//     @PutMapping("/reorder")
//     public ResponseEntity<?> reorderAgendas(@RequestBody Map<String, Object> payload) {
//         try {
//             String day = (String) payload.get("day");
//             @SuppressWarnings("unchecked")
//             List<Long> agendaIdsInOrder = (List<Long>) payload.get("ids");
//             agendaService.reorderAgendas(day, agendaIdsInOrder);
//             return ResponseEntity.ok("Agendas reordered successfully");
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reorder agendas: " + e.getMessage());
//         }
//     }
// }



package conferenceadmin.conference.Controller;

import conferenceadmin.conference.Entity.Agenda;
import conferenceadmin.conference.Service.AgendaService;
import conferenceadmin.conference.Service.AuditService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agendas")
@CrossOrigin // Ensure frontend can access this
public class AgendaController {

    private final AgendaService agendaService;
    private final AuditService auditService;

    public AgendaController(AgendaService agendaService, AuditService auditService) {
        this.agendaService = agendaService;
        this.auditService = auditService;
    }

    // ... (Your other existing methods: POST, PUT, GET, DELETE) ...
    // Keep them exactly as they were, I am only showing the fixed REORDER method below

    @PostMapping
    @CacheEvict(value = "conferenceData", allEntries = true)
    public ResponseEntity<?> addAgenda(@RequestParam("conferencecode") String conferencecode,
                                       @RequestParam("day") String day,
                                       @RequestParam("time") String time,
                                       @RequestParam("title") String title,
                                       @RequestParam("description") String description,
                                       @RequestParam(value = "speaker", required = false) String speaker,
                                       @RequestParam("room") String room,
                                       @RequestParam("username") String username,
                                       HttpServletRequest request) {
        try {
            Agenda saved = agendaService.saveAgenda(conferencecode, day, time, title, description, speaker, room);
            String newValues = String.format("Title: %s, Day: %s, Time: %s, Room: %s, Conference: %s",
                                           title, day, time, room, conferencecode);
            auditService.logCreate(username, "Agenda", saved.getId().toString(), title, newValues, request, conferencecode);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @CacheEvict(value = "conferenceData", allEntries = true)
    public ResponseEntity<?> updateAgenda(@PathVariable("id") Long id,
                                          @RequestParam(value = "conferencecode", required = false) String conferencecode,
                                          @RequestParam(value = "day", required = false) String day,
                                          @RequestParam(value = "time", required = false) String time,
                                          @RequestParam(value = "title", required = false) String title,
                                          @RequestParam(value = "description", required = false) String description,
                                          @RequestParam(value = "speaker", required = false) String speaker,
                                          @RequestParam(value = "room", required = false) String room,
                                          @RequestParam("username") String username,
                                          HttpServletRequest request) {
        try {
            // Get the agenda before update for comparison
            Agenda existingAgenda = agendaService.getAgendaById(id);

            // Update the agenda
            Agenda updated = agendaService.updateAgenda(id, conferencecode, day, time, title, description, speaker, room);

            // Always log the update attempt with details of what was requested to change
            String requestedChanges = buildRequestedChangesString(conferencecode, day, time, title, description, speaker, room);
            String currentValues = buildCurrentValuesString(existingAgenda);
            String updatedValues = buildUpdatedValuesString(updated);

            // Detect actual changes by comparing what was requested vs what actually changed
            String actualChanges = buildActualChangesDescription(existingAgenda, updated, conferencecode, day, time, title, description, speaker, room);

            auditService.logUpdate(username, "Agenda", id.toString(), updated.getTitle(),
                                 actualChanges,
                                 currentValues,
                                 updatedValues, request, updated.getConferencecode());

            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAgendaById(@PathVariable("id") Long id) {
        try {
            Agenda agenda = agendaService.getAgendaById(id);
            return ResponseEntity.ok(agenda);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/conference/{conferencecode}")
    public ResponseEntity<?> getAgendasByConferencecode(@PathVariable("conferencecode") String conferencecode) {
        return ResponseEntity.ok(agendaService.getAgendasByConferencecode(conferencecode));
    }

    @GetMapping("/conference/{conferencecode}/{day}")
    public ResponseEntity<?> getAgendasByConferencecodeAndDay(@PathVariable("conferencecode") String conferencecode, @PathVariable("day") String day) {
        return ResponseEntity.ok(agendaService.getAgendasByConferencecodeAndDay(conferencecode, day));
    }

    @DeleteMapping("/{id}")
    @CacheEvict(value = "conferenceData", allEntries = true)
    public ResponseEntity<?> deleteAgenda(@PathVariable("id") Long id, @RequestParam("username") String username, HttpServletRequest request) {
        try {
            Agenda agenda = agendaService.getAgendaById(id);
            agendaService.deleteAgenda(id);
            auditService.logDelete(username, "Agenda", id.toString(), agenda.getTitle(), request, agenda.getConferencecode());
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ---------------------------------------------------------
    //  FIXED REORDER METHOD
    // ---------------------------------------------------------
    @PutMapping("/reorder")
    @CacheEvict(value = "conferenceData", allEntries = true)
    public ResponseEntity<?> reorderAgendas(@RequestBody ReorderRequest request) {
        try {
            // Validate that we have data
            if (request.getDay() == null || request.getIds() == null) {
                return ResponseEntity.badRequest().body("Day and IDs are required");
            }
            
            agendaService.reorderAgendas(request.getDay(), request.getIds());
            return ResponseEntity.ok("Agendas reordered successfully");
        } catch (Exception e) {
            e.printStackTrace(); // This prints the error to your VS Code terminal
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to reorder: " + e.getMessage());
        }
    }

    // Helper Class to safely map JSON to Java
    public static class ReorderRequest {
        private String day;
        private List<Long> ids;

        public String getDay() { return day; }
        public void setDay(String day) { this.day = day; }
        public List<Long> getIds() { return ids; }
        public void setIds(List<Long> ids) { this.ids = ids; }
    }

    private String buildRequestedChangesString(String conferencecode, String day, String time, String title, String description, String speaker, String room) {
        StringBuilder requested = new StringBuilder("Update requested for: ");
        boolean hasChanges = false;

        if (conferencecode != null) {
            requested.append("Conference Code");
            hasChanges = true;
        }
        if (day != null) {
            if (hasChanges) requested.append(", ");
            requested.append("Day");
            hasChanges = true;
        }
        if (time != null) {
            if (hasChanges) requested.append(", ");
            requested.append("Time");
            hasChanges = true;
        }
        if (title != null) {
            if (hasChanges) requested.append(", ");
            requested.append("Title");
            hasChanges = true;
        }
        if (description != null) {
            if (hasChanges) requested.append(", ");
            requested.append("Description");
            hasChanges = true;
        }
        if (speaker != null) {
            if (hasChanges) requested.append(", ");
            requested.append("Speaker");
            hasChanges = true;
        }
        if (room != null) {
            if (hasChanges) requested.append(", ");
            requested.append("Room");
            hasChanges = true;
        }

        return hasChanges ? requested.toString() : "Update requested - no specific fields provided";
    }

    private String buildCurrentValuesString(Agenda agenda) {
        return String.format("Title: %s, Day: %s, Time: %s, Room: %s, Conference: %s",
                           agenda.getTitle(), agenda.getDay(), agenda.getTime(),
                           agenda.getRoom(), agenda.getConferencecode());
    }

    private String buildUpdatedValuesString(Agenda agenda) {
        return String.format("Title: %s, Day: %s, Time: %s, Room: %s, Conference: %s",
                           agenda.getTitle(), agenda.getDay(), agenda.getTime(),
                           agenda.getRoom(), agenda.getConferencecode());
    }

    private String buildActualChangesDescription(Agenda existing, Agenda updated, String requestedConferencecode, String requestedDay, String requestedTime, String requestedTitle, String requestedDescription, String requestedSpeaker, String requestedRoom) {
        StringBuilder description = new StringBuilder();
        boolean hasRequests = false;

        // Check conferencecode
        if (requestedConferencecode != null && !requestedConferencecode.isBlank()) {
            hasRequests = true;
            if (!existing.getConferencecode().equals(updated.getConferencecode())) {
                description.append("Conference Code changed from '").append(existing.getConferencecode()).append("' to '").append(updated.getConferencecode()).append("'");
            } else {
                description.append("Conference Code  updated to '").append(requestedConferencecode).append("' ");
            }
        }

        // Check day
        if (requestedDay != null && !requestedDay.isBlank()) {
            if (hasRequests) description.append("; ");
            hasRequests = true;
            if (!existing.getDay().equals(updated.getDay())) {
                description.append("Day changed from '").append(existing.getDay()).append("' to '").append(updated.getDay()).append("'");
            } else {
                description.append("Day  updated to '").append(requestedDay).append("' ");
            }
        }

        // Check time
        if (requestedTime != null && !requestedTime.isBlank()) {
            if (hasRequests) description.append("; ");
            hasRequests = true;
            if (!existing.getTime().equals(updated.getTime())) {
                description.append("Time changed from '").append(existing.getTime()).append("' to '").append(updated.getTime()).append("'");
            } else {
                description.append("Time  updated to '").append(requestedTime).append("' ");
            }
        }

        // Check title
        if (requestedTitle != null && !requestedTitle.isBlank()) {
            if (hasRequests) description.append("; ");
            hasRequests = true;
            if (!existing.getTitle().equals(updated.getTitle())) {
                description.append("Title changed from '").append(existing.getTitle()).append("' to '").append(updated.getTitle()).append("'");
            } else {
                description.append("Title  updated to '").append(requestedTitle).append("' ");
            }
        }

        // Check description
        if (requestedDescription != null && !requestedDescription.isBlank()) {
            if (hasRequests) description.append("; ");
            hasRequests = true;
            if (!existing.getDescription().equals(updated.getDescription())) {
                description.append("Description changed from '").append(existing.getDescription()).append("' to '").append(updated.getDescription()).append("'");
            } else {
                description.append("Description  updated to '").append(requestedDescription).append("' ");
            }
        }

        // Check speaker
        if (requestedSpeaker != null && !requestedSpeaker.isBlank()) {
            if (hasRequests) description.append("; ");
            hasRequests = true;
            if (!existing.getSpeaker().equals(updated.getSpeaker())) {
                description.append("Speaker changed from '").append(existing.getSpeaker()).append("' to '").append(updated.getSpeaker()).append("'");
            } else {
                description.append("Speaker  updated to '").append(requestedSpeaker).append("' ");
            }
        }

        // Check room
        if (requestedRoom != null && !requestedRoom.isBlank()) {
            if (hasRequests) description.append("; ");
            hasRequests = true;
            if (!existing.getRoom().equals(updated.getRoom())) {
                description.append("Room changed from '").append(existing.getRoom()).append("' to '").append(updated.getRoom()).append("'");
            } else {
                description.append("Room  updated to '").append(requestedRoom).append("' ");
            }
        }

        return hasRequests ? description.toString() : "Update requested - no specific fields provided";
    }
}