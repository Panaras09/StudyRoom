/**
 * Adapter για την εξωτερική υπηρεσία αργιών.
 *
 * Υλοποιεί το HolidayPort και λειτουργεί ως ενδιάμεσος ανάμεσα στην εφαρμογή
 * και μία δημόσια υπηρεσία αργιών. Η κύρια εφαρμογή το χρησιμοποιεί σαν
 * black-box εξωτερική υπηρεσία, ώστε να αποτρέπει κρατήσεις σε ημέρες που
 * η βιβλιοθήκη θεωρείται κλειστή.
 */

package gr.studyrooms.external;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.Arrays;

@Component
public class PublicHolidayAdapter implements HolidayPort {
    private final RestClient restClient;
    private final String countryCode;

    public PublicHolidayAdapter(@Value("${app.holidays.base-url}") String baseUrl,
                                @Value("${app.holidays.country-code}") String countryCode) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.countryCode = countryCode;
    }

    @Override
    public boolean isPublicHoliday(LocalDate date) {
        try {
            HolidayDto[] holidays = restClient.get()
                    .uri("/PublicHolidays/{year}/{countryCode}", date.getYear(), countryCode)
                    .retrieve()
                    .body(HolidayDto[].class);
            if (holidays == null) return false;
            return Arrays.stream(holidays).anyMatch(h -> date.toString().equals(h.date()));
        } catch (Exception ex) {
            // Fail-open for demo: if the external black-box service is down, the app keeps working.
            return false;
        }
    }

    record HolidayDto(String date, String localName, String name) {}
}
