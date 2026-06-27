/**
 * Repository για την πρόσβαση στα δεδομένα των χώρων μελέτης.
 *
 * Παρέχει μεθόδους ανάγνωσης και αποθήκευσης χώρων μελέτης στη βάση δεδομένων.
 * Χρησιμοποιείται για την προβολή ενεργών χώρων στους φοιτητές και για τη
 * διαχείριση χώρων από το προσωπικό βιβλιοθήκης.
 */

package gr.studyrooms.repository;

import gr.studyrooms.domain.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {
    List<StudyRoom> findByActiveTrueOrderByNameAsc();
}
