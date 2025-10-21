package com.project.quora.consumer;

import com.project.quora.config.KafkaConfig;
import com.project.quora.consumer.strategy.ViewCountStrategyRouter;
import com.project.quora.enums.TargetType;
import com.project.quora.event.ViewCountEvent;
import com.project.quora.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class KafkaEventConsumer {

    private final ViewCountStrategyRouter viewCountStrategyRouter;

    @KafkaListener(
            topics = KafkaConfig.TOPIC_NAME,
            groupId = KafkaConfig.consumerGroupId,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeViewCountEvent(ViewCountEvent viewCountEvent) {
        viewCountStrategyRouter.handleView(viewCountEvent);
    }
}
