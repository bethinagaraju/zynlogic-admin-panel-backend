package conferenceadmin.conference.Entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "speaker_sections")
public class SpeakerSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String priorities;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String currentFocus;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String futureFocus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "speaker_id", nullable = false)
    @JsonBackReference
    private Speaker speaker;

    public SpeakerSection() {
    }

    public SpeakerSection(String content) {
        this.content = content;
    }

    public SpeakerSection(String content, String priorities, String currentFocus, String futureFocus) {
        this.content = content;
        this.priorities = priorities;
        this.currentFocus = currentFocus;
        this.futureFocus = futureFocus;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Speaker getSpeaker() {
        return speaker;
    }

    public void setSpeaker(Speaker speaker) {
        this.speaker = speaker;
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
