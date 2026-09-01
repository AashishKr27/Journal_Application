package com.journal.journal.repository;

import com.journal.journal.entity.ConfigJournalAppEntity;
import lombok.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfigJournalAppRepository extends MongoRepository<@NonNull ConfigJournalAppEntity, @NonNull String> {

}
