package conferenceadmin.conference.Repository;

import conferenceadmin.conference.Entity.roboticsAbstractSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface roboticsAbstractSubmissionRepository extends JpaRepository<roboticsAbstractSubmission, Long> {

    List<roboticsAbstractSubmission> findByConferencecode(String conferencecode);

}