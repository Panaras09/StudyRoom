package gr.studyrooms.controller;

import gr.studyrooms.domain.Reservation;
import gr.studyrooms.dto.ReservationRequest;
import gr.studyrooms.service.ReservationService;
import gr.studyrooms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ReservationApiController {
    private final ReservationService reservationService;
    private final UserService userService;

    public ReservationApiController(ReservationService reservationService, UserService userService) {
        this.reservationService = reservationService;
        this.userService = userService;
    }

    @GetMapping("/reservations/my")
    public List<Reservation> myReservations(Authentication authentication) {
        return reservationService.history(userService.getByUsername(authentication.getName()));
    }

    @PostMapping("/reservations")
    public Reservation create(Authentication authentication, @Valid @RequestBody ReservationRequest request) {
        return reservationService.createReservation(userService.getByUsername(authentication.getName()), request);
    }

    @PostMapping("/reservations/{id}/cancel")
    public Reservation cancel(Authentication authentication, @PathVariable Long id) {
        return reservationService.cancelByStudent(userService.getByUsername(authentication.getName()), id);
    }

    @GetMapping("/staff/reservations")
    public List<Reservation> forDay(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return reservationService.reservationsForDay(date);
    }

    @PostMapping("/staff/reservations/{id}/cancel")
    public Reservation cancelByStaff(@PathVariable Long id, @RequestParam(required = false) String reason) {
        return reservationService.cancelByStaff(id, reason);
    }

    @GetMapping("/staff/statistics")
    public Map<String, Long> statistics(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return reservationService.occupancyStatistics(from, to);
    }
}
