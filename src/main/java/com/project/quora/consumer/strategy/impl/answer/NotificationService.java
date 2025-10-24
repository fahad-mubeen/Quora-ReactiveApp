package com.project.quora.consumer.strategy.impl.answer;

import com.project.quora.consumer.strategy.IAnswerCreatedStrategy;
import com.project.quora.event.AnswerCreatedEvent;
import org.springframework.stereotype.Service;

@Service
public class NotificationService implements IAnswerCreatedStrategy {
    @Override
    public void process(AnswerCreatedEvent answerCreatedEvent) {
        System.out.println("NotificationService: Sending notification for new answer with ID " + answerCreatedEvent.getAnswerId());
    }
}
