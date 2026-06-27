/**
 * Entity που αναπαριστά έναν χρήστη της εφαρμογής.
 *
 * Περιέχει τα βασικά στοιχεία σύνδεσης και προφίλ, όπως username, password,
 * ονοματεπώνυμο και ρόλο. Χρησιμοποιείται τόσο για φοιτητές όσο και για
 * προσωπικό βιβλιοθήκης.
 */

package gr.studyrooms.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Email
    @NotBlank
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank
    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private int penaltyDaysRemaining = 0;
}
