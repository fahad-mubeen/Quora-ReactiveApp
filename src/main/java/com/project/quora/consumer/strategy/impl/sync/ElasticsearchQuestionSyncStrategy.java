package com.project.quora.consumer.strategy.impl.sync;

import com.project.quora.consumer.strategy.IQuestionSyncStrategy;
import com.project.quora.event.QuestionUpdateEvent;
import com.project.quora.mapper.QuestionMapper;
import com.project.quora.repository.QuestionElasticsearchRepository;
import com.project.quora.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ElasticsearchQuestionSyncStrategy implements IQuestionSyncStrategy {

    private final QuestionRepository questionRepository;
    private final QuestionElasticsearchRepository questionElasticsearchRepository;

    @Override
    public String getSyncType() {
        return "ELASTICSEARCH";
    }

    @Override
    public void process(QuestionUpdateEvent event) {
        questionRepository.findById(event.getQuestionId())
                .map(QuestionMapper::toQuestionDocument)
                .flatMap(questionElasticsearchRepository::save)
                .subscribe(
                        doc -> System.out.println("Synchronized question to ES: " + doc.getId()),
                        err -> System.err.println("Error syncing question to ES: " + err.getMessage())
                );
    }
}
