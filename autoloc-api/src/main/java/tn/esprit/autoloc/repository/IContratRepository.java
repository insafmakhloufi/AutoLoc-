package tn.esprit.autoloc.repository;

import org.springframework.data.repository.CrudRepository;
import tn.esprit.autoloc.domain.Contrat;

public interface IContratRepository extends CrudRepository<Contrat, Long> {
}