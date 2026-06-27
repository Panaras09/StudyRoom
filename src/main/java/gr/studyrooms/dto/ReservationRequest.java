package gr.studyrooms.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReservationRequest(
        @NotNull Long roomId,
        @NotNull @Future LocalDateTime startsAt,
        @NotNull @Future LocalDateTime endsAt
) {}
