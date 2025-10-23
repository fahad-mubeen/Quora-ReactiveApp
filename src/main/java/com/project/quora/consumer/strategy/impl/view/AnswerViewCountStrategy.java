package com.project.quora.consumer.strategy.impl.view;

import com.project.quora.consumer.strategy.IViewCountStrategy;
import com.project.quora.enums.TargetType;
import com.project.quora.event.ViewCountEvent;
import com.project.quora.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnswerViewCountStrategy implements IViewCountStrategy {

    private final AnswerRepository answerRepository;

    @Override
    public TargetType getSupportedType() {
        return TargetType.ANSWER;
    }

    @Override
    public void process(ViewCountEvent event) {
        answerRepository.findById(event.getTargetId())
                .flatMap(answer -> {
                    answer.setViewCount(answer.getViewCount() + 1);
                    return answerRepository.save(answer);
                })
                .subscribe(
                        updatedAnswer -> {
                            System.out.println("View count increment for answer: " + updatedAnswer.getId());
                        }, error -> {
                            System.out.println("Error incrementing view count");
                        }
                );
    }
}
