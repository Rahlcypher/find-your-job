package com.fij.config;

import com.fij.models.Job;
import com.fij.models.Role;
import com.fij.models.User;
import com.fij.repositories.JobRepository;
import com.fij.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            loadUsers();
        }
        if (jobRepository.count() == 0) {
            loadJobs();
        }
    }

    private void loadUsers() {
        User recruiter = new User();
        recruiter.setEmail("recruiter@test.com");
        recruiter.setPassword(passwordEncoder.encode("password123"));
        recruiter.setFirstName("Marie");
        recruiter.setLastName("Dupont");
        recruiter.setRoles(Set.of(Role.ROLE_RECRUITER));
        userRepository.save(recruiter);

        User candidate = new User();
        candidate.setEmail("candidate@test.com");
        candidate.setPassword(passwordEncoder.encode("password123"));
        candidate.setFirstName("Jean");
        candidate.setLastName("Martin");
        candidate.setRoles(Set.of(Role.ROLE_CANDIDATE));
        userRepository.save(candidate);
    }

    private void loadJobs() {
        User recruiter = userRepository.findByEmail("recruiter@test.com").orElseThrow();

        List<Job> jobs = List.of(
            createJob("Développeur Java Spring Boot", "Nous recherchons un développeur Java experimenté pour rejoindre notre équipe tech. Vous travaillerez sur des projets innovants avec les dernières technologies.",
                "TechCorp", "Paris", 45000, 65000, "CDI", "Temps plein", "Hybride", 36, recruiter),
            createJob("Développeur Android (Kotlin)", "Rejoignez notre équipe mobile pour développer des applications utilisées par des millions d'utilisateurs.",
                "AppFactory", "Lyon", 40000, 55000, "CDI", "Temps plein", "Télétravail possible", 24, recruiter),
            createJob("Designer UX/UI", "Vous êtes passionné par l'expérience utilisateur ? Rejoignez-nous pour créer des interfaces innovantes.",
                "DesignLab", "Bordeaux", 35000, 50000, "CDI", "Temps plein", "Présentiel", 12, recruiter),
            createJob("Chef de Projet IT", "Gestion de projets digitaux, coordination des équipes techniques et relation client.",
                "Innovatech", "Nantes", 42000, 60000, "CDI", "Temps plein", "Hybride", 18, recruiter),
            createJob("Stagiaire Développeur Web", "Stage de 6 mois pour rejoindre notre équipe web. Vous serez encadré par nos développeurs seniors.",
                "WebAgency", "Toulouse", 1200, 1500, "Stage", "Temps plein", "Présentiel", 6, recruiter),
            createJob("Ingénieur DevOps", "Nous cherchons un expert DevOps pour optimiser notre infrastructure et nos pipelines CI/CD.",
                "CloudFirst", "Lille", 48000, 70000, "CDI", "Temps plein", "Hybride", 24, recruiter),
            createJob("Data Analyst", "Analyse de données, création de tableaux de bord et storytelling data.",
                "DataViz", "Rennes", 38000, 52000, "CDI", "Temps plein", "Télétravail possible", 12, recruiter),
            createJob("Product Owner", "Définition de la roadmap produit, priorisation du backlog et collaboration avec les équipes agiles.",
                "AgileSoft", "Marseille", 50000, 70000, "CDI", "Temps plein", "Hybride", 30, recruiter)
        );

        jobRepository.saveAll(jobs);
    }

    private Job createJob(String title, String description, String company, String location,
                          int salaryMin, int salaryMax, String jobType, String workSchedule,
                          String remotePolicy, int duration, User recruiter) {
        Job job = new Job();
        job.setTitle(title);
        job.setDescription(description);
        job.setCompany(company);
        job.setLocation(location);
        job.setSalaryMin(salaryMin);
        job.setSalaryMax(salaryMax);
        job.setJobType(jobType);
        job.setWorkSchedule(workSchedule);
        job.setRemotePolicy(remotePolicy);
        job.setDuration(duration);
        job.setActive(true);
        job.setCreatedAt(LocalDateTime.now());
        job.setExpiresAt(LocalDateTime.now().plusMonths(3));
        job.setRecruiter(recruiter);
        return job;
    }
}
