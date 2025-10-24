package com.project.quora.consumer.strategy;

import com.project.quora.event.AnswerCreatedEvent;

public interface IAnswerCreatedStrategy {
    void process(AnswerCreatedEvent answerCreatedEvent);
}
