package com.journal.journal.service;

import com.journal.journal.entity.JournalEntry;
import com.journal.journal.entity.User;
import com.journal.journal.repository.JournalEntryRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class JournalEntryService {
    @Autowired
    private JournalEntryRepository journalEntryRepository;

    @Autowired
    private UserService userService;

    public void saveEntry(JournalEntry journalEntry, String username) throws BadRequestException {
        try {
            User user = userService.findByUsername(username);
            journalEntry.setDate(LocalDateTime.now());
            JournalEntry saved = journalEntryRepository.save(journalEntry);
            user.getJournalEntries().add(saved);
            userService.saveUser(user);
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

//    public List<JournalEntry> getEntry() {
//        return journalEntryRepository.findAll();
//    }

    public JournalEntry getEntryById(String _id) {
        return journalEntryRepository.findById(_id).orElse(null);
    }

    public void updateEntry(JournalEntry newEntry, String username, String id) throws BadRequestException {
        User user = userService.findByUsername(username);
        user.getJournalEntries().removeIf(x -> x.getId().equals(id));

        newEntry.setDate(LocalDateTime.now());
        JournalEntry saved = journalEntryRepository.save(newEntry);
        user.getJournalEntries().add(saved);

        userService.saveUser(user);
    }

    public void deleteAllEntries(String username) throws BadRequestException {
        User user = userService.findByUsername(username);
        for (JournalEntry entry : user.getJournalEntries()) {
            journalEntryRepository.deleteById(entry.getId());
        }
        user.getJournalEntries().clear();
        userService.saveUser(user);
    }

    public boolean deleteById(String _id, String username) throws BadRequestException {
        try {
            boolean removed = false;
            User user = userService.findByUsername(username);
            removed = user.getJournalEntries().removeIf(x -> x.getId().equals(_id));
            if(removed) {
                userService.saveUser(user);
                journalEntryRepository.deleteById(_id);
            } return removed;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
