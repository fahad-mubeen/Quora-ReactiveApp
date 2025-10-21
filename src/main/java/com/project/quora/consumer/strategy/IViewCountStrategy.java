package com.project.quora.consumer.strategy;

import com.project.quora.enums.TargetType;
import com.project.quora.event.ViewCountEvent;

public interface IViewCountStrategy {
    TargetType getSupportedType();

    void process(ViewCountEvent event);
}
