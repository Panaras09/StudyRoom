/**
 * Repository για την πρόσβαση στα δεδομένα των κρατήσεων.
 *
 * Παρέχει μεθόδους αναζήτησης και αποθήκευσης κρατήσεων στη βάση δεδομένων.
 * Χρησιμοποιείται από το ReservationService για ιστορικό κρατήσεων,
 * έλεγχο επικαλύψεων και διαχείριση κρατήσεων από φοιτητές ή προσωπικό.
 */


package gr.studyrooms.repository;

import gr.studyrooms.domain.Reservation;
import gr.studyrooms.domain.ReservationStatus;
import gr.studyrooms.domain.StudyRoom;
import gr.studyrooms.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByStudentOrderByStartsAtDesc(UserEntity student);

    List<Reservation> findByRoomAndStatus(StudyRoom room, ReservationStatus status);

    List<Reservation> findAllByOrderByStartsAtDesc();

    @Query("""
            select count(r) from Reservation r
            where r.student = :student
              and r.status = 'ACTIVE'
              and r.startsAt >= :dayStart
              and r.startsAt < :dayEnd
            """)
    long countActiveForStudentOnDay(@Param("student") UserEntity student,
                                    @Param("dayStart") LocalDateTime dayStart,
                                    @Param("dayEnd") LocalDateTime dayEnd);

    @Query("""
            select count(r) from Reservation r
            where r.room = :room
              and r.status = 'ACTIVE'
              and r.startsAt < :end
              and r.endsAt > :start
            """)
    long countOverlappingActiveReservations(@Param("room") StudyRoom room,
                                            @Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end);

    @Query("""
            select r from Reservation r
            where r.startsAt >= :start and r.startsAt < :end
            order by r.startsAt asc
            """)
    List<Reservation> findBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}
