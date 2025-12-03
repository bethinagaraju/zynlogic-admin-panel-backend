package conferenceadmin.conference.Repository;

import conferenceadmin.conference.Entity.roboticsImportantDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface roboticsImportantDateRepository extends JpaRepository<roboticsImportantDate, Long> {

    List<roboticsImportantDate> findByConferencecode(String conferencecode);

}
