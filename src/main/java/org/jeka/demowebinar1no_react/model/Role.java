package org.jeka.demowebinar1no_react.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER("user"),
    ASSISTANT("assistant"),
    SYSTEM("system");

    private final String role;

    public static Role getRole(String roleName) {
        return Arrays.stream(Role.values()).filter(r -> r.getRole().equalsIgnoreCase(roleName)).findAny().orElseThrow();
    }

}
