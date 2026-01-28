package conferenceadmin.conference.Service;

import conferenceadmin.conference.Entity.roboticsTopic;
import conferenceadmin.conference.Repository.roboticsTopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class roboticsTopicService {

    private final roboticsTopicRepository repository;

    public roboticsTopicService(roboticsTopicRepository repository) {
        this.repository = repository;
    }

    public roboticsTopic saveTopic(String topicName, String conferencecode) {
        // Find the max orderIndex for the conferencecode and add 1
        List<roboticsTopic> existing = repository.findByConferencecodeOrderByOrderIndex(conferencecode);
        Integer maxOrder = existing.isEmpty() ? 0 : existing.get(existing.size() - 1).getOrderIndex();
        roboticsTopic t = new roboticsTopic(topicName, conferencecode, maxOrder + 1);
        return repository.save(t);
    }

    public roboticsTopic updateTopic(Long id, String topicName, String conferencecode) {
        Optional<roboticsTopic> opt = repository.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Topic not found");
        }
        roboticsTopic t = opt.get();
        if (topicName != null && !topicName.isBlank()) {
            t.setTopicName(topicName);
        }
        if (conferencecode != null && !conferencecode.isBlank()) {
            t.setConferencecode(conferencecode);
        }
        return repository.save(t);
    }

    public roboticsTopic getTopicById(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Topic not found"));
    }

    public List<roboticsTopic> getAllTopics() {
        return repository.findAll();
    }

    public List<roboticsTopic> getTopicsByConferencecode(String conferencecode) {
        return repository.findByConferencecodeOrderByOrderIndex(conferencecode);
    }

    public void deleteTopic(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Topic not found");
        }
        repository.deleteById(id);
    }

    public void reorderTopics(List<Long> topicIdsInOrder) {
        for (int i = 0; i < topicIdsInOrder.size(); i++) {
            Long id = topicIdsInOrder.get(i);
            Optional<roboticsTopic> opt = repository.findById(id);
            if (opt.isPresent()) {
                roboticsTopic t = opt.get();
                t.setOrderIndex(i + 1); // Start from 1
                repository.save(t);
            }
        }
    }
}