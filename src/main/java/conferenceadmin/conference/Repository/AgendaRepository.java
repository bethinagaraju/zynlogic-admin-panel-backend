// package conferenceadmin.conference.Repository;

// import conferenceadmin.conference.Entity.Agenda;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;
// import org.springframework.data.repository.query.Param;
// import org.springframework.stereotype.Repository;

// import java.util.List;

// @Repository
// public interface AgendaRepository extends JpaRepository<Agenda, Long> {

//     @Query("SELECT a FROM Agenda a WHERE a.conferencecode = :conferencecode AND a.day = :day ORDER BY a.orderIndex ASC")
//     List<Agenda> findByConferencecodeAndDayOrderByOrderIndex(@Param("conferencecode") String conferencecode, @Param("day") String day);

//     List<Agenda> findByConferencecode(String conferencecode);
// }



package conferenceadmin.conference.Repository;

import conferenceadmin.conference.Entity.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AgendaRepository extends JpaRepository<Agenda, Long> {
    
    // Existing method (keep this if used elsewhere, or replace usage)
    List<Agenda> findByConferencecode(String conferencecode);

    // *** ADD THIS NEW METHOD ***
    // This forces the database to return items sorted by their orderIndex
    List<Agenda> findByConferencecodeOrderByOrderIndexAsc(String conferencecode);

    // Keep your existing specific day method
    List<Agenda> findByConferencecodeAndDayOrderByOrderIndex(String conferencecode, String day);
}