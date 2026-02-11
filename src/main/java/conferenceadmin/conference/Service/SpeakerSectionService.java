package conferenceadmin.conference.Service;

import conferenceadmin.conference.Entity.Speaker;
import conferenceadmin.conference.Entity.SpeakerSection;
import conferenceadmin.conference.Repository.SpeakerRepository;
import conferenceadmin.conference.Repository.SpeakerSectionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SpeakerSectionService {

    private final SpeakerSectionRepository speakerSectionRepository;
    private final SpeakerRepository speakerRepository;

    public SpeakerSectionService(SpeakerSectionRepository speakerSectionRepository, 
                                  SpeakerRepository speakerRepository) {
        this.speakerSectionRepository = speakerSectionRepository;
        this.speakerRepository = speakerRepository;
    }

    @Transactional
    public SpeakerSection createSection(Long speakerId, String content) {
        Speaker speaker = speakerRepository.findById(speakerId)
                .orElseThrow(() -> new IllegalArgumentException("Speaker not found with id: " + speakerId));

        SpeakerSection section = new SpeakerSection(content);
        section.setSpeaker(speaker);
        
        return speakerSectionRepository.save(section);
    }

    @Transactional
    public List<SpeakerSection> createMultipleSections(Long speakerId, List<SpeakerSectionDTO> sectionsData) {
        Speaker speaker = speakerRepository.findById(speakerId)
                .orElseThrow(() -> new IllegalArgumentException("Speaker not found with id: " + speakerId));

        return sectionsData.stream().map(dto -> {
            SpeakerSection section = new SpeakerSection(dto.getContent());
            section.setSpeaker(speaker);
            section.setPriorities(dto.getPriorities());
            section.setCurrentFocus(dto.getCurrentFocus());
            section.setFutureFocus(dto.getFutureFocus());
            return speakerSectionRepository.save(section);
        }).toList();
    }

    public SpeakerSection getSectionById(Long id) {
        return speakerSectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Section not found with id: " + id));
    }

    public List<SpeakerSection> getAllSections() {
        return speakerSectionRepository.findAll();
    }

    public List<SpeakerSection> getSectionsBySpeakerId(Long speakerId) {
        return speakerSectionRepository.findBySpeakerId(speakerId);
    }

    public List<SpeakerSection> getSectionsBySpeakerSlug(String slug) {
        return speakerSectionRepository.findBySpeakerSlug(slug);
    }

    @Transactional
    public SpeakerSection updateSection(Long id, String content,
                                         String priorities, String currentFocus, String futureFocus) {
        SpeakerSection section = speakerSectionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Section not found with id: " + id));

        if (content != null) {
            section.setContent(content);
        }
        if (priorities != null) {
            section.setPriorities(priorities);
        }
        if (currentFocus != null) {
            section.setCurrentFocus(currentFocus);
        }
        if (futureFocus != null) {
            section.setFutureFocus(futureFocus);
        }

        return speakerSectionRepository.save(section);
    }

    @Transactional
    public void deleteSection(Long id) {
        if (!speakerSectionRepository.existsById(id)) {
            throw new IllegalArgumentException("Section not found with id: " + id);
        }
        speakerSectionRepository.deleteById(id);
    }

    // DTO class for bulk creation
    public static class SpeakerSectionDTO {
        private String content;
        private String priorities;
        private String currentFocus;
        private String futureFocus;

        public SpeakerSectionDTO() {}

        public SpeakerSectionDTO(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getPriorities() {
            return priorities;
        }

        public void setPriorities(String priorities) {
            this.priorities = priorities;
        }

        public String getCurrentFocus() {
            return currentFocus;
        }

        public void setCurrentFocus(String currentFocus) {
            this.currentFocus = currentFocus;
        }

        public String getFutureFocus() {
            return futureFocus;
        }

        public void setFutureFocus(String futureFocus) {
            this.futureFocus = futureFocus;
        }
    }
}
