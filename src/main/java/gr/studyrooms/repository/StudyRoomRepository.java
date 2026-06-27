package gr.studyrooms.repository;

import gr.studyrooms.domain.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {
    List<StudyRoom> findByActiveTrueOrderByNameAsc();
}
