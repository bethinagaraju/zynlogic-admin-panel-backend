package conferenceadmin.conference.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import conferenceadmin.conference.Entity.Speaker;
import java.util.List;

public interface SpeakerRepository extends JpaRepository<Speaker, Long> {

    List<Speaker> findByConferencecode(String conferencecode);

    @Query("SELECT s FROM Speaker s WHERE s.conferencecode = :conferencecode ORDER BY s.orderIndex ASC")
    List<Speaker> findByConferencecodeOrderByOrderIndex(@Param("conferencecode") String conferencecode);

}
