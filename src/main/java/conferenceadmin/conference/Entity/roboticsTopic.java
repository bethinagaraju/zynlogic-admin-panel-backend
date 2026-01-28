package conferenceadmin.conference.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "topics")
public class roboticsTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String topicName;

    private String conferencecode;

    @Column(nullable = false)
    private Integer orderIndex;

    public roboticsTopic() {
    }

    public roboticsTopic(String topicName, String conferencecode, Integer orderIndex) {
        this.topicName = topicName;
        this.conferencecode = conferencecode;
        this.orderIndex = orderIndex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getConferencecode() {
        return conferencecode;
    }

    public void setConferencecode(String conferencecode) {
        this.conferencecode = conferencecode;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }
}