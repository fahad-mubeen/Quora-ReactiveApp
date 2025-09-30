package com.project.quora.utils;

import java.time.LocalDateTime;

public class CursorUtils {
    public static boolean isValidCursor(String cursor) {
        if (cursor == null || cursor.isEmpty()) {
            return false;
        }
        try {
            LocalDateTime.parse(cursor);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static LocalDateTime parseCursor(String cursor) {
        if(!isValidCursor(cursor)) {
            throw new IllegalArgumentException("Invalid cursor format");
        }
        return LocalDateTime.parse(cursor);
    }
}
