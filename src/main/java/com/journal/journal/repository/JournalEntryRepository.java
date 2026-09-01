package com.journal.journal.repository;

import com.journal.journal.entity.JournalEntry;
import lombok.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface JournalEntryRepository extends MongoRepository<@NonNull JournalEntry, @NonNull String> {

}
