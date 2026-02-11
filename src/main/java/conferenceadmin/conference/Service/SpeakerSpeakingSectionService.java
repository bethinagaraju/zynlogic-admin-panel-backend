package conferenceadmin.conference.Service;

import conferenceadmin.conference.Entity.Speaker;
import conferenceadmin.conference.Entity.SpeakerSpeakingSection;
import conferenceadmin.conference.Repository.SpeakerRepository;
import conferenceadmin.conference.Repository.SpeakerSpeakingSectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SpeakerSpeakingSectionService {

    private final SpeakerSpeakingSectionRepository repository;
    private final SpeakerRepository speakerRepository;

    public SpeakerSpeakingSectionService(SpeakerSpeakingSectionRepository repository, SpeakerRepository speakerRepository) {
        this.repository = repository;
        this.speakerRepository = speakerRepository;
    }

    @Transactional
    public SpeakerSpeakingSection createSection(Long speakerId, String title, String description, String date) {
        Speaker speaker = speakerRepository.findById(speakerId)
                .orElseThrow(() -> new IllegalArgumentException("Speaker not found with id: " + speakerId));

        SpeakerSpeakingSection section = new SpeakerSpeakingSection();
        section.setTitle(title);
        section.setDescription(description);
        section.setDate(date);
        section.setSpeaker(speaker);

        return repository.save(section);
    }

    public List<SpeakerSpeakingSection> getSectionsBySpeakerId(Long speakerId) {
        return repository.findBySpeakerId(speakerId);
    }

    public List<SpeakerSpeakingSection> getAllSections() {
        return repository.findAll();
    }

    public List<SpeakerSpeakingSection> getSectionsBySlug(String slug) {
        return repository.findBySpeakerSlug(slug);
    }

    @Transactional
    public SpeakerSpeakingSection updateSection(Long id, String title, String description, String date) {
        SpeakerSpeakingSection section = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Section not found with id: " + id));

        if (title != null) section.setTitle(title);
        if (description != null) section.setDescription(description);
        if (date != null) section.setDate(date);

        return repository.save(section);
    }

    public void deleteSection(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Section not found with id: " + id);
        }
        repository.deleteById(id);
    }

    public SpeakerSpeakingSection getSectionById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Section not found with id: " + id));
    }
}
