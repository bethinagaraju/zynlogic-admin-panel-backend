// package conferenceadmin.conference.Service;

// import conferenceadmin.conference.Entity.Registration;
// import conferenceadmin.conference.Repository.RegistrationRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.util.UUID;

// @Service
// public class RegistrationService {

//     @Autowired
//     private RegistrationRepository repo;

//     public Registration create(Registration r) {
//         r.setReferenceId("REF_" + UUID.randomUUID());
//         r.setPaymentStatus("PENDING");
//         return repo.save(r);
//     }

//     public void updateStatus(String ref, String status) {
//         Registration r = repo.findByReferenceId(ref)
//                 .orElseThrow(() -> new RuntimeException("Invalid ref"));
//         r.setPaymentStatus(status);
//         repo.save(r);
//     }
// }





package conferenceadmin.conference.Service;

import conferenceadmin.conference.Entity.Registration;
import conferenceadmin.conference.Repository.RegistrationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository repo;

    // Create registration
    public Registration create(Registration r) {
        r.setStateId(UUID.randomUUID().toString()); // 🔑 important
        r.setPaymentStatus("PENDING");
        return repo.save(r);
    }

    // Update payment status using state_id
    // public void updateStatusByStateId(String stateId, String status) {
    //     Registration r = repo.findByStateId(stateId)
    //             .orElseThrow(() -> new RuntimeException("Invalid state_id"));

    //     r.setPaymentStatus(status);
    //     repo.save(r);
    // }

//     public void updateStatusByStateIdSafe(String stateId, String status) {
//     repo.findByStateId(stateId).ifPresent(reg -> {
//         reg.setPaymentStatus(status);
//         repo.save(reg);
//     });
// }


public boolean updateStatusByStateIdSafe(String stateId, String status) {
    return repo.findByStateId(stateId).map(reg -> {
        reg.setPaymentStatus(status);
        repo.save(reg);
        return true;
    }).orElse(false);
}


}
