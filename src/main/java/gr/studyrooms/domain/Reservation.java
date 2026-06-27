/**
 * Entity που αναπαριστά μία κράτηση χώρου μελέτης.
 *
 * Συνδέει έναν φοιτητή με έναν συγκεκριμένο χώρο μελέτης για συγκεκριμένο
 * χρονικό διάστημα. Περιέχει επίσης την κατάσταση της κράτησης, ώστε να
 * γνωρίζουμε αν είναι ενεργή ή ακυρωμένη.
 */


package gr.studyrooms.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private StudyRoom room;

    @ManyToOne(optional = false)
    private UserEntity student;

    @Column(nullable = false)
    private LocalDateTime startsAt;

    @Column(nullable = false)
    private LocalDateTime endsAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.ACTIVE;

    @Column(length = 500)
    private String cancellationReason;
}
