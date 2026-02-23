Student Hibernate Practice Project
=======
# 🏢 Gestion des Réservations de Salles — JPA / Hibernate

> A hands-on Java project demonstrating JPA & Hibernate relationships through a room reservation management system.

---

## 📽️ Demo




https://github.com/user-attachments/assets/ed38dfaa-e2d8-4dde-b596-ebc98a21d50e


---

## 📖 About The Project

This project was built as a practical exercise to explore **JPA and Hibernate ORM** concepts through a real-world use case: managing room reservations in a building or campus.

It covers all major relationship types (`@OneToMany`, `@ManyToOne`, `@ManyToMany`), cascade behaviors, orphan removal, and bidirectional synchronization — all in a clean, minimal Maven project with an in-memory H2 database.

---

## 🗂️ Entity Model

```
Utilisateur ──< Reservation >── Salle
                                  │
                            >─────┤
                           Equipement (ManyToMany)
```

| Entity | Description |
|---|---|
| `Utilisateur` | A user who can make reservations |
| `Salle` | A room that can be reserved and has equipment |
| `Reservation` | Links a user to a room for a time period |
| `Equipement` | Equipment that can be assigned to multiple rooms |

### Entity Relationship Diagram

<img width="845" height="682" alt="diagramme de class" src="https://github.com/user-attachments/assets/b85df34e-6290-464f-bb8c-436d0e020021" />



---

## 🔗 JPA Relationships Covered

Utilisateur & Reservation

One user can make many reservations.
Each reservation belongs to one user.
Reservation stores the user ID.

Salle & Reservation

One room can have many reservations.
Each reservation is for one room.
Reservation stores the room ID.

Salle & Equipement

A room can have many equipment items.
Equipment can be in many rooms.
There is a join table between them.

---


## 🛠️ Tech Stack

| Technology | Version |
|---|---|
| Java | 11+ |
| JPA | 2.2 |
| Hibernate | 5.x |
| H2 Database | In-memory (`mem:testdb`) |
| Bean Validation | javax.validation |
| Maven | 3.x |

---

## 🚀 Getting Started

### Prerequisites

- Java 11 or higher
- Maven 3.x

### Run the project

---

## 📁 Project Structure

```
src/
└── main/
    ├── java/
    │   └── com/example/
    │       ├── App.java                  ← Entry point & test scenarios
    │       └── model/
    │           ├── Utilisateur.java      ← @OneToMany → Reservation
    │           ├── Salle.java            ← @OneToMany + @ManyToMany
    │           ├── Reservation.java      ← @ManyToOne × 2 (owner)
    │           └── Equipement.java       ← @ManyToMany (inverse)
    └── resources/
        └── META-INF/
            └── persistence.xml           ← JPA / Hibernate config (H2)
```

---

## 💡 Key Concepts Demonstrated

- **Owner vs Inverse side** in bidirectional relationships
- **Helper methods** (`addReservation`, `removeReservation`, `addEquipement`...) to keep both sides in sync
- **Cascade types**: `ALL`, `PERSIST + MERGE` — and when NOT to use `REMOVE`
- **`orphanRemoval = true`** to auto-delete detached children
- **`FetchType.LAZY`** on `@ManyToOne` to avoid unnecessary queries
- **Bean Validation** annotations (`@NotBlank`, `@Email`, `@Min`, `@Size`)

---

