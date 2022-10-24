package io.github.ms5984.retrox.accessories.internal;
/*
 *  Copyright 2022 ms5984
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class ColorLib {
    public static final Pattern COLOR_PATTERN = Pattern.compile("&([a-fA-F\\dklmnor]|#[a-fA-F\\d]{6})");

    public static @NotNull String processDisplayName(@NotNull String input) {
        return replaceLegacyCodes(input, "<!i>"); // override MC-default italics
    }

    public static @NotNull String processLoreLine(@NotNull String input) {
        return replaceLegacyCodes(input, "<!i><white>"); // override MC-default italics + dark_purple
    }

    static String replaceLegacyCodes(@NotNull String input, @NotNull String defaults) {
        final var match = COLOR_PATTERN.matcher(input);
        final StringBuilder sb = new StringBuilder(defaults);
        final HashSet<String> activeFormats = new HashSet<>();
        // search for legacy codes
        while (match.find()) {
            final var group = match.group(1);
            switch (group.toLowerCase(Locale.ROOT).charAt(0)) {
                // add format to active formats if necessary
                case 'k', 'l', 'm', 'n', 'o' -> activeFormats.add(group);
                // if we find a color code, close all active formats
                case 'a', 'b', 'c', 'd', 'e', 'f', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '#' -> {
                    match.appendReplacement(sb, activeFormats.stream()
                            .map(format -> tagFromAmpersand(format, true))
                            .collect(Collectors.joining()) + tagFromAmpersand(group, false));
                    activeFormats.clear();
                    continue;
                }
            }
            match.appendReplacement(sb, tagFromAmpersand(group, false));
        }
        match.appendTail(sb);
        return sb.toString();
    }

    public static String tagFromAmpersand(String code, boolean close) {
        final StringBuilder sb = new StringBuilder("<");
        if (close) sb.append('/');
        if (code.length() == 7) {
            // hex code
            return sb.append(code).append(">").toString();
        } else if (code.length() != 1) {
            // invalid code
            return "";
        }
        return sb.append(switch (code.charAt(0)) {
            case '0' -> "black";
            case '1' -> "dark_blue";
            case '2' -> "dark_green";
            case '3' -> "dark_aqua";
            case '4' -> "dark_red";
            case '5' -> "dark_purple";
            case '6' -> "gold";
            case '7' -> "gray";
            case '8' -> "dark_gray";
            case '9' -> "blue";
            case 'a' -> "green";
            case 'b' -> "aqua";
            case 'c' -> "red";
            case 'd' -> "light_purple";
            case 'e' -> "yellow";
            case 'f' -> "white";
            case 'k' -> "obfuscated";
            case 'l' -> "bold";
            case 'm' -> "strikethrough";
            case 'n' -> "underline";
            case 'o' -> "italic";
            case 'r' -> "reset";
            default -> throw new IllegalStateException("Unexpected value: " + code);
        }).append(">").toString();
    }
}
