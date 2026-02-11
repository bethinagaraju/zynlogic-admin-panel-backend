package conferenceadmin.conference.Repository;

import conferenceadmin.conference.Entity.SpeakerSpeakingSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeakerSpeakingSectionRepository extends JpaRepository<SpeakerSpeakingSection, Long> {

    List<SpeakerSpeakingSection> findBySpeakerId(Long speakerId);

    @Query("SELECT ss FROM SpeakerSpeakingSection ss JOIN FETCH ss.speaker s WHERE s.slug = :slug")
    List<SpeakerSpeakingSection> findBySpeakerSlug(@Param("slug") String slug);
}
