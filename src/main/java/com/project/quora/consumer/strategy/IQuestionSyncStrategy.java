package com.project.quora.consumer.strategy;

import com.project.quora.event.QuestionUpdateEvent;

public interface IQuestionSyncStrategy {
    String getSyncType();
    void process(QuestionUpdateEvent event);
}
