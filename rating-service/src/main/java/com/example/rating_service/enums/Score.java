package com.example.rating_service.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Score {
    ONE_STAR(1),
    TWO_STARS(2),
    THREE_STARS(3),
    FOUR_STARS(4),
    FIVE_STARS(5);

    private final int value;

    Score(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @JsonValue
    public String getName() {
        return this.name();
    }

    @JsonCreator
    public static Score from(String value) {
        return Score.valueOf(value.toUpperCase());
    }
}

