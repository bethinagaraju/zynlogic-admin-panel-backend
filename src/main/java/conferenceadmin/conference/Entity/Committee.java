package conferenceadmin.conference.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "committees")
public class Committee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String conferencecode;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String university;

    @Column(nullable = false)
    private Integer orderIndex;

    public Committee() {
    }

    public Committee(String conferencecode, String name, String university, Integer orderIndex) {
        this.conferencecode = conferencecode;
        this.name = name;
        this.university = university;
        this.orderIndex = orderIndex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConferencecode() {
        return conferencecode;
    }

    public void setConferencecode(String conferencecode) {
        this.conferencecode = conferencecode;
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

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}
