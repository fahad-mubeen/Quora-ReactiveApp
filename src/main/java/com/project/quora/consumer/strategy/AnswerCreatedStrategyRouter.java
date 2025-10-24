package com.project.quora.consumer.strategy;

import com.project.quora.event.AnswerCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AnswerCreatedStrategyRouter {
    private final List<IAnswerCreatedStrategy> strategies;

    public void handleEvent(AnswerCreatedEvent event) {
        for (IAnswerCreatedStrategy strategy : strategies) {
            strategy.process(event);
        }
    }
}
