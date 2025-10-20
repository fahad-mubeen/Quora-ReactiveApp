package com.project.quora.producer;

import com.project.quora.config.KafkaConfig;
import com.project.quora.event.ViewCountEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishViewCountEvent(ViewCountEvent viewCountEvent) {
        kafkaTemplate.send(KafkaConfig.TOPIC_NAME, viewCountEvent.getTargetId(), viewCountEvent)
                .whenComplete((res, err) -> {
                    if(err != null) {
                        System.err.println("Error publishing ViewCountEvent: " + err.getMessage());
                    } else {
                        System.out.println("ViewCountEvent published successfully for targetId: " + viewCountEvent.getTargetId());
                    }
                });
    }
}
