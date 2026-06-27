/**
 * Κεντρική κλάση εκκίνησης της εφαρμογής StudyRooms.
 *
 * Εκκινεί το Spring Boot application και φορτώνει όλα τα components της εφαρμογής,
 * όπως controllers, services, repositories, security configuration και αρχικοποίηση
 * demo δεδομένων.
 */

package gr.studyrooms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudyRoomsApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudyRoomsApplication.class, args);
    }
}
