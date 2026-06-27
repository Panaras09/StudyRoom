/**
 * Repository για την πρόσβαση στα δεδομένα των χρηστών.
 *
 * Παρέχει μεθόδους αναζήτησης χρηστών, κυρίως με βάση το username.
 * Χρησιμοποιείται από το authentication, το registration και τις λειτουργίες
 * που χρειάζονται τον τρέχοντα συνδεδεμένο χρήστη.
 */


package gr.studyrooms.repository;

import gr.studyrooms.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
