package conferenceadmin.conference.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "abstract_submissions")
public class roboticsAbstractSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String conferencecode;

    private String title;

    private String fullName;

    private String phoneNumber;

    private String emailAddress;

    private String organization;

    private String country;

    private String abstractFilePath;

    public roboticsAbstractSubmission() {
    }

    public roboticsAbstractSubmission(String conferencecode, String title, String fullName, String phoneNumber, String emailAddress, String organization, String country, String abstractFilePath) {
        this.conferencecode = conferencecode;
        this.title = title;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.organization = organization;
        this.country = country;
        this.abstractFilePath = abstractFilePath;
    }

    // Getters and setters
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAbstractFilePath() {
        return abstractFilePath;
    }

    public void setAbstractFilePath(String abstractFilePath) {
        this.abstractFilePath = abstractFilePath;
    }
}