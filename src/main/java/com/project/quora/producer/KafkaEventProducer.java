package com.project.quora.producer;

import com.project.quora.config.KafkaConfig;
import com.project.quora.event.AnswerCreatedEvent;
import com.project.quora.event.ViewCountEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishViewCountEvent(ViewCountEvent viewCountEvent) {
        kafkaTemplate.send("view-count-topic", viewCountEvent.getTargetId(), viewCountEvent);
    }

    public void publishAnswerCreatedEvent(AnswerCreatedEvent answerCreatedEvent) {
        kafkaTemplate.send("answer-created-topic", answerCreatedEvent);
    }
}
