/**
 * Enum που ορίζει τους ρόλους χρηστών της εφαρμογής.
 *
 * Οι ρόλοι χρησιμοποιούνται από το Spring Security για authorization.
 * Για παράδειγμα, ο φοιτητής μπορεί να κάνει κρατήσεις, ενώ το προσωπικό
 * βιβλιοθήκης μπορεί να διαχειρίζεται χώρους και κρατήσεις.
 */

package gr.studyrooms.domain;

public enum Role {
    ROLE_STUDENT,
    ROLE_STAFF
}
