package com.project.quora.service;

import com.project.quora.dto.LikeRequestDTO;
import com.project.quora.dto.LikeResponseDTO;
import com.project.quora.enums.TargetType;
import reactor.core.publisher.Mono;

public interface ILikeService {

    Mono<LikeResponseDTO> createLike(LikeRequestDTO answerRequestDTO);

    Mono<Integer> countLikesByTargetIdAndLikeType(String targetId, TargetType targetType);

    Mono<Integer> countDisLikesByTargetIdAndLikeType(String targetId, TargetType targetType);

    Mono<LikeResponseDTO> toggleLike(String targetId, TargetType targetType);

}
