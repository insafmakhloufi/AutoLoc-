package tn.esprit.autoloc.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import tn.esprit.autoloc.domain.CategorieVehicule;
import tn.esprit.autoloc.domain.StatutVehicule;
import tn.esprit.autoloc.domain.Vehicule;
import tn.esprit.autoloc.repository.IVehiculeRepository;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final IVehiculeRepository vehiculeRepository;

    @Override
    public void run(String... args) {
        if (vehiculeRepository.count() == 0) {
            Vehicule v1 = new Vehicule(null, "TU-1234-AB", "Renault", "Clio",
                    CategorieVehicule.CITADINE, new BigDecimal("60.00"), StatutVehicule.DISPONIBLE);
            Vehicule v2 = new Vehicule(null, "TU-5678-CD", "Peugeot", "308",
                    CategorieVehicule.BERLINE, new BigDecimal("90.00"), StatutVehicule.DISPONIBLE);
            Vehicule v3 = new Vehicule(null, "TU-9012-EF", "Toyota", "RAV4",
                    CategorieVehicule.SUV, new BigDecimal("150.00"), StatutVehicule.MAINTENANCE);

            vehiculeRepository.saveAll(List.of(v1, v2, v3));
        }
    }
}