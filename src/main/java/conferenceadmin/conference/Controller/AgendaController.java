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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agendas")
@CrossOrigin // Ensure frontend can access this
public class AgendaController {

    private final AgendaService agendaService;

    public AgendaController(AgendaService agendaService) {
        this.agendaService = agendaService;
    }

    // ... (Your other existing methods: POST, PUT, GET, DELETE) ...
    // Keep them exactly as they were, I am only showing the fixed REORDER method below

    @PostMapping
    public ResponseEntity<?> addAgenda(@RequestParam("conferencecode") String conferencecode,
                                       @RequestParam("day") String day,
                                       @RequestParam("time") String time,
                                       @RequestParam("title") String title,
                                       @RequestParam("description") String description,
                                       @RequestParam(value = "speaker", required = false) String speaker,
                                       @RequestParam("room") String room) {
        try {
            Agenda saved = agendaService.saveAgenda(conferencecode, day, time, title, description, speaker, room);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAgenda(@PathVariable("id") Long id,
                                          @RequestParam(value = "conferencecode", required = false) String conferencecode,
                                          @RequestParam(value = "day", required = false) String day,
                                          @RequestParam(value = "time", required = false) String time,
                                          @RequestParam(value = "title", required = false) String title,
                                          @RequestParam(value = "description", required = false) String description,
                                          @RequestParam(value = "speaker", required = false) String speaker,
                                          @RequestParam(value = "room", required = false) String room) {
        try {
            Agenda updated = agendaService.updateAgenda(id, conferencecode, day, time, title, description, speaker, room);
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
    public ResponseEntity<?> deleteAgenda(@PathVariable("id") Long id) {
        try {
            agendaService.deleteAgenda(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ---------------------------------------------------------
    //  FIXED REORDER METHOD
    // ---------------------------------------------------------
    @PutMapping("/reorder")
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
}