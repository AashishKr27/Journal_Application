package com.journal.journal.repository;

import com.journal.journal.entity.User;
import lombok.NonNull;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<@NonNull User, @NonNull String> {
    User findByUsername(String username);
    boolean deleteByUsername(String username);
}
