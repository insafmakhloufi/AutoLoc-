# Atelier 4 — Analyse et couche Service

## Analyse du travail initial

Source retrouvée : Downloads/AutoLoc--tp2-3/AutoLoc--tp2-3. Une copie a été restaurée dans Desktop/autoloccc/AutoLoc- ; la source téléchargée est conservée.

Les neuf entités, leurs associations JPA et les neuf repositories existaient déjà. Lombok était utilisé de façon ciblée, sans @Data dans le modèle principal. Aucune couche Service ni exception métier de l'atelier 4 n'était présente.

Corrections complémentaires :
- IContratRepository étend désormais JpaRepository au lieu de CrudRepository : conformité au prérequis et retour List pour findAll.
- Le plugin Maven Spring Boot désigne explicitement tn.esprit.autoloc.AutolocApiApplication, car deux classes principales existent dans des packages distincts. Dans IntelliJ, choisir également cette classe.
- application.properties est converti en UTF-8 ; la déclaration ddl-auto=create redondante est retirée. Le réglage effectif update est conservé.

## A. Modes d'injection comparés

| Critère | Constructeur | Attribut |
| --- | --- | --- |
| Champ final possible | Oui | Non dans l'injection Spring habituelle |
| Dépendance visible dans une signature | Oui, constructeur généré par Lombok | Non, champ privé |
| Utilisable avec new hors conteneur | Oui, repository obligatoire au constructeur | Repository null après un simple new |
| SonarQube | Mode recommandé | S6813 attendue, à confirmer dans l'IDE |

@RequiredArgsConstructor génère le constructeur des champs final. Spring utilise automatiquement ce constructeur unique sans @Autowired. Les services sont des singletons sans état propre à un appel.

## B. Services réalisés

| Service | Dépendance injectée | Mode | Justification |
| --- | --- | --- | --- |
| AgenceServiceImpl | IAgenceRepository | Constructeur | Dépendance obligatoire et typée par interface ; accès aux données pour le CRUD métier |
| EmployeServiceImpl | IEmployeRepository | Constructeur | Dépendance obligatoire et typée par interface ; accès aux données pour le CRUD métier |
| ClientServiceImpl | IClientRepository | Constructeur | Dépendance obligatoire et typée par interface ; accès aux données pour le CRUD métier |
| EquipementServiceImpl | IEquipementRepository | Constructeur | Dépendance obligatoire et typée par interface ; accès aux données pour le CRUD métier |
| VehiculeServiceImpl | IVehiculeRepository | Constructeur | Dépendance obligatoire et typée par interface ; accès aux données pour le CRUD métier |
| ReservationServiceImpl | IReservationRepository | Constructeur | Dépendance obligatoire et typée par interface ; accès aux données pour le CRUD métier |
| ContratServiceImpl | IContratRepository | Constructeur | Dépendance obligatoire et typée par interface ; accès aux données pour le CRUD métier |
| MaintenanceServiceImpl | IMaintenanceRepository | Constructeur | Dépendance obligatoire et typée par interface ; accès aux données pour le CRUD métier |
| PaiementServiceImpl | IPaiementRepository | Constructeur | Dépendance obligatoire et typée par interface ; lecture seule |

Les huit services CRUD :
- refusent un identifiant non null à la création ;
- lèvent ResourceNotFoundException si findById ou deleteById vise une ressource absente ;
- chargent l'existant avant update, valident les données et recopient les attributs modifiables ;
- conservent l'identifiant et les collections existantes, en particulier Contrat.paiements ;
- utilisent IllegalArgumentException avec un message explicite pour les données métier invalides.

Règles appliquées à create et update :
- Agence : nom, ville, adresse et téléphone non vides.
- Employe : nom et prénom non vides, rôle présent.
- Client : nom, prénom, email, téléphone et numéro de permis non vides.
- Equipement : libellé non vide.
- Vehicule : tarif journalier présent et non négatif.
- Reservation et Maintenance : dates présentes, fin non antérieure au début.
- Contrat : montant total présent et non négatif.

Le modèle ne possède ni quantité d'équipement, ni kilométrage, ni coût de maintenance : les exemples correspondants du PDF ne justifient pas l'ajout de nouveaux attributs. Les associations simples propriétaires sont recopiées telles que reçues ; la gestion complète des liens reste au chapitre 9. La cascade ALL existante de Contrat vers Paiement propage notamment la suppression du contrat à ses paiements.

IPaiementService expose uniquement findById et findAll. Les écritures passent par l'agrégat Contrat ; aucune méthode autonome de mutation des paiements n'a été ajoutée dans cet atelier.

@Transactional(readOnly = true) définit les opérations de lecture. Les méthodes d'écriture redéfinissent @Transactional pour que chargement, modification et sauvegarde appartiennent à une transaction unique. readOnly est une indication transactionnelle, pas un contrôle de permission.

## C. Messages d'erreur du conteneur

