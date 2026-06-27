/**
 * DTO για αίτημα αυθεντικοποίησης χρήστη.
 *
 * Μεταφέρει το username και το password από έναν API client προς το backend,
 * ώστε η εφαρμογή να ελέγξει τα στοιχεία σύνδεσης και να εκδώσει JWT token.
 */

package gr.studyrooms.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(@NotBlank String username, @NotBlank String password) {}
