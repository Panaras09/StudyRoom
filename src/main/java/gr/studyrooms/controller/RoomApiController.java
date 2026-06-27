package gr.studyrooms.controller;

import gr.studyrooms.domain.StudyRoom;
import gr.studyrooms.dto.StudyRoomRequest;
import gr.studyrooms.service.StudyRoomService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RoomApiController {
    private final StudyRoomService service;

    public RoomApiController(StudyRoomService service) {
        this.service = service;
    }

    @GetMapping("/rooms")
    public List<StudyRoom> activeRooms() {
        return service.activeRooms();
    }

    @PostMapping("/staff/rooms")
    public StudyRoom create(@Valid @RequestBody StudyRoomRequest request) {
        return service.create(request);
    }

    @PutMapping("/staff/rooms/{id}")
    public StudyRoom update(@PathVariable Long id, @Valid @RequestBody StudyRoomRequest request) {
        return service.update(id, request);
    }
}
