package com.project.quora.consumer.strategy;

import com.project.quora.event.QuestionUpdateEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class QuestionSyncStrategyRouter {

    private final Map<String, IQuestionSyncStrategy> strategyMap;

    public QuestionSyncStrategyRouter(List<IQuestionSyncStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        IQuestionSyncStrategy::getSyncType,
                        Function.identity()
                ));
    }

    public void route(String syncType, QuestionUpdateEvent event) {
        IQuestionSyncStrategy strategy = strategyMap.get(syncType);
        if (strategy == null) {
            throw new IllegalArgumentException("No sync strategy found for type: " + syncType);
        }
        strategy.process(event);
    }
}
