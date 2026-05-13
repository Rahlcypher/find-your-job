# FIJ Backend - TODO

## Configuration
- [x] Spring Boot + PostgreSQL setup
- [x] SecurityConfig (JWT + CORS)
- [x] SwaggerConfig

## Authentification
- [x] AuthController (/api/auth/*)
- [x] AuthService (login, register, refreshToken)
- [x] JwtService (generate, validate tokens)

## Jobs
- [x] JobController (/api/jobs/*)
- [x] JobService (CRUD jobs, search)
- [x] JobRepository

## Applications
- [x] ApplicationController (/api/applications/*)
- [x] ApplicationService
- [x] ApplicationRepository

## Profil (Candidat)
- [x] CandidateController (/api/profile/*)
- [x] CandidateProfileService (CV, skills, etc.)
- [x] Education/Experience/Skill/Language models

## Recruteur
- [x] RecruiterController (/api/recruiter/*)
- [x] RecruiterService (CRUD jobs, manage applications)
- [ ] Tests unitaires (RecruiterServiceTest ✅, RecruiterControllerTest ⏸️)

## Chat
- [x] ChatController (/api/chats/*)
- [x] ChatService
- [x] ChatRepository / MessageRepository
- [x] WebSocketConfig
- [x] ChatWebSocketController

## Signalement & Blocage
- [x] ReportController (/api/reports/*)
- [x] ReportService
- [x] ReportRepository
- [x] BlockController (/api/blocks/*)
- [x] BlockService
- [x] BlockRepository

## Utilisateurs
- [x] UserController (/api/users/*)
- [x] UserService

## Data & Tests
- [x] DataLoader (seed data)
- [x] Phase4IntegrationTest
- Tests unitaires à compléter

## Déploiement
- [x] Dockerfile (multi-stage)
- [x] docker-compose.yml
- [x] Azure Container Apps (testé)
- [x] Render (production)
- [x] URL publique : `https://find-your-job-924c.onrender.com`