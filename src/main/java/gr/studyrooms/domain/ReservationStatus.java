/**
 * Enum που ορίζει την κατάσταση μίας κράτησης.
 *
 * Χρησιμοποιείται για να ξεχωρίζουμε ενεργές και ακυρωμένες κρατήσεις.
 * Έτσι η εφαρμογή μπορεί να φιλτράρει και να εμφανίζει σωστά το ιστορικό
 * και τη διαχείριση κρατήσεων.
 */

package gr.studyrooms.domain;

public enum ReservationStatus {
    ACTIVE,
    CANCELLED,
    COMPLETED,
    NO_SHOW
}
