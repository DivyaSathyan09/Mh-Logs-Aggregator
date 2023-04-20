package com.mhcure.logmerge.constants;

import lombok.Getter;

@Getter
public enum UserPromptConstants {
    PROMPT_MESSAGE_TO_ASK_USER_FOR_SAVING_DECRYPTED_FILES("There might be some encrypted file !\nPlease press S to save the decrypted version of files or N to continue without saving"),
  PROMPT_MESSAGE_AT_INVALID_KEY_FOR_SAVING_DECRYPTED_FILES("Invalid entry !\nPlease press S to save the decrypted version of files or N to continue without saving"),
    PROMPT_MESSAGE_WHEN_FILE_IS_INVALID("is not a valid file. So skipping"),
    ;
    private final String key;

    UserPromptConstants(String key) {
        this.key = key;
    }


}
