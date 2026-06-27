package gr.studyrooms.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public record StudyRoomRequest(
        @NotBlank String name,
        @NotBlank String location,
        @Min(1) int capacity,
        @NotNull LocalTime opensAt,
        @NotNull LocalTime closesAt,
        boolean active
) {}
