package gr.studyrooms.external;

import java.time.LocalDate;

public interface HolidayPort {
    boolean isPublicHoliday(LocalDate date);
}
