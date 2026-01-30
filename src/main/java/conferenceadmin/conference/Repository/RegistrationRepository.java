
// package conferenceadmin.conference.Repository;

// import conferenceadmin.conference.Entity.Registration;
// import org.springframework.data.jpa.repository.JpaRepository;

// import java.util.Optional;

// public interface RegistrationRepository
//         extends JpaRepository<Registration, Long> {

//     Optional<Registration> findByStateId(String stateId);
// }


// package conferenceadmin.conference.Repository;

// import conferenceadmin.conference.Entity.Registration;
// import org.springframework.data.jpa.repository.JpaRepository;
// import java.util.Optional;

// public interface RegistrationRepository extends JpaRepository<Registration, Long> {
//     Optional<Registration> findByStateId(String stateId);
    
//     // Add this new method
//     Optional<Registration> findByEmail(String email);
// }



package conferenceadmin.conference.Repository;

import conferenceadmin.conference.Entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    
    Optional<Registration> findByStateId(String stateId);

    // ✅ Add this line so the service works
    Optional<Registration> findByEmail(String email);

    List<Registration> findByConferenceCode(String conferenceCode);
}