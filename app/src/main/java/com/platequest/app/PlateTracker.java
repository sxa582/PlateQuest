package com.platequest.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Pure Java game logic, separated from Android UI for easy testing. */
public final class PlateTracker {
    public static final int POSITION_COUNT = 7;
    public static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static final String DIGITS = "0123456789";
    public static final int TOTAL_SLOTS = (3 * CHARACTERS.length()) + (4 * DIGITS.length());

    private PlateTracker() {}

    public static String normalize(String raw) {
        if (raw == null) return "";
        String upper = raw.toUpperCase(Locale.US);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < upper.length(); i++) {
            char c = upper.charAt(i);
            if ((c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')) {
                result.append(c);
            }
        }
        return result.toString();
    }

    public static String formatForDisplay(String normalized) {
        String clean = normalize(normalized);
        if (clean.length() <= 3) return clean;
        return clean.substring(0, 3) + "-" + clean.substring(3);
    }

    public static boolean isValid(String raw) {
        String plate = normalize(raw);
        if (plate.length() != POSITION_COUNT) return false;
        for (int i = 0; i < POSITION_COUNT; i++) {
            if (charactersForPosition(i).indexOf(plate.charAt(i)) < 0) return false;
        }
        return true;
    }

    public static String charactersForPosition(int positionIndex) {
        return positionIndex >= 3 ? DIGITS : CHARACTERS;
    }

    public static List<Discovery> findNewDiscoveries(
            String rawPlate,
            List<Set<String>> discoveredByPosition
    ) {
        String plate = normalize(rawPlate);
        if (!isValid(plate) || discoveredByPosition.size() != POSITION_COUNT) {
            return Collections.emptyList();
        }

        List<Discovery> discoveries = new ArrayList<>();
        for (int i = 0; i < POSITION_COUNT; i++) {
            String character = String.valueOf(plate.charAt(i));
            if (!discoveredByPosition.get(i).contains(character)) {
                discoveries.add(new Discovery(i, character));
            }
        }
        return discoveries;
    }

    public static int totalFound(List<Set<String>> discoveredByPosition) {
        int total = 0;
        for (Set<String> set : discoveredByPosition) total += set.size();
        return total;
    }

    public static int percentComplete(int totalFound) {
        return Math.round(totalFound * 100f / TOTAL_SLOTS);
    }

    public static int totalSlotsForPosition(int positionIndex) {
        return charactersForPosition(positionIndex).length();
    }

    public static final class Discovery {
        public final int positionIndex;
        public final String character;

        public Discovery(int positionIndex, String character) {
            this.positionIndex = positionIndex;
            this.character = character;
        }
    }
}
