package gr.studyrooms.config;

import gr.studyrooms.domain.Role;
import gr.studyrooms.domain.StudyRoom;
import gr.studyrooms.domain.UserEntity;
import gr.studyrooms.repository.StudyRoomRepository;
import gr.studyrooms.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalTime;

@Configuration
public class DataInitializer {
    @Bean
    CommandLineRunner seed(UserRepository users, StudyRoomRepository rooms, PasswordEncoder encoder) {
        return args -> {
            if (users.count() == 0) {
                UserEntity student = new UserEntity();
                student.setUsername("student");
                student.setPassword(encoder.encode("student123"));
                student.setEmail("student@example.com");
                student.setFullName("Demo Student");
                student.setRole(Role.ROLE_STUDENT);
                users.save(student);

                UserEntity staff = new UserEntity();
                staff.setUsername("staff");
                staff.setPassword(encoder.encode("staff123"));
                staff.setEmail("staff@example.com");
                staff.setFullName("Library Staff");
                staff.setRole(Role.ROLE_STAFF);
                users.save(staff);
            }

            if (rooms.count() == 0) {
                createRoom(rooms, "Χώρος Ανάγνωσης Α", "Βιβλιοθήκη", 4, "08:00", "20:00");
                createRoom(rooms, "Χώρος Ανάγνωσης Β", "Βιβλιοθήκη", 6, "09:00", "21:00");
                createRoom(rooms, "Αναγνωστήριο", "Κεντρικό Κτήριο", 20, "08:00", "22:00");
            }
        };
    }

    private void createRoom(StudyRoomRepository rooms, String name, String location, int capacity, String opens, String closes) {
        StudyRoom room = new StudyRoom();
        room.setName(name);
        room.setLocation(location);
        room.setCapacity(capacity);
        room.setOpensAt(LocalTime.parse(opens));
        room.setClosesAt(LocalTime.parse(closes));
        room.setActive(true);
        rooms.save(room);
    }
}
