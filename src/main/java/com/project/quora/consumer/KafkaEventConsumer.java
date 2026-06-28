package com.project.quora.consumer;

import com.project.quora.consumer.strategy.AnswerCreatedStrategyRouter;
import com.project.quora.consumer.strategy.QuestionSyncStrategyRouter;
import com.project.quora.consumer.strategy.ViewCountStrategyRouter;
import com.project.quora.event.AnswerCreatedEvent;
import com.project.quora.event.QuestionUpdateEvent;
import com.project.quora.event.ViewCountEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventConsumer {

    private final ViewCountStrategyRouter viewCountStrategyRouter;

    private final AnswerCreatedStrategyRouter answerCreatedStrategyRouter;

    private final QuestionSyncStrategyRouter questionSyncStrategyRouter;

    @KafkaListener(
            topics = "view-count-topic",
            groupId = "view-count-consumer-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeViewCountEvent(ViewCountEvent viewCountEvent) {
        viewCountStrategyRouter.handleView(viewCountEvent);
    }

    @KafkaListener(
            topics = "answer-created-topic",
            groupId = "answer-created-consumer-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeAnswerCreatedEvent(AnswerCreatedEvent answerCreatedEvent) {
        answerCreatedStrategyRouter.handleEvent(answerCreatedEvent);
    }

    @KafkaListener(
            topics = "question-updates",
            groupId = "elastic-sync-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeQuestionUpdateEvent(QuestionUpdateEvent event) {
        questionSyncStrategyRouter.route("ELASTICSEARCH", event);
    }
}
