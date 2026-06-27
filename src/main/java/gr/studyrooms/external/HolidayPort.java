/**
 * Port για τον έλεγχο αργιών.
 *
 * Ορίζει το συμβόλαιο που χρησιμοποιεί η εφαρμογή για να ρωτήσει αν μία
 * συγκεκριμένη ημερομηνία είναι αργία. Με αυτόν τον τρόπο το business logic
 * δεν εξαρτάται άμεσα από την υλοποίηση της εξωτερικής υπηρεσίας.
 */

package gr.studyrooms.external;

import java.time.LocalDate;

public interface HolidayPort {
    boolean isPublicHoliday(LocalDate date);
}
