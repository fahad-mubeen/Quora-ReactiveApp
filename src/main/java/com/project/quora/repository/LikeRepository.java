package com.project.quora.repository;

import com.project.quora.model.Like;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface LikeRepository extends ReactiveMongoRepository<Like, String> {

}
