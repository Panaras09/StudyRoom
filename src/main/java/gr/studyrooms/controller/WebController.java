
/**
 * MVC controller για το web UI της εφαρμογής.
 *
 * Διαχειρίζεται τις σελίδες που εμφανίζονται στον browser, όπως login, εγγραφή,
 * λίστα χώρων, φόρμα κράτησης, ιστορικό κρατήσεων φοιτητή και σελίδες διαχείρισης
 * προσωπικού βιβλιοθήκης. Επιστρέφει Thymeleaf views αντί για JSON responses.
 */


package gr.studyrooms.controller;

import gr.studyrooms.dto.RegisterRequest;
import gr.studyrooms.dto.ReservationRequest;
import gr.studyrooms.dto.StudyRoomRequest;
import gr.studyrooms.service.ReservationService;
import gr.studyrooms.service.StudyRoomService;
import gr.studyrooms.service.UserService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Collectors;

@Controller
public class WebController {
    private final UserService userService;
    private final StudyRoomService roomService;
    private final ReservationService reservationService;

    public WebController(
            UserService userService,
            StudyRoomService roomService,
            ReservationService reservationService
    ) {
        this.userService = userService;
        this.roomService = roomService;
        this.reservationService = reservationService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("rooms", roomService.activeRooms());
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest("", "", "", ""));
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute RegisterRequest registerRequest,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", buildValidationMessage(bindingResult));
            return "auth/register";
        }

        try {
            userService.registerStudent(registerRequest);
            return "redirect:/login?registered";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/rooms")
    public String rooms(Authentication authentication, Model model) {
        addRoleFlags(authentication, model);
        model.addAttribute("rooms", roomService.activeRooms());
        return "rooms/list";
    }

    @GetMapping("/reservations/new")
    public String newReservation(
            @RequestParam(required = false) Long roomId,
            Model model
    ) {
        LocalDateTime start = LocalDate.now().plusDays(1).atTime(10, 0);
        LocalDateTime end = LocalDate.now().plusDays(1).atTime(11, 0);

        model.addAttribute("rooms", roomService.activeRooms());
        model.addAttribute("reservationRequest", new ReservationRequest(roomId, start, end));
        return "reservations/new";
    }

    @PostMapping("/reservations")
    public String createReservation(
            Authentication authentication,
            @Valid @ModelAttribute ReservationRequest reservationRequest,
            BindingResult bindingResult,
            Model model
    ) {
        if (authentication == null) {
            model.addAttribute("rooms", roomService.activeRooms());
            model.addAttribute("error", "Δεν υπάρχει ενεργό login session. Κάνε logout/login και ξαναδοκίμασε.");
            return "reservations/new";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("rooms", roomService.activeRooms());
            model.addAttribute("error", buildValidationMessage(bindingResult));
            return "reservations/new";
        }

        try {
            reservationService.createReservation(
                    userService.getByUsername(authentication.getName()),
                    reservationRequest
            );
            return "redirect:/reservations/my";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("rooms", roomService.activeRooms());
            model.addAttribute("error", ex.getMessage());
            return "reservations/new";
        } catch (Exception ex) {
            model.addAttribute("rooms", roomService.activeRooms());
            model.addAttribute("error", "Αποτυχία δημιουργίας κράτησης: " + ex.getMessage());
            return "reservations/new";
        }
    }

    @GetMapping("/reservations/my")
    public String myReservations(Authentication authentication, Model model) {
        if (authentication == null) {
            return "redirect:/login";
        }

        if (hasRole(authentication, "ROLE_STAFF")) {
            return "redirect:/staff/reservations";
        }

        model.addAttribute(
                "reservations",
                reservationService.history(userService.getByUsername(authentication.getName()))
        );

        return "reservations/my";
    }

    @PostMapping("/reservations/{id}/cancel")
    public String cancel(
            Authentication authentication,
            @PathVariable Long id
    ) {
        reservationService.cancelByStudent(
                userService.getByUsername(authentication.getName()),
                id
        );

        return "redirect:/reservations/my";
    }

    @GetMapping("/staff/rooms")
    public String staffRooms(Model model) {
        model.addAttribute("rooms", roomService.allRooms());
        model.addAttribute(
                "roomRequest",
                new StudyRoomRequest("", "", 1, LocalTime.of(8, 0), LocalTime.of(20, 0), true)
        );

        return "staff/rooms";
    }

    @PostMapping("/staff/rooms")
    public String createRoom(
            @Valid @ModelAttribute StudyRoomRequest roomRequest,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("rooms", roomService.allRooms());
            model.addAttribute("error", buildValidationMessage(bindingResult));
            return "staff/rooms";
        }

        roomService.create(roomRequest);
        return "redirect:/staff/rooms";
    }

    @GetMapping("/staff/reservations")
    public String staffReservations(Model model) {
        model.addAttribute("reservations", reservationService.allReservations());
        return "staff/reservations";
    }

    @PostMapping("/staff/reservations/{id}/cancel")
    public String cancelByStaff(
            @PathVariable Long id,
            @RequestParam(required = false) String reason
    ) {
        reservationService.cancelByStaff(id, reason);
        return "redirect:/staff/reservations";
    }

    @GetMapping("/staff/statistics")
    public String statistics(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,

            Model model
    ) {
        LocalDate start = from == null ? LocalDate.now().minusDays(7) : from;
        LocalDate end = to == null ? LocalDate.now().plusDays(7) : to;

        model.addAttribute("from", start);
        model.addAttribute("to", end);
        model.addAttribute("stats", reservationService.occupancyStatistics(start, end));

        return "staff/statistics";
    }

    private void addRoleFlags(Authentication authentication, Model model) {
        model.addAttribute("isStudent", authentication != null && hasRole(authentication, "ROLE_STUDENT"));
        model.addAttribute("isStaff", authentication != null && hasRole(authentication, "ROLE_STAFF"));
    }

    private boolean hasRole(Authentication authentication, String role) {
        if (authentication == null) {
            return false;
        }

        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }

    private String buildValidationMessage(BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            return "";
        }

        return bindingResult.getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining(" | "));
    }

    private String formatFieldError(FieldError error) {
        return switch (error.getField()) {
            case "roomId" -> "Πρέπει να επιλέξεις χώρο.";
            case "startsAt" -> "Η ημερομηνία/ώρα έναρξης πρέπει να είναι μελλοντική.";
            case "endsAt" -> "Η ημερομηνία/ώρα λήξης πρέπει να είναι μελλοντική.";
            case "name" -> "Πρέπει να συμπληρώσεις όνομα χώρου.";
            case "location" -> "Πρέπει να συμπληρώσεις τοποθεσία.";
            case "capacity" -> "Η χωρητικότητα πρέπει να είναι τουλάχιστον 1.";
            default -> error.getField() + ": " + error.getDefaultMessage();
        };
    }
}
