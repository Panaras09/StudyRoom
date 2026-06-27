package gr.studyrooms.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@Entity
public class StudyRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @NotBlank
    @Column(nullable = false)
    private String location;

    @Min(1)
    @Column(nullable = false)
    private int capacity;

    @NotNull
    @Column(nullable = false)
    private LocalTime opensAt;

    @NotNull
    @Column(nullable = false)
    private LocalTime closesAt;

    @Column(nullable = false)
    private boolean active = true;
}
