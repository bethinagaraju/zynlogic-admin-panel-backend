package conferenceadmin.conference.Entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "speakers")
public class Speaker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String university;

    private String conferencecode;

    private String imagePath;

    private String speakerType;

    @Column(nullable = false)
    private Integer orderIndex;

    @Column(columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean visible = true;

    @Column(unique = true)
    private String slug;

    @Column(nullable = true)
    private String linkedin;

    @Column(nullable = true)
    private String partnerLogo;

    @Column(nullable = true)
    private String speakerrole;

    @OneToMany(mappedBy = "speaker", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<SpeakerSection> sections = new ArrayList<>();

    public Speaker() {
    }

    public Speaker(String name, String university, String conferencecode, String imagePath, String speakerType, Integer orderIndex, String speakerrole) {
        this.name = name;
        this.university = university;
        this.conferencecode = conferencecode;
        this.imagePath = imagePath;
        this.speakerType = speakerType;
        this.orderIndex = orderIndex;
        this.visible = true;
        this.speakerrole = speakerrole;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getConferencecode() {
        return conferencecode;
    }

    public void setConferencecode(String conferencecode) {
        this.conferencecode = conferencecode;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getSpeakerType() {
        return speakerType;
    }

    public void setSpeakerType(String speakerType) {
        this.speakerType = speakerType;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public List<SpeakerSection> getSections() {
        return sections;
    }

    public void setSections(List<SpeakerSection> sections) {
        this.sections = sections;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getPartnerLogo() {
        return partnerLogo;
    }

    public void setPartnerLogo(String partnerLogo) {
        this.partnerLogo = partnerLogo;
    }

    public String getSpeakerrole() {
        return speakerrole;
    }

    public void setSpeakerrole(String speakerrole) {
        this.speakerrole = speakerrole;
    }
}
