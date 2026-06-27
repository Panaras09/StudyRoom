package gr.studyrooms.service;

import gr.studyrooms.domain.StudyRoom;
import gr.studyrooms.dto.StudyRoomRequest;
import gr.studyrooms.repository.StudyRoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudyRoomService {
    private final StudyRoomRepository repository;

    public StudyRoomService(StudyRoomRepository repository) {
        this.repository = repository;
    }

    public List<StudyRoom> activeRooms() {
        return repository.findByActiveTrueOrderByNameAsc();
    }

    public List<StudyRoom> allRooms() {
        return repository.findAll();
    }

    public StudyRoom get(Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Δεν βρέθηκε χώρος μελέτης."));
    }

    public StudyRoom create(StudyRoomRequest request) {
        StudyRoom room = new StudyRoom();
        apply(room, request);
        return repository.save(room);
    }

    public StudyRoom update(Long id, StudyRoomRequest request) {
        StudyRoom room = get(id);
        apply(room, request);
        return repository.save(room);
    }

    private void apply(StudyRoom room, StudyRoomRequest request) {
        if (!request.opensAt().isBefore(request.closesAt())) {
            throw new IllegalArgumentException("Το άνοιγμα πρέπει να είναι πριν από το κλείσιμο.");
        }
        room.setName(request.name());
        room.setLocation(request.location());
        room.setCapacity(request.capacity());
        room.setOpensAt(request.opensAt());
        room.setClosesAt(request.closesAt());
        room.setActive(request.active());
    }
}
