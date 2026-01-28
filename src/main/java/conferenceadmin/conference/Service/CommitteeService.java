package conferenceadmin.conference.Service;

import conferenceadmin.conference.Entity.Committee;
import conferenceadmin.conference.Repository.CommitteeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommitteeService {

    private final CommitteeRepository repository;

    public CommitteeService(CommitteeRepository repository) {
        this.repository = repository;
    }

    public Committee saveCommittee(String conferencecode, String name, String university) {
        if (conferencecode == null || conferencecode.isBlank()) {
            throw new IllegalArgumentException("Conference code is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Committee name is required");
        }
        if (university == null || university.isBlank()) {
            throw new IllegalArgumentException("University is required");
        }

        Integer nextOrder = getNextOrderIndex(conferencecode);
        Committee committee = new Committee(conferencecode, name, university, nextOrder);
        return repository.save(committee);
    }

    public Committee updateCommittee(Long id, String conferencecode, String name, String university) {
        Optional<Committee> opt = repository.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Committee not found");
        }

        Committee committee = opt.get();

        if (conferencecode != null && !conferencecode.isBlank()) {
            committee.setConferencecode(conferencecode);
        }
        if (name != null && !name.isBlank()) {
            committee.setName(name);
        }
        if (university != null && !university.isBlank()) {
            committee.setUniversity(university);
        }

        return repository.save(committee);
    }

    public Committee getCommitteeById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Committee not found"));
    }

    public List<Committee> getAllCommittees() {
        return repository.findAll();
    }

    public List<Committee> getCommitteesByConferencecode(String conferencecode) {
        return repository.findByConferencecodeOrderByOrderIndexAsc(conferencecode);
    }

    public void deleteCommittee(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Committee not found");
        }
        repository.deleteById(id);
    }

    @Transactional
    public void reorderCommittees(List<Long> committeeIdsInOrder) {
        if (committeeIdsInOrder == null || committeeIdsInOrder.isEmpty()) {
            throw new IllegalArgumentException("Committee IDs list cannot be empty");
        }

        for (int i = 0; i < committeeIdsInOrder.size(); i++) {
            Long id = committeeIdsInOrder.get(i);
            Optional<Committee> opt = repository.findById(id);
            if (opt.isEmpty()) {
                throw new IllegalArgumentException("Committee with id " + id + " not found");
            }
            Committee committee = opt.get();
            committee.setOrderIndex(i);
            repository.save(committee);
        }
    }

    private Integer getNextOrderIndex(String conferencecode) {
        List<Committee> committees = repository.findByConferencecode(conferencecode);
        if (committees.isEmpty()) {
            return 0;
        }
        return committees.stream()
                .mapToInt(Committee::getOrderIndex)
                .max()
                .orElse(0) + 1;
    }
}
