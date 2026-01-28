package conferenceadmin.conference.Repository;

import conferenceadmin.conference.Entity.roboticsTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface roboticsTopicRepository extends JpaRepository<roboticsTopic, Long> {

    List<roboticsTopic> findByConferencecode(String conferencecode);

    @Query("SELECT t FROM roboticsTopic t WHERE t.conferencecode = :conferencecode ORDER BY t.orderIndex ASC")
    List<roboticsTopic> findByConferencecodeOrderByOrderIndex(@Param("conferencecode") String conferencecode);

}