# Atelier 3 — Notes Repository

## Choix des interfaces

| Interface | Étend | Justification |
|---|---|---|
| IContratRepository | JpaRepository<Contrat, Long> | CRUD complet, findAll renvoie une List, saveAndFlush disponible. |
| IPaiementRepository | JpaRepository<Paiement, Long> | Lecture des paiements ; création et suppression passent par le Contrat (composition). |
| IVehiculeRepository | JpaRepository<Vehicule, Long> | CRUD, tri et pagination pour lister les véhicules. |
| IAgenceRepository | JpaRepository<Agence, Long> | CRUD complet, collections renvoyées en List. |
| IEmployeRepository | JpaRepository<Employe, Long> | CRUD complet, collections renvoyées en List. |
| IEquipementRepository | JpaRepository<Equipement, Long> | CRUD complet, collections renvoyées en List. |
| IClientRepository | JpaRepository<Client, Long> | CRUD et pagination de la liste des clients. |
| IReservationRepository | JpaRepository<Reservation, Long> | CRUD et tri par date. |
| IMaintenanceRepository | JpaRepository<Maintenance, Long> | CRUD complet, collections renvoyées en List. |

## Anomalies SonarQube for IDE

| Anomalie SonarQube for IDE | Règle / explication | Correction apportée |
|---|---|---|
| Import inutilisé `lombok.AllArgsConstructor` dans `Vehicule.java` | java:S1128 — un import non utilisé alourdit le code et prête à confusion sur les dépendances réelles de la classe. | Suppression de la ligne d'import. |
| Import inutilisé `lombok.AllArgsConstructor` dans `Reservation.java` | java:S1128 — même règle. | Suppression de la ligne d'import. |
| Import inutilisé `lombok.AllArgsConstructor` dans `Client.java`, `Contrat.java`, `Equipement.java`, `Maintenance.java`, `Paiement.java` | java:S1128 — même règle, détectée sur les autres entités. | Suppression des imports (Optimize Imports). |