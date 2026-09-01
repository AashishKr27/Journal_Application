package com.journal.journal.scheduler;

import com.journal.journal.cache.AppCache;
import com.journal.journal.entity.JournalEntry;
import com.journal.journal.entity.User;
import com.journal.journal.repository.UserRepositoryImpl;
import com.journal.journal.service.EmailService;
import com.journal.journal.service.SentimentAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserScheduler {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepositoryImpl userRepository;

    @Autowired
    private SentimentAnalysisService sentimentAnalysisService;

    @Autowired
    private AppCache appCache;

    @Scheduled(cron = "0 21 22 * * *")
    public void fetchUserAndSendSAMail() {
        List<User> users = userRepository.getUserForSA();

        System.out.println("Scheduler is running!");
        System.out.println(STR."Users found: \{users.size()}");

        for (User user : users) {
            List<JournalEntry> journalEntries = user.getJournalEntries();

            List<String> filteredEntries = journalEntries.stream().filter(
                    x -> x.getDate().isAfter(LocalDateTime.now().minusDays(7))
            ).map(JournalEntry::getContent).toList();

            String entry = String.join(" ", filteredEntries);
            String sentiment = sentimentAnalysisService.getSentiment(entry);
            emailService.sendEmail(
                    user.getEmail(),
                    "Sentiment for last 7 days",
                    entry
            );
        }
    }

    @Scheduled(cron = "0 */10 * * * *")
    public void clearCache() {
        appCache.init();
    }
}
