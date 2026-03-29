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

## Phase 4 - Jobs
- [x] Créer DTOs Job
- [x] Créer JobApi
- [x] Créer JobRepository
- [x] Créer use cases (GetJobs, GetJobDetail, ApplyToJob)
- [x] Créer JobViewModel
- [x] Créer JobListScreen
- [x] Créer JobDetailScreen

## Phase 5 - Applications
- [ ] Créer ApplicationApi
- [ ] Créer ApplicationRepository
- [ ] Créer use cases (GetMyApplications)
- [ ] Créer ApplicationViewModel
- [ ] Créer MyApplicationsScreen

## Phase 6 - Profil
- [ ] Créer ProfileApi
- [ ] Créer ProfileRepository
- [ ] Créer use cases (GetProfile, UpdateProfile)
- [ ] Créer ProfileViewModel
- [ ] Créer ProfileScreen
- [ ] Créer EditProfileScreen

## Phase 7 - Chat
- [ ] Configurer WebSocket client
- [ ] Créer ChatApi
- [ ] Créer ChatRepository
- [ ] Créer ChatViewModel
- [ ] Créer ChatListScreen
- [ ] Créer ConversationScreen

## Phase 8 - Sécurité & Robustesse
- [ ] Gestion 401 → auto logout
- [ ] Gestion 403
- [ ] Retry policy
- [ ] Messages erreur
