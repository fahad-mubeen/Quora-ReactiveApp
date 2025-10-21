package com.project.quora.consumer.strategy.impl.view;

import com.project.quora.consumer.strategy.IViewCountStrategy;
import com.project.quora.enums.TargetType;
import com.project.quora.event.ViewCountEvent;
import com.project.quora.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QuestionViewCountStrategy implements IViewCountStrategy {

    private final QuestionRepository questionRepository;

    @Override
    public TargetType getSupportedType() {
        return TargetType.QUESTION;
    }

    @Override
    public void process(ViewCountEvent event) {
        questionRepository.findById(event.getTargetId())
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
    }
}
