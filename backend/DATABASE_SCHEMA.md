# 📊 Base de données du projet FIJ - Schema complet

## Comment consulter la base de données

### 1. Via le terminal (psql)

```
bash
# Se connecter à la base
psql -h localhost -p 5432 -U admin -d fyj_database

# Mot de passe: rahlofpgsql

# Voir toutes les tables
\dt

# Voir les colonnes d'une table
\d nom_de_la_table

# Voir les données
SELECT * FROM users LIMIT 10;
```

### 2. Via une interface graphique

- **DBeaver** (gratuit, recommandé)
- **pgAdmin**
- **TablePlus**
- **DataGrip**

Paramètres de connexion :
- Host: localhost
- Port: 5432
- Database: fyj_database
- User: admin
- Password: rahlofpgsql

---

## 📋 Liste des tables et colonnes

### 1. users (Utilisateurs)
| Colonne | Type | Description |
|---------|------|-------------|
| id | BIGINT | Clé primaire |
| email | VARCHAR(255) | Email unique |
| password | VARCHAR(255) | Mot de passe hashé |
| first_name | VARCHAR(255) | Prénom |
| last_name | VARCHAR(255) | Nom |
| phone | VARCHAR(255) | Téléphone |
| location | VARCHAR(255) | Localisation |

### 2. user_roles (Rôles des utilisateurs)
| Colonne | Type | Description |
|---------|------|-------------|
| user_id | BIGINT | FK vers users |
| roles | VARCHAR | ROLE_CANDIDATE ou ROLE_RECRUITER |

---

### 3. jobs (Offres d'emploi)
| Colonne | Type | Description |
|---------|------|-------------|
| id | BIGINT | Clé primaire |
| title | VARCHAR(255) | Titre du poste |
| description | TEXT | Description complète |
| company | VARCHAR(255) | Nom de l'entreprise |
| location | VARCHAR(255) | Lieu de travail |
| salary_min | INTEGER | Salaire minimum |
| salary_max | INTEGER | Salaire maximum |
| job_type | VARCHAR(255) | Type de contrat |
| work_schedule | VARCHAR(255) | Horaires |
| remote_policy | VARCHAR(255) | Politique remote |
| duration | INTEGER | Durée (mois) |
| active | BOOLEAN | Est active |
| created_at | TIMESTAMP | Date de création |
| expires_at | TIMESTAMP | Date d'expiration |
| recruiter_id | BIGINT | FK vers users (recruteur) |

---

### 4. applications (Candidatures)
| Colonne | Type | Description |
|---------|------|-------------|
| id | BIGINT | Clé primaire |
| job_id | BIGINT | FK vers jobs |
| candidate_id | BIGINT | FK vers users |
| status | VARCHAR(255) | Statut (PENDING, ACCEPTED, REJECTED) |
| cover_letter | TEXT | Lettre de motivation |
| applied_at | TIMESTAMP | Date de candidature |

---

### 5. chats (Conversations)
| Colonne | Type | Description |
|---------|------|-------------|
| id | BIGINT | Clé primaire |
| job_id | BIGINT | FK vers jobs |
| candidate_id | BIGINT | FK vers users |
| recruiter_id | BIGINT | FK vers users |
| created_at | TIMESTAMP | Date de création |
| last_message_at | TIMESTAMP | Dernier message |

---

### 6. messages (Messages)
| Colonne | Type | Description |
|---------|------|-------------|
| id | BIGINT | Clé primaire |
| chat_id | BIGINT | FK vers chats |
| sender_id | BIGINT | FK vers users |
| content | TEXT | Contenu du message |
| attachment_url | VARCHAR(255) | URL de la pièce jointe |
| attachment_name | VARCHAR(255) | Nom du fichier |
| sent_at | TIMESTAMP | Date d'envoi |
| read | BOOLEAN | Lu ou non |

---

