// package conferenceadmin.conference.Repository;

// import conferenceadmin.conference.Entity.Registration;
// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.stereotype.Repository;

// import java.util.Optional;

// @Repository
// public interface RegistrationRepository extends JpaRepository<Registration, Long> {
//     Optional<Registration> findByReferenceId(String referenceId);
// }



package conferenceadmin.conference.Repository;

import conferenceadmin.conference.Entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegistrationRepository
        extends JpaRepository<Registration, Long> {

    Optional<Registration> findByStateId(String stateId);
}
