/**
 * Service που περιέχει τη λογική διαχείρισης χρηστών.
 *
 * Χρησιμοποιείται για εγγραφή νέων φοιτητών, αναζήτηση χρηστών με βάση το
 * username και ανάκτηση του συνδεδεμένου χρήστη. Συνεργάζεται με το
 * UserRepository και το PasswordEncoder για την αποθήκευση ασφαλών κωδικών.
 */

package gr.studyrooms.service;

import gr.studyrooms.domain.Role;
import gr.studyrooms.domain.UserEntity;
import gr.studyrooms.dto.RegisterRequest;
import gr.studyrooms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserEntity registerStudent(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Το username υπάρχει ήδη.");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Το email υπάρχει ήδη.");
        }
        UserEntity user = new UserEntity();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setFullName(request.fullName());
        user.setRole(Role.ROLE_STUDENT);
        return userRepository.save(user);
    }

    public UserEntity getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Δεν βρέθηκε χρήστης."));
    }
}
