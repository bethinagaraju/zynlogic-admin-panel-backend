// package conferenceadmin.conference.Service;

// import conferenceadmin.conference.Entity.Agenda;
// import conferenceadmin.conference.Repository.AgendaRepository;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import java.util.List;
// import java.util.Optional;

// @Service
// public class AgendaService {

//     private final AgendaRepository repository;

//     public AgendaService(AgendaRepository repository) {
//         this.repository = repository;
//     }

//     public Agenda saveAgenda(String conferencecode, String day, String time, String title, String description, String speaker, String room) {
//         Integer nextOrder = getNextOrderIndex(conferencecode, day);
//         Agenda agenda = new Agenda(conferencecode, day, time, title, description, speaker, room, nextOrder);
//         return repository.save(agenda);
//     }

//     public Agenda updateAgenda(Long id, String conferencecode, String day, String time, String title, String description, String speaker, String room) {
//         Optional<Agenda> opt = repository.findById(id);
//         if (opt.isEmpty()) {
//             throw new IllegalArgumentException("Agenda not found");
//         }
//         Agenda agenda = opt.get();
//         if (conferencecode != null && !conferencecode.isBlank()) {
//             agenda.setConferencecode(conferencecode);
//         }
//         if (day != null && !day.isBlank()) {
//             agenda.setDay(day);
//         }
//         if (time != null && !time.isBlank()) {
//             agenda.setTime(time);
//         }
//         if (title != null && !title.isBlank()) {
//             agenda.setTitle(title);
//         }
//         if (description != null && !description.isBlank()) {
//             agenda.setDescription(description);
//         }
//         if (speaker != null) { // allow empty string
//             agenda.setSpeaker(speaker);
//         }
//         if (room != null && !room.isBlank()) {
//             agenda.setRoom(room);
//         }
//         return repository.save(agenda);
//     }

//     public Agenda getAgendaById(Long id) {
//         return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Agenda not found"));
//     }

//     public List<Agenda> getAgendasByConferencecodeAndDay(String conferencecode, String day) {
//         return repository.findByConferencecodeAndDayOrderByOrderIndex(conferencecode, day);
//     }

//     public List<Agenda> getAgendasByConferencecode(String conferencecode) {
//         return repository.findByConferencecode(conferencecode);
//     }

//     public void deleteAgenda(Long id) {
//         if (!repository.existsById(id)) {
//             throw new IllegalArgumentException("Agenda not found");
//         }
//         repository.deleteById(id);
//     }

//     private Integer getNextOrderIndex(String conferencecode, String day) {
//         List<Agenda> existing = repository.findByConferencecodeAndDayOrderByOrderIndex(conferencecode, day);
//         return existing.isEmpty() ? 1 : existing.get(existing.size() - 1).getOrderIndex() + 1;
//     }

//     @Transactional
//     public void reorderAgendas(String day, List<Long> agendaIdsInOrder) {
//         for (int i = 0; i < agendaIdsInOrder.size(); i++) {
//             Long id = agendaIdsInOrder.get(i);
//             Optional<Agenda> opt = repository.findById(id);
//             if (opt.isPresent()) {
//                 Agenda a = opt.get();
//                 if (day.equals(a.getDay())) {  // Ensure it belongs to the specified day
//                     a.setOrderIndex(i + 1);
//                     repository.save(a);
//                 }
//             }
//         }
//     }
// }



package conferenceadmin.conference.Service;

import conferenceadmin.conference.Entity.Agenda;
import conferenceadmin.conference.Repository.AgendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AgendaService {

    private final AgendaRepository repository;

    public AgendaService(AgendaRepository repository) {
        this.repository = repository;
    }

    // ... (Keep your existing save, update, get, delete methods exactly as they are) ...
    public Agenda saveAgenda(String conferencecode, String day, String time, String title, String description, String speaker, String room) {
        Integer nextOrder = getNextOrderIndex(conferencecode, day);
        Agenda agenda = new Agenda(conferencecode, day, time, title, description, speaker, room, nextOrder);
        return repository.save(agenda);
    }

    public Agenda updateAgenda(Long id, String conferencecode, String day, String time, String title, String description, String speaker, String room) {
        Optional<Agenda> opt = repository.findById(id);
        if (opt.isEmpty()) throw new IllegalArgumentException("Agenda not found");
        Agenda agenda = opt.get();
        if (conferencecode != null && !conferencecode.isBlank()) agenda.setConferencecode(conferencecode);
        if (day != null && !day.isBlank()) agenda.setDay(day);
        if (time != null && !time.isBlank()) agenda.setTime(time);
        if (title != null && !title.isBlank()) agenda.setTitle(title);
        if (description != null && !description.isBlank()) agenda.setDescription(description);
        if (speaker != null) agenda.setSpeaker(speaker);
        if (room != null && !room.isBlank()) agenda.setRoom(room);
        return repository.save(agenda);
    }

    public Agenda getAgendaById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Agenda not found"));
    }

    public List<Agenda> getAgendasByConferencecodeAndDay(String conferencecode, String day) {
        return repository.findByConferencecodeAndDayOrderByOrderIndex(conferencecode, day);
    }

    public List<Agenda> getAgendasByConferencecode(String conferencecode) {
        // return repository.findByConferencecode(conferencecode);
        return repository.findByConferencecodeOrderByOrderIndexAsc(conferencecode);
    }

    public void deleteAgenda(Long id) {
        if (!repository.existsById(id)) throw new IllegalArgumentException("Agenda not found");
        repository.deleteById(id);
    }

    private Integer getNextOrderIndex(String conferencecode, String day) {
        List<Agenda> existing = repository.findByConferencecodeAndDayOrderByOrderIndex(conferencecode, day);
        return existing.isEmpty() ? 1 : existing.get(existing.size() - 1).getOrderIndex() + 1;
    }

    // =========================================================================
    //  FIXED REORDER METHOD
    // =========================================================================
    @Transactional
    public void reorderAgendas(String day, List<Long> agendaIdsInOrder) {
        System.out.println("DEBUG: Processing Reorder for request day: '" + day + "'");

        for (int i = 0; i < agendaIdsInOrder.size(); i++) {
            Long id = agendaIdsInOrder.get(i);
            Optional<Agenda> opt = repository.findById(id);

            if (opt.isPresent()) {
                Agenda a = opt.get();
                
                // Safe comparison: Handle nulls, trim spaces, ignore case
                String requestDay = (day != null) ? day.trim() : "";
                String dbDay = (a.getDay() != null) ? a.getDay().trim() : "";

                // Debug print to check mismatch
                // System.out.println("Comparing ID " + id + ": Req='" + requestDay + "' vs DB='" + dbDay + "'");

                if (requestDay.equalsIgnoreCase(dbDay)) {
                    a.setOrderIndex(i + 1);
                    repository.save(a); // This will now trigger the UPDATE query
                    System.out.println("DEBUG: Updated order for ID " + id + " to " + (i + 1));
                } else {
                    System.err.println("WARNING: Day Mismatch for ID " + id + ". Database has: '" + dbDay + "'");
                }
            } else {
                System.err.println("WARNING: ID " + id + " not found in DB");
            }
        }
    }
}