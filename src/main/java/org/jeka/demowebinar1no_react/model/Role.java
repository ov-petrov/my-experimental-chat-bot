package org.jeka.demowebinar1no_react.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("user") {
        @Override
        Message getMessage(String text) {
            return new UserMessage(text);
        }
    },
    ASSISTANT("assistant") {
        @Override
        Message getMessage(String text) {
            return new AssistantMessage(text);
        }
    },
    SYSTEM("system") {
        @Override
        Message getMessage(String text) {
            return new SystemMessage(text);
        }
    };

    private final String role;

    public static Role getRole(String roleName) {
        return Arrays.stream(Role.values()).filter(r -> r.getRole().equalsIgnoreCase(roleName)).findAny().orElseThrow();
    }

    abstract Message getMessage(String text);

}
