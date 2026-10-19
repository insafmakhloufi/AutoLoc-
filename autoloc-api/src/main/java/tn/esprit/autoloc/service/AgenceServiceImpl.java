package tn.esprit.autoloc.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.autoloc.domain.Agence;
import tn.esprit.autoloc.exception.ResourceNotFoundException;
import tn.esprit.autoloc.repository.IAgenceRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AgenceServiceImpl implements IAgenceService {

    private final IAgenceRepository agenceRepository;

    @Override
    @Transactional
    public Agence create(Agence agence) {
        if (agence == null) {
            throw new IllegalArgumentException("Agence obligatoire");
        }
        if (agence.getIdAgence() != null) {
            throw new IllegalArgumentException("Un nouveau agence ne doit pas avoir d'identifiant");
        }
        validate(agence);
        return agenceRepository.save(agence);
    }

    @Override
    public Agence findById(Long id) {
        return agenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", id));
    }

    @Override
    public List<Agence> findAll() {
        return agenceRepository.findAll();
    }

    @Override
    @Transactional
    public Agence update(Long id, Agence agence) {
        Agence existant = findById(id);
        if (agence == null) {
            throw new IllegalArgumentException("Agence obligatoire");
        }
        validate(agence);
        existant.setNom(agence.getNom());
        existant.setVille(agence.getVille());
        existant.setAdresse(agence.getAdresse());
        existant.setTelephone(agence.getTelephone());
        return agenceRepository.save(existant);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!agenceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Agence", id);
        }
        agenceRepository.deleteById(id);
    }

    private void validate(Agence agence) {
        if (agence.getNom() == null || agence.getNom().isBlank()) {
            throw new IllegalArgumentException("Agence : Nom obligatoire");
        }
        if (agence.getVille() == null || agence.getVille().isBlank()) {
            throw new IllegalArgumentException("Agence : Ville obligatoire");
        }
        if (agence.getAdresse() == null || agence.getAdresse().isBlank()) {
            throw new IllegalArgumentException("Agence : Adresse obligatoire");
        }
        if (agence.getTelephone() == null || agence.getTelephone().isBlank()) {
            throw new IllegalArgumentException("Agence : Telephone obligatoire");
        }
    }

}
