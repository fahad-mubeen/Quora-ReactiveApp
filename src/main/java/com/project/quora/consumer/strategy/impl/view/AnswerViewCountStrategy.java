package com.project.quora.consumer.strategy.impl.view;

import com.project.quora.consumer.strategy.IViewCountStrategy;
import com.project.quora.enums.TargetType;
import com.project.quora.event.ViewCountEvent;
import org.springframework.stereotype.Component;

@Component
public class AnswerViewCountStrategy implements IViewCountStrategy {

    @Override
    public TargetType getSupportedType() {
        return TargetType.ANSWER;
    }

    @Override
    public void process(ViewCountEvent event) {
        System.out.println("Processing view count for ANSWER: " + event.getTargetId());
    }
}
