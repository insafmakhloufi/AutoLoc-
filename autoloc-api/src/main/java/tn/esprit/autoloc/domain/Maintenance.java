package tn.esprit.autoloc.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "maintenance")
@Getter
@Setter
@NoArgsConstructor
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMaintenance;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    @Column(length = 255)
    private String description;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_vehicule")
    private Vehicule vehicule;

    public Maintenance(Long idMaintenance, LocalDate dateDebut, LocalDate dateFin, String description) {
        this.idMaintenance = idMaintenance;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.description = description;
    }
}