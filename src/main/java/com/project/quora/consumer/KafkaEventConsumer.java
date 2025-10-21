package com.project.quora.consumer;

import com.project.quora.config.KafkaConfig;
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

    private final QuestionRepository questionRepository;

    @Transactional
    @KafkaListener(
            topics = KafkaConfig.TOPIC_NAME,
            groupId = KafkaConfig.consumerGroupId,
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeViewCountEvent(ViewCountEvent viewCountEvent) {
        // Placeholder for future implementation
        if(viewCountEvent.getTargetType().equals(TargetType.QUESTION)){
            questionRepository.findById(viewCountEvent.getTargetId())
                    .flatMap(question -> {
                        question.setViewCount(question.getViewCount() + 1);
                        return questionRepository.save(question);
                    })
                    .subscribe(
                            updatedQuestion -> {
                                System.out.println("View count increment for question: " + updatedQuestion.getId());
                            }, error -> {
                                System.out.println("Error incrementing view count");
                            }
                    );
        } else if(viewCountEvent.getTargetType().equals(TargetType.ANSWER)){

        } else if(viewCountEvent.getTargetType().equals(TargetType.COMMENT)){

        }
        else {

        }
    }
}
