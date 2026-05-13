# FIJ - Journal d'avancement

## 2026-05-13 - Déploiement Render

### ✅ Réalisé
- **Déploiement backend** sur Render (plan gratuit)
  - Config via Docker + env vars
  - Correction `Network is unreachable` → utilisation DB PostgreSQL Render
  - URL publique : `https://find-your-job-924c.onrender.com`
- **Base de données** : Render PostgreSQL (`fyj_db`)
  - Pas de problème réseau (même infrastructure)
  - DataLoader exécuté → users de test disponibles
- **Mobile** : URL API mise à jour vers Render
- **Git** : `.gitignore` ajouté, `target/` retiré du tracking

### 📝 Fichiers modifiés
- `mobile/app/build.gradle.kts` (BASE_URL → Render)
- `.gitignore` (nouveau)

---

## 2026-05-12 - Déploiement Azure

### ✅ Réalisé
- **Azure Container Registry** : `fyjregistry`
- **Azure Container Apps** : `fyj-backend` + `fyj-db`
- Résolution : activé ingress interne sur la DB (`external: false`)
- Test : backend accessible, login fonctionnel

### ❌ Abandonné (coût)
- Azure Container Apps fonctionnel mais non utilisé (Render gratuit suffit)

---

## 2026-05-09 - Dockerisation du backend

### ✅ Réalisé
- Création du `Dockerfile` (multi-stage : Maven build → JRE 17 runtime)
- Mise à jour du `docker-compose.yml` avec le service `backend`
- Communication inter-containers via nom de service (`db`)
- Healthcheck PostgreSQL pour attendre la DB avant de lancer le backend
- Variables d'environnement pour l'URL de connexion DB (pas de modification du `.properties`)

### 📝 Fichiers modifiés
- `backend/Dockerfile` (nouveau)
- `backend/docker-compose.yml`

---

## 2026-05-08 - Phase 3 : Navigation multi-rôles

### ✅ Réalisé
- **Gestion du rôle utilisateur (userRole)**
  - Ajout de `USER_ROLE_KEY` dans `TokenManager.kt` (DataStore)
  - Exposition du flow `userRole` dans `AuthRepository.kt`
  - Exposition du `StateFlow` dans `AuthViewModel.kt`
  - Sauvegarde du rôle lors du login/register

- **Navigation conditionnelle par rôle**
  - `NavGraph.kt` utilise `userRole` pour déterminer quel écran afficher
  - Recruteur → `recruiter/jobs` (Mes Jobs)
  - Candidat → `home` (Accueil)

- **RecruiterBottomNavigationBar**
  - Création de `presentation/components/RecruiterBottomNavigationBar.kt`
  - Items : Mes Jobs, Candidatures, Messages, Profil
  - Correction du bug : `ChatList` route manquante pour recruteur (ajout dans NavGraph)

- **Refactoring**
  - Extraction de `Result.kt` (classe sealed partagée)
  - Suppression des doublons dans `AuthRepository.kt` et `RecruiterRepository.kt`

### 🐛 Bugs corrigés
- `Navigation destination chats not found` : Route `ChatList` ajoutée pour les recruteurs dans NavGraph

### 📝 Fichiers modifiés
- `mobile/app/src/main/java/com/example/findyourjob_mobile/data/remote/TokenManager.kt`
- `mobile/app/src/main/java/com/example/findyourjob_mobile/data/repository/AuthRepository.kt`
- `mobile/app/src/main/java/com/example/findyourjob_mobile/presentation/viewmodel/AuthViewModel.kt`
- `mobile/app/src/main/java/com/example/findyourjob_mobile/navigation/NavGraph.kt`
- `mobile/app/src/main/java/com/example/findyourjob_mobile/presentation/components/RecruiterBottomNavigationBar.kt`
- `mobile/app/src/main/java/com/example/findyourjob_mobile/data/repository/Result.kt` (nouveau)

### 📋 Fichiers TODO mis à jour
- `mobile/TODO.md` - Avancement mobile
- `backend/TODO.md` - Avancement backend

### 🐳 Environnement Docker
- **Base de données** : Dockerisée (`docker-compose.yml` - image `postgis/postgis:15-3.3`)
  - Container : `find_your_job_db`
  - Port : `5432:5432`
  - Volume persistant : `postgres_data`
- **Backend** : ✅ Dockerisé (multi-stage Maven, Java 17, port 8080)
- **Mobile** : ❌ Non dockerisé (APK sur device/émulateur)

---

## 📊 État actuel

### Backend (Java/Spring Boot)
- ✅ Auth (JWT)
- ✅ Jobs + Applications
- ✅ Recruteur (CRUD jobs, gestion candidatures)
- ✅ Chat (WebSocket + REST)
- ✅ Signalement & Blocage
- ✅ DataLoader (users de test : recruiter@test.com / candidate@test.com)

### Mobile (Android/Kotlin)
- ✅ Auth + Navigation
- ✅ Jobs (candidat)
- ✅ Applications (candidat)
- ✅ Recruteur (Mes Jobs, JobForm, Candidatures reçues)
- ✅ Navigation multi-rôles (BottomBar conditionnelle)
- ⏳ Chat (écrans existants, route ok)
- ⏳ Profil (à faire)

### Prochaine étape
**Phase 8 - Chat** : Tester l'écran ChatList pour recruteur + Phase 7 - Profil
