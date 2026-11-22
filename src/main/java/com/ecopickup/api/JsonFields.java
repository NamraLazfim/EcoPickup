package com.ecopickup.api;

// Small JSON reader for the few fields we need from Nominatim.
final class JsonFields {

    private static final int HEX_RADIX = 16;
    private static final int UNICODE_DIGITS = 4;

    private JsonFields() {
    }

    static String firstString(String json, String key) {
        String marker = "\"" + key + "\"";
        int keyAt = json.indexOf(marker);
        if (keyAt < 0) {
            return null;
        }
        int colon = json.indexOf(':', keyAt + marker.length());
        int open = colon < 0 ? -1 : json.indexOf('"', colon);
        return open < 0 ? null : readString(json, open + 1);
    }

    private static String readString(String json, int start) {
        StringBuilder value = new StringBuilder();
        int index = start;
        while (index < json.length() && json.charAt(index) != '"') {
            char current = json.charAt(index);
            if (current == '\\' && index + 1 < json.length()) {
                index = appendEscape(json, index + 1, value);
            } else {
                value.append(current);
                index++;
            }
        }
        return value.toString();
    }

    private static int appendEscape(String json, int index, StringBuilder value) {
        char code = json.charAt(index);
        if (code == 'u' && index + UNICODE_DIGITS < json.length()) {
            String hex = json.substring(index + 1, index + 1 + UNICODE_DIGITS);
            value.append((char) Integer.parseInt(hex, HEX_RADIX));
            return index + 1 + UNICODE_DIGITS;
        }
        value.append(switch (code) {
            case 'n' -> '\n';
            case 't' -> '\t';
            default -> code;
        });
        return index + 1;
    }
}
