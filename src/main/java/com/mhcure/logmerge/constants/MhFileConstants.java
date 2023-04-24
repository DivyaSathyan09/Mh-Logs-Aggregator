package com.mhcure.logmerge.constants;

import lombok.Getter;

@Getter
public enum MhFileConstants {
    BACKSLASH("\\"),
    NEW_LINE_CHAR("\n"),
    FILENAME_LOGS_TMT_SEPARATOR("|"),
    LINE_SEPARATOR("----------------------****************----------------------------------");
    private final String key;

    MhFileConstants(String key) {
        this.key = key;
    }


}
