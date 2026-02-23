package com.example;

import com.example.model.Equipement;
import com.example.model.Reservation;
import com.example.model.Salle;
import com.example.model.Utilisateur;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDateTime;
import java.util.List;

public class App {
    public static void main(String[] args) {
        // Create the EntityManagerFactory
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("gestion-reservations");

        try {
            // Test relationships and cascading operations
            System.out.println("\n=== Relationship and Cascade Operations Test ===");
            testRelationsEtCascade(emf);

            // Test orphan removal
            System.out.println("\n=== Orphan Removal Test ===");
            testSuppressionOrpheline(emf);

            // Test many-to-many relationship with equipment
            System.out.println("\n=== Many-to-Many Relationship Test with Equipment ===");
            testRelationManyToMany(emf);

        } finally {
            // Close the EntityManagerFactory
            emf.close();
        }
    }

    private static void testRelationsEtCascade(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Create entities
            System.out.println("Creating entities...");

            // Create a user
            Utilisateur utilisateur = new Utilisateur("Dupont", "Jean", "jean.dupont@example.com");

            // Create a room
            Salle salle = new Salle("Salle A101", 30);
            salle.setDescription("Salle de réunion équipée d'un projecteur");

            // Create a reservation
            Reservation reservation = new Reservation(
                    LocalDateTime.now().plusDays(1),
                    LocalDateTime.now().plusDays(1).plusHours(2),
                    "Réunion d'équipe"
            );

            // Establish relationships
            utilisateur.addReservation(reservation);
            salle.addReservation(reservation);

            // Persist the user with cascade on the reservation
            em.persist(utilisateur);
            em.persist(salle);

            em.getTransaction().commit();
            System.out.println("Entities created and linked successfully!");

            // Verify persisted entities
            em.clear(); // Clear the persistence context

            System.out.println("\nVerifying persisted entities:");
            Utilisateur utilisateurPersiste = em.find(Utilisateur.class, utilisateur.getId());
            System.out.println("User: " + utilisateurPersiste);
            System.out.println("Number of reservations: " + utilisateurPersiste.getReservations().size());

            Salle sallePersistee = em.find(Salle.class, salle.getId());
            System.out.println("Room: " + sallePersistee);
            System.out.println("Number of reservations: " + sallePersistee.getReservations().size());

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private static void testSuppressionOrpheline(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            // Create a user with reservations
            em.getTransaction().begin();

            Utilisateur utilisateur = new Utilisateur("Martin", "Sophie", "sophie.martin@example.com");

            Salle salle1 = new Salle("Salle B102", 20);
            em.persist(salle1);

            Salle salle2 = new Salle("Salle C103", 15);
            em.persist(salle2);

            // Create two reservations
            Reservation reservation1 = new Reservation(
                    LocalDateTime.now().plusDays(2),
                    LocalDateTime.now().plusDays(2).plusHours(1),
                    "Entretien"
            );

            Reservation reservation2 = new Reservation(
                    LocalDateTime.now().plusDays(3),
                    LocalDateTime.now().plusDays(3).plusHours(2),
                    "Formation"
            );

            // Establish relationships
            utilisateur.addReservation(reservation1);
            utilisateur.addReservation(reservation2);
            salle1.addReservation(reservation1);
            salle2.addReservation(reservation2);

            em.persist(utilisateur);

            em.getTransaction().commit();
            System.out.println("User with two reservations created!");

            // Remove a reservation (orphan removal test)
            em.getTransaction().begin();

            Utilisateur utilisateurAModifier = em.find(Utilisateur.class, utilisateur.getId());
            System.out.println("Number of reservations before removal: " + utilisateurAModifier.getReservations().size());

            // Remove the first reservation (deleted thanks to orphanRemoval=true)
            Reservation reservationASupprimer = utilisateurAModifier.getReservations().get(0);
            utilisateurAModifier.removeReservation(reservationASupprimer);

            em.getTransaction().commit();

            // Verify removal
            em.clear();
            Utilisateur utilisateurApresModification = em.find(Utilisateur.class, utilisateur.getId());
            System.out.println("Number of reservations after removal: " + utilisateurApresModification.getReservations().size());

            // Verify the reservation was removed from the database
            Long reservationId = reservationASupprimer.getId();
            Reservation reservationSupprimee = em.find(Reservation.class, reservationId);
            System.out.println("Does the reservation still exist? " + (reservationSupprimee != null));

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    private static void testRelationManyToMany(EntityManagerFactory emf) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Create equipment
            Equipement projecteur = new Equipement("Projecteur", "Projecteur HD");
            Equipement ecran = new Equipement("Écran interactif", "Écran tactile 65 pouces");
            Equipement visioconference = new Equipement("Système de visioconférence", "Système complet avec caméra HD");

            // Create rooms
            Salle salleReunion = new Salle("Salle de réunion D104", 25);
            Salle salleFormation = new Salle("Salle de formation E205", 40);

            // Add equipment to rooms
            salleReunion.addEquipement(projecteur);
            salleReunion.addEquipement(visioconference);

            salleFormation.addEquipement(projecteur);
            salleFormation.addEquipement(ecran);

            // Persist rooms (equipment is persisted via cascade)
            em.persist(salleReunion);
            em.persist(salleFormation);

            em.getTransaction().commit();
            System.out.println("Rooms and equipment created successfully!");

            // Verify relationships
            em.clear();

            System.out.println("\nVerifying many-to-many relationships:");

            // Fetch rooms
            Salle salleReunionPersistee = em.find(Salle.class, salleReunion.getId());
            System.out.println("Room: " + salleReunionPersistee.getNom());
            System.out.println("Equipment:");
            for (Equipement equipement : salleReunionPersistee.getEquipements()) {
                System.out.println("- " + equipement.getNom());
            }

            Salle salleFormationPersistee = em.find(Salle.class, salleFormation.getId());
            System.out.println("\nRoom: " + salleFormationPersistee.getNom());
            System.out.println("Equipment:");
            for (Equipement equipement : salleFormationPersistee.getEquipements()) {
                System.out.println("- " + equipement.getNom());
            }

            // Fetch equipment and display associated rooms
            Equipement projecteurPersiste = em.createQuery(
                            "SELECT e FROM Equipement e WHERE e.nom = :nom", Equipement.class)
                    .setParameter("nom", "Projecteur")
                    .getSingleResult();

            System.out.println("\nEquipment: " + projecteurPersiste.getNom());
            System.out.println("Equipped rooms:");
            for (Salle salle : projecteurPersiste.getSalles()) {
                System.out.println("- " + salle.getNom());
            }

            // Test removing equipment from a room
            em.getTransaction().begin();

            salleReunionPersistee.removeEquipement(projecteurPersiste);

            em.getTransaction().commit();

            // Verify after removal
            em.clear();

            Salle salleApresModification = em.find(Salle.class, salleReunion.getId());
            System.out.println("\nRoom after removing equipment: " + salleApresModification.getNom());
            System.out.println("Remaining equipment:");
            for (Equipement equipement : salleApresModification.getEquipements()) {
                System.out.println("- " + equipement.getNom());
            }

            // Verify the equipment still exists
            Equipement projecteurApresModification = em.find(Equipement.class, projecteurPersiste.getId());
            System.out.println("\nDoes the equipment still exist? " + (projecteurApresModification != null));

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}
