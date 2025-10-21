package com.project.quora.consumer.strategy;

import com.project.quora.enums.TargetType;
import com.project.quora.event.ViewCountEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ViewCountStrategyRouter {
    private final Map<TargetType, IViewCountStrategy> strategyMap;

    // Spring injects all strategy beans into the list
    public ViewCountStrategyRouter(List<IViewCountStrategy> strategies) {
        // Create a Map where the key is the TargetType and the value is the strategy itself
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        IViewCountStrategy::getSupportedType,
                        Function.identity()
                ));
    }

    // Finds the correct strategy and executes its process method.
    public void handleView(ViewCountEvent event) {
        TargetType type = event.getTargetType();
        IViewCountStrategy chosenStrategy = strategyMap.get(type);

        if (chosenStrategy == null) {
            throw new IllegalArgumentException("No strategy found for type: " + type);
        }

        chosenStrategy.process(event);
    }
}
