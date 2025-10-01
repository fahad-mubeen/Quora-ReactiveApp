package com.project.quora.service;

import com.project.quora.dto.LikeRequestDTO;
import com.project.quora.dto.LikeResponseDTO;
import com.project.quora.enums.LikeFor;
import reactor.core.publisher.Mono;

public interface ILikeService {

    Mono<LikeResponseDTO> createLike(LikeRequestDTO answerRequestDTO);

    Mono<Integer> countLikesByTargetIdAndLikeType(String targetId, LikeFor likeFor);

    Mono<Integer> countDisLikesByTargetIdAndLikeType(String targetId, LikeFor likeFor);

    Mono<LikeResponseDTO> toggleLike(String targetId, LikeFor likeFor);

}
