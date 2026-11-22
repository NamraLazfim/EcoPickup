package com.ecopickup.gui;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

// Turns typed text into values, with friendly error messages.
final class Input {

    private Input() {
    }

    static void requireText(String text, String message) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    static LocalDate date(String text, String field) {
        requireText(text, "Enter the " + field + ".");
        try {
            return LocalDate.parse(text.trim());
        } catch (DateTimeParseException badFormat) {
            throw new IllegalArgumentException("Enter the " + field + " as yyyy-mm-dd, for example "
                    + LocalDate.now().plusDays(1) + ".");
        }
    }

    static double positiveNumber(String text, String field) {
        requireText(text, "Enter the " + field + ".");
        double value;
        try {
            value = Double.parseDouble(text.trim());
        } catch (NumberFormatException notNumber) {
            value = Double.NaN;
        }
        // NaN and Infinity parse fine in Java, so they need their own check
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("The " + field + " must be a number, for example 12.5.");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("The " + field + " must be greater than 0.");
        }
        return value;
    }

    static int positiveWholeNumber(String text, String field) {
        requireText(text, "Enter the " + field + ".");
        int value;
        try {
            value = Integer.parseInt(text.trim());
        } catch (NumberFormatException notNumber) {
            throw new IllegalArgumentException("The " + field + " must be a whole number.");
        }
        if (value <= 0) {
            throw new IllegalArgumentException("The " + field + " must be greater than 0.");
        }
        return value;
    }
}
