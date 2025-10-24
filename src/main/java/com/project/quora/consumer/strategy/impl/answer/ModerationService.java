package com.project.quora.consumer.strategy.impl.answer;

import com.project.quora.consumer.strategy.IAnswerCreatedStrategy;
import com.project.quora.event.AnswerCreatedEvent;
import com.project.quora.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ModerationService implements IAnswerCreatedStrategy {

    private final WebClient.Builder webClientBuilder;
    private final AnswerRepository answerRepository;

    private static final String CUSTOM_PROFANITY_LIST = "badword";

    // A private inner class to match the JSON response from the API
    // returns: {"result": "filtered text..."}
    private static class FilteredResponse {
        public String result;
    }

    @Override
    public void process(AnswerCreatedEvent answerCreatedEvent) {
        WebClient client = webClientBuilder.build();

        // Call external API (this is a reactive, non-blocking call)
        Mono<String> filteredTextMono = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/json")
                        .queryParam("text", answerCreatedEvent.getAnswerText())
                        .queryParam("add", CUSTOM_PROFANITY_LIST)
                        .build())
                .retrieve() // Get the response
                .bodyToMono(FilteredResponse.class) // Maps the JSON to inner class
                .flatMap(response -> Mono.justOrEmpty(response.result));

        // We must .block() here.
        // This is a common and acceptable pattern in a Kafka listener.
        // It "bridges" the reactive (WebClient) and non-reactive (Kafka) worlds
        // by waiting for the API call to finish.
        // This is safe, because Kafka listener threads are dedicated to processing messages
        // blocking them here doesn’t break reactive principles globally.
        String filteredText = filteredTextMono.block();

        // 4. Update the answer in the database
        if (filteredText != null && !filteredText.equals(answerCreatedEvent.getAnswerText())) {
            // .subscribe() to make the reactive save happen
            answerRepository.findById(answerCreatedEvent.getAnswerId())
                    .flatMap(answer -> {
                        answer.setContent(filteredText);
                        return answerRepository.save(answer);
                    })
                    .subscribe(); // Fire-and-forget the database update
        }
    }
}
