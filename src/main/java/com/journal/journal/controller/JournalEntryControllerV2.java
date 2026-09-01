package com.journal.journal.controller;

import com.journal.journal.entity.JournalEntry;
import com.journal.journal.entity.User;
import com.journal.journal.service.JournalEntryService;
import com.journal.journal.service.UserService;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/journal")
public class JournalEntryControllerV2 {

    @Autowired
    private JournalEntryService journalEntryService;

    @Autowired
    private UserService userService;

    /* CREATE */
    @PostMapping()
    public ResponseEntity<@NonNull Object> createEntries(@RequestBody JournalEntry entries) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            if (userService.findByUsername(username) == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            } else {
                journalEntryService.saveEntry(entries, username);
                return new ResponseEntity<>("Journal created successfully", HttpStatus.OK);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /* READ */
    @GetMapping
    public ResponseEntity<@NonNull Object> getAllJournalEntriesOfUser() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            User user = userService.findByUsername(username);
            if(user.getJournalEntries() == null || user.getJournalEntries().isEmpty()){
                return new ResponseEntity<>("No journal entries found for user: " + username, HttpStatus.NOT_FOUND);
            } return new ResponseEntity<>(user.getJournalEntries(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred", HttpStatus.BAD_GATEWAY);
        }
    }

    /* READ MY ID */
    @GetMapping("/id/{myId}")
    public ResponseEntity<@NonNull Object> getById(@PathVariable String myId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        List<JournalEntry> collect = userService.findByUsername(username)
                .getJournalEntries().stream().filter(x -> x.getId().equals(myId))
                .toList();

        if(!collect.isEmpty()) {
            JournalEntry journalEntry = journalEntryService.getEntryById(myId);
            if (journalEntry != null) {
                return new ResponseEntity<>(journalEntry, HttpStatus.OK);
            }
        } return new ResponseEntity<>("Journal entry does not exist", HttpStatus.NOT_FOUND);
    }

    /* UPDATE BY ID */
    @PutMapping("/id/{id}")
    public ResponseEntity<@NonNull Object> updateEntry(@PathVariable String id, @RequestBody JournalEntry newEntry) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            List<JournalEntry> collect = userService.findByUsername(username)
                    .getJournalEntries().stream().filter(x -> x.getId().equals(id))
                    .toList();

            if(!collect.isEmpty()) {
                JournalEntry oldEntry = journalEntryService.getEntryById(id);
                if (oldEntry != null) {
                    oldEntry.setTitle(!newEntry.getTitle().isEmpty() ? newEntry.getTitle() : oldEntry.getTitle());
                    oldEntry.setContent(newEntry.getContent() != null && !newEntry.getContent().isEmpty() ? newEntry.getContent() : oldEntry.getContent());
                    journalEntryService.updateEntry(oldEntry, username, id);
                    return new ResponseEntity<>("Entry updated successfully", HttpStatus.OK);
                }
            } else {
                journalEntryService.saveEntry(newEntry, username);
                return new ResponseEntity<>("New entry created successfully", HttpStatus.OK);
            }

        } catch (Exception e) {
            return new ResponseEntity<>("Failed to update entries", HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    /* DELETE ONE BY ID */
    @DeleteMapping("/id/{myId}")
    public ResponseEntity<@NonNull String> deleteById(@PathVariable String myId) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            boolean removed = journalEntryService.deleteById(myId, username);
            if(removed) return new ResponseEntity<>("Entry deleted successfully", HttpStatus.OK);
            return new ResponseEntity<>("Entry does not exist", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed to delete entries", HttpStatus.BAD_REQUEST);
        }
    }

    /* DELETE ALL */
    @DeleteMapping
    public ResponseEntity<@NonNull String> deleteAll() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String username = auth.getName();

            List<JournalEntry> collect = userService.findByUsername(username).getJournalEntries();
            if(!collect.isEmpty()) {
                journalEntryService.deleteAllEntries(username);
                return new ResponseEntity<>("Entries deleted successfully", HttpStatus.OK);
            } return new ResponseEntity<>("Entries doesn't exist", HttpStatus.NOT_FOUND);

        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }
}
