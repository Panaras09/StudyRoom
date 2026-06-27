/**
 * DTO για την απάντηση αυθεντικοποίησης.
 *
 * Επιστρέφει στον API client τον τύπο του token και το JWT access token.
 * Το token χρησιμοποιείται μετά στο Authorization header για προστατευμένα API calls.
 */

package gr.studyrooms.dto;

public record AuthResponse(String tokenType, String accessToken) {}
