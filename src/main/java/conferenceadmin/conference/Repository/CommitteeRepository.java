package conferenceadmin.conference.Repository;

import conferenceadmin.conference.Entity.Committee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommitteeRepository extends JpaRepository<Committee, Long> {
    List<Committee> findByConferencecodeOrderByOrderIndexAsc(String conferencecode);
    List<Committee> findByConferencecode(String conferencecode);
}
