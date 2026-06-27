/**
 * Service που περιέχει την επιχειρησιακή λογική των κρατήσεων.
 *
 * Ελέγχει τους κανόνες πριν δημιουργηθεί μία κράτηση, όπως αν ο χώρος είναι
 * ενεργός, αν η ώρα είναι μέσα στο ωράριο λειτουργίας, αν υπάρχει διαθέσιμη
 * χωρητικότητα και αν η ημερομηνία επιτρέπεται. Επίσης διαχειρίζεται ακυρώσεις,
 * ιστορικό κρατήσεων και στατιστικά πληρότητας.
 */

package gr.studyrooms.service;

import gr.studyrooms.domain.Reservation;
import gr.studyrooms.domain.ReservationStatus;
import gr.studyrooms.domain.StudyRoom;
import gr.studyrooms.domain.UserEntity;
import gr.studyrooms.dto.ReservationRequest;
import gr.studyrooms.external.HolidayPort;
import gr.studyrooms.repository.ReservationRepository;
import gr.studyrooms.repository.StudyRoomRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final StudyRoomRepository roomRepository;
    private final HolidayPort holidayPort;
    private final int maxActivePerDay;

    public ReservationService(ReservationRepository reservationRepository,
                              StudyRoomRepository roomRepository,
                              HolidayPort holidayPort,
                              @Value("${app.rules.max-active-reservations-per-student-per-day}") int maxActivePerDay) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.holidayPort = holidayPort;
        this.maxActivePerDay = maxActivePerDay;
    }

    @Transactional
    public Reservation createReservation(UserEntity student, ReservationRequest request) {
        StudyRoom room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new IllegalArgumentException("Δεν βρέθηκε χώρος μελέτης."));

        validateReservation(student, room, request.startsAt(), request.endsAt());

        Reservation reservation = new Reservation();
        reservation.setStudent(student);
        reservation.setRoom(room);
        reservation.setStartsAt(request.startsAt());
        reservation.setEndsAt(request.endsAt());
        reservation.setStatus(ReservationStatus.ACTIVE);
        return reservationRepository.save(reservation);
    }

    public List<Reservation> history(UserEntity student) {
        return reservationRepository.findByStudentOrderByStartsAtDesc(student);
    }

    public List<Reservation> allReservations() {
        return reservationRepository.findAllByOrderByStartsAtDesc();
    }

    @Transactional
    public Reservation cancelByStudent(UserEntity student, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Δεν βρέθηκε κράτηση."));

        if (!reservation.getStudent().getId().equals(student.getId())) {
            throw new IllegalArgumentException("Δεν μπορείς να ακυρώσεις κράτηση άλλου φοιτητή.");
        }

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new IllegalArgumentException("Η κράτηση δεν είναι ενεργή.");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancellationReason("Ακύρωση από φοιτητή");
        return reservationRepository.save(reservation);
    }

    @Transactional
    public Reservation cancelByStaff(Long reservationId, String reason) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Δεν βρέθηκε κράτηση."));

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new IllegalArgumentException("Η κράτηση δεν είναι ενεργή.");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancellationReason(reason == null || reason.isBlank()
                ? "Ακύρωση από προσωπικό βιβλιοθήκης"
                : reason);

        return reservationRepository.save(reservation);
    }

    public List<Reservation> reservationsForDay(LocalDate date) {
        return reservationRepository.findBetween(date.atStartOfDay(), date.plusDays(1).atStartOfDay());
    }

    public Map<String, Long> occupancyStatistics(LocalDate from, LocalDate to) {
        Map<String, Long> stats = new LinkedHashMap<>();
        List<Reservation> reservations = reservationRepository.findBetween(from.atStartOfDay(), to.plusDays(1).atStartOfDay());

        for (Reservation reservation : reservations) {
            stats.merge(reservation.getRoom().getName(), 1L, Long::sum);
        }

        return stats;
    }

    private void validateReservation(UserEntity student, StudyRoom room, LocalDateTime start, LocalDateTime end) {
        if (student.getPenaltyDaysRemaining() > 0) {
            throw new IllegalArgumentException("Υπάρχει penalty λόγω no-show. Δεν επιτρέπεται νέα κράτηση.");
        }

        if (!room.isActive()) {
            throw new IllegalArgumentException("Ο χώρος δεν είναι ενεργός.");
        }

        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Η ώρα έναρξης πρέπει να είναι πριν από την ώρα λήξης.");
        }

        if (start.toLocalDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Δεν επιτρέπεται κράτηση στο παρελθόν.");
        }

        if (holidayPort.isPublicHoliday(start.toLocalDate())) {
            throw new IllegalArgumentException("Η βιβλιοθήκη είναι κλειστή λόγω εθνικής αργίας.");
        }

        if (start.toLocalTime().isBefore(room.getOpensAt()) || end.toLocalTime().isAfter(room.getClosesAt())) {
            throw new IllegalArgumentException("Η κράτηση είναι εκτός ωραρίου λειτουργίας του χώρου.");
        }

        long activeToday = reservationRepository.countActiveForStudentOnDay(
                student,
                start.toLocalDate().atStartOfDay(),
                start.toLocalDate().plusDays(1).atStartOfDay()
        );

        if (activeToday >= maxActivePerDay) {
            throw new IllegalArgumentException("Έχεις φτάσει το όριο ενεργών κρατήσεων για την ημέρα.");
        }

        long overlapping = reservationRepository.countOverlappingActiveReservations(room, start, end);
        if (overlapping >= room.getCapacity()) {
            throw new IllegalArgumentException("Δεν υπάρχει διαθεσιμότητα για αυτό το διάστημα.");
        }
    }
}