1. **Service absent** : NoSuchBeanDefinitionException, souvent enveloppée dans UnsatisfiedDependencyException lors de la création de ContratController. Le bean IContratService manque. Les deux causes probables sont l'absence de @Service sur ContratServiceImpl ou un package hors de tn.esprit.autoloc et de ses sous-packages. Ajouter @Service ou corriger le package/scan.
2. **Deux candidats** : NoUniqueBeanDefinitionException, souvent enveloppée dans UnsatisfiedDependencyException pour NotificationController. emailNotificateur et smsNotificateur satisfont le même type. Première correction : @Primary sur la classe du candidat par défaut. Seconde : @Qualifier("emailNotificateur") sur le paramètre du constructeur du contrôleur. Utiliser un constructeur explicite pour ce qualificatif plutôt que supposer sa copie depuis un champ par Lombok.
3. **Cycle** : BeanCurrentlyInCreationException, éventuellement enveloppée. clientServiceImpl exige reservationServiceImpl, qui exige clientServiceImpl avant que sa construction soit terminée. Avec l'injection par constructeur, aucun des deux objets ne peut être fourni en premier. Extraire l'orchestration dans un troisième service dépendant des deux, ou déplacer la responsabilité commune vers un composant indépendant.

Lire Description et la dernière cause pertinente Caused by pour retrouver l'origine d'une erreur.

## D. Qualité et anomalies SonarQube for IDE

L'analyse SonarQube dans IntelliJ n'a pas été exécutée depuis cet environnement. Les règles suivantes sont des points pédagogiques et corrections préventives présents dans le code livré ; elles ne constituent pas des diagnostics IDE observés.

| Règle à vérifier | Anomalie étudiée | Correction dans le livrable |
| --- | --- | --- |
| S6813 | Injection par attribut : dépendance cachée et champ non final | Injection par constructeur et repository final dans les neuf services |
| S112 | Exception générique levée pour une ressource absente | ResourceNotFoundException dédiée |
| S1948 | Absence de serialVersionUID dans une exception sérialisable | Champ private static final long serialVersionUID = 1L |

Pour terminer les expériences demandées :
1. Analyser service et exception avec « Analyze with SonarQube for IDE ».
2. Remplacer temporairement l'injection par constructeur de ContratServiceImpl par une injection par attribut, relever le diagnostic réellement affiché, puis restaurer.
3. Remplacer temporairement ResourceNotFoundException par RuntimeException dans findById, relever le diagnostic, puis restaurer.
4. Relever une troisième anomalie effective et sa correction. Les identifiants ci-dessus restent à confirmer selon la version du plugin.

Le code final ne contient ni injection par attribut, ni new de repository ou de service, ni exception générique directement levée.

## E. Questions de compréhension

1. **Où est le new de ContratServiceImpl ?** Le conteneur Spring instancie le bean détecté par @Service en appelant son constructeur, généré par Lombok. Aucun new explicite n'est nécessaire dans l'application. Le repository fourni est un proxy Spring Data.
2. **Pourquoi dépendre de IContratService ?** Un contrôleur dépend du contrat métier et peut recevoir une autre implémentation sans modifier son code. La persistance reste derrière le service.
3. **Pourquoi un singleton sans état ?** Plusieurs requêtes partagent la même instance. Un champ contratCourant pourrait être remplacé par une requête B alors que A le traite : A modifierait le mauvais contrat. Garder ces données dans les variables locales.
4. **Pourquoi charger avant save ?** Pour vérifier l'existence et contrôler précisément les attributs modifiés, tout en conservant l'identifiant et les associations non concernées. Une fusion directe d'un objet reçu incomplet peut écraser des colonnes ou vider des collections ; avec orphanRemoval, elle peut supprimer les enfants.
5. **Annotations et Paiement.** @Component rend une classe détectable par scan ; @Bean déclare un objet retourné par une méthode de configuration, utile pour une classe externe ou une construction particulière. @Primary désigne un candidat par défaut ; @Qualifier choisit un candidat au point d'injection. Paiement reste en lecture seule car son cycle de vie dépend du Contrat et des cascades.

## Vérification et limites

- Maven 3.9.16 local, JDK 21, cible Java 17 : compilation de 48 fichiers source et empaquetage réussis avec -o -DskipTests package.
- Le JAR démarre avec la configuration MySQL existante et --server.port=0 : Found 9 JPA repository interfaces, Started AutolocApiApplication, puis ReadinessState ACCEPTING_TRAFFIC.
- Aucun test automatisé ni programme de démonstration ajouté, conformément au périmètre du PDF. Les tests préexistants sont compilés mais non exécutés. La validation fonctionnelle des services via REST/Postman relève de l'atelier 5.
- L'analyse SonarQube for IDE et les relevés des expériences restent à effectuer dans IntelliJ.
- La copie téléchargée initiale ne contenait pas de dépôt .git. Le livrable est intégré dans une branche tp4 créée à partir de tp2+3 du dépôt insafmakhloufi/AutoLoc-.
