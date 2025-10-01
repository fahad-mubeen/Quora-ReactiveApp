package com.project.quora.repository;

import com.project.quora.model.Like;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends ReactiveMongoRepository<Like, String> {

}