### 7. blocks (Utilisateurs bloqués)
| Colonne | Type | Description |
|---------|------|-------------|
| id | BIGINT | Clé primaire |
| blocker_id | BIGINT | FK vers users (celui qui bloque) |
| blocked_id | BIGINT | FK vers users (celui qui est bloqué) |
| blocked_at | TIMESTAMP | Date du blocage |

---

### 8. reports (Signalements)
| Colonne | Type | Description |
|---------|------|-------------|
| id | BIGINT | Clé primaire |
| reporter_id | BIGINT | FK vers users |
| reported_user_id | BIGINT | FK vers users |
| reason | VARCHAR(255) | Raison du signalement |
| description | TEXT | Description |
| status | VARCHAR(255) | Statut (PENDING, RESOLVED) |
| created_at | TIMESTAMP | Date de création |

---

### 9. educations (Formations)
| Colonne | Type | Description |
|---------|------|-------------|
| id | BIGINT | Clé primaire |
| degree | VARCHAR(255) | Diplôme |
| school | VARCHAR(255) | École |
| field_of_study | VARCHAR(255) | Domaine d'étude |
| start_date | DATE | Date de début |
| end_date | DATE | Date de fin |
| user_id | BIGINT | FK vers users |

---

### 10. experiences (Expériences professionnelles)
| Colonne | Type | Description |
|---------|------|-------------|
| id | BIGINT | Clé primaire |
| title | VARCHAR(255) | Poste |
| company | VARCHAR(255) | Entreprise |
| description | TEXT | Description |
| start_date | DATE | Date de début |
| end_date | DATE | Date de fin |
| current_job | BOOLEAN | Poste actuel |
| user_id | BIGINT | FK vers users |

---

### 11. skills (Compétences)
| Colonne | Type | Description |
|---------|------|-------------|
| id | BIGINT | Clé primaire |
| name | VARCHAR(255) | Nom de la compétence |
| level | VARCHAR(255) | Niveau |
| user_id | BIGINT | FK vers users |

---

### 12. languages (Langues)
| Colonne | Type | Description |
|---------|------|-------------|
| id | BIGINT | Clé primaire |
| name | VARCHAR(255) | Langue |
| level | VARCHAR(255) | Niveau |
| user_id | BIGINT | FK vers users |

---

### 13. job_preferences (Préférences d'emploi)
| Colonne | Type | Description |
|---------|------|-------------|
| id | BIGINT | Clé primaire |
| min_salary | INTEGER | Salaire minimum souhaité |
| mobility_zone | VARCHAR(255) | Zone de mobilité |
| job_type | VARCHAR(255) | Type de poste |
| work_schedule | VARCHAR(255) | Horaires souhaités |
| remote_preference | VARCHAR(255) | Préférence remote |
| user_id | BIGINT | FK vers users |

---

### 14. documents (Documents/CV)
| Colonne | Type | Description |
|---------|------|-------------|
| id | BIGINT | Clé primaire |
| file_name | VARCHAR(255) | Nom du fichier |
| file_type | VARCHAR(255) | Type MIME |
| file_path | VARCHAR(255) | Chemin du fichier |
| file_size | BIGINT | Taille en octets |
| uploaded_at | TIMESTAMP | Date d'upload |
| user_id | BIGINT | FK vers users |

---

## 🔗 Schéma des relations

```
users (1) ←→ (N) user_roles
users (1) ←→ (N) jobs (via recruiter_id)
users (1) ←→ (N) applications (via candidate_id)
users (1) ←→ (N) chats (candidate ou recruiter)
users (1) ←→ (N) messages
users (1) ←→ (N) blocks (blocker ou blocked)
users (1) ←→ (N) reports (reporter ou reported_user)
users (1) ←→ (N) educations
users (1) ←→ (N) experiences
users (1) ←→ (N) skills
users (1) ←→ (N) languages
users (1) ←→ (1) job_preferences
users (1) ←→ (N) documents

jobs (1) ←→ (N) applications
jobs (1) ←→ (N) chats

chats (1) ←→ (N) messages
