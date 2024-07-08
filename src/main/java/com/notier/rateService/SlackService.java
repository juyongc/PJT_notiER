package com.notier.rateService;

import com.slack.api.Slack;
import com.slack.api.methods.MethodsClient;
import com.slack.api.methods.SlackApiException;
import com.slack.api.methods.request.chat.ChatPostMessageRequest;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class SlackService {

    private static final String SLACK_CHANNEL = "#slack_notice";

    @Value("${slack.token}")
    private String slackToken;

    public void sendSlackMessage(String message) {

        try {

            MethodsClient methods = Slack.getInstance().methods(slackToken);

            ChatPostMessageRequest request = ChatPostMessageRequest.builder()
                .channel(SLACK_CHANNEL)
                .text(message)
                .build();

            methods.chatPostMessage(request);

            log.info("Send a message to Slack: " + SLACK_CHANNEL);

        } catch (SlackApiException | IOException e) {
            log.error(e.getMessage());
        }
    }

}
