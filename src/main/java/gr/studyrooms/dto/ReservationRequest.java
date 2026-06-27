/**
 * DTO για αίτημα δημιουργίας κράτησης.
 *
 * Μεταφέρει τον χώρο μελέτης και το χρονικό διάστημα που επέλεξε ο φοιτητής.
 * Χρησιμοποιείται τόσο από το web UI όσο και από το REST API για τη δημιουργία
 * νέας κράτησης.
 */

package gr.studyrooms.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReservationRequest(
        @NotNull Long roomId,
        @NotNull @Future LocalDateTime startsAt,
        @NotNull @Future LocalDateTime endsAt
) {}
