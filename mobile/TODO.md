# FIJ Mobile - TODO

## Phase 1 - Setup technique
- [x] Ajouter dépendances (Retrofit, OkHttp, Hilt, Navigation, DataStore)
- [x] Créer structure dossiers Clean Architecture
- [x] Config réseau (ApiService, interceptors)

## Phase 2 - Authentification
- [x] Créer DTOs (LoginRequest, RegisterRequest, AuthResponse)
- [x] Créer AuthApi
- [x] Créer AuthRepository
- [x] Créer use cases (Login, Register, SaveToken)
- [x] Créer AuthViewModel
- [x] Créer LoginScreen
- [x] Créer RegisterScreen

## Phase 3 - Navigation principale
- [x] Créer NavGraph
- [x] Configurer BottomBar

## Phase 4 - Jobs (Candidat)
- [x] Créer DTOs Job
- [x] Créer JobApi
- [x] Créer JobRepository
- [x] Créer use cases (GetJobs, GetJobDetail, ApplyToJob)
- [x] Créer JobViewModel
- [x] Créer HomeScreen (liste jobs)
- [x] Créer JobDetailScreen

## Phase 5 - Applications (Candidat)
- [x] Créer ApplicationApi
- [x] Créer ApplicationRepository
- [x] Créer use cases (GetMyApplications)
- [x] Créer ApplicationViewModel
- [x] Créer ApplicationsScreen (candidatures envoyées)

## Phase 6 - Recruteur
- [x] Créer RecruiterApi
- [x] Créer RecruiterRepository
- [x] Créer RecruiterViewModel
- [x] Créer MyJobsScreen (jobs du recruteur)
- [x] Créer JobFormScreen (créer/modifier job)
- [x] Créer ApplicationsReceivedScreen (candidatures reçues)
- [x] Gérer userRole (TokenManager, AuthRepository, AuthViewModel)
- [x] Créer RecruiterBottomNavigationBar
- [x] Intégration navigation par rôle (NavGraph)

## Phase 7 - Profil
- [ ] Créer ProfileApi
- [ ] Créer ProfileRepository
- [ ] Créer use cases (GetProfile, UpdateProfile)
- [ ] Créer ProfileViewModel
- [ ] Créer ProfileScreen
- [ ] Créer EditProfileScreen

## Phase 8 - Chat
- [ ] Configurer WebSocket client
- [ ] Créer ChatApi
- [ ] Créer ChatRepository
- [ ] Créer ChatViewModel
- [ ] Créer ChatListScreen
- [ ] Créer ConversationScreen

## Phase 9 - Déploiement
- [x] URL API mise à jour vers Render
- [ ] Tester l'APK avec le backend Render

## Phase 10 - Sécurité & Robustesse
- [ ] Gestion 401 → auto logout
- [ ] Gestion 403
- [ ] Retry policy
- [ ] Messages erreur