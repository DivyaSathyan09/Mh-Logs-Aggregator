package com.mhcure.logmerge.utils;

import lombok.Getter;

@Getter
public enum MhMessageKeyEnum {

    IGNORE_MERGING("com.mhcure.logfiles.ignore_merging"),
    TOTAL_FILES_FOUND("com.mhcure.userInfo.message.totalFiles"),
    WRITE_BUFFERED_SIZE("com.mhcure.userInfo.message.bufferSize"),
    IGNORE_INVALID_FILE("com.mhcure.logfiles.skip_invalid_files"),
    MESSAGE_NO_FILES_FOUND("com.mhcure.userInfo.message.notfound"),
    MESSAGE_FINISHED_MERGING("com.mhcure.userInfo.message.mergingFiles"),
    MESSAGE_LOG_FILES_LOCATION("com.mhcure.userPrompt.message.locationIs"),
    MESSAGE_PROCESSING_FILE("com.mhcure.userInfo.message.processingFiles"),
    MESSAGE_SPECIFIED_FOLDER("com.mhcure.userPrompt.message.folderSpecified"),
    MESSAGE_INVALID_FILE_LOCATION("com.mhcure.userInfo.message.validLocation"),
    TOTAL_TIME_TO_READ_FILES("com.mhcure.userInfo.message.totalTime.to.readFiles"),
    TOTAL_TIME_TO_WRITE_FILES("com.mhcure.userInfo.message.totalTime.to.writeFiles"),
    MESSAGE_TO_SAVE_DECRYPTED_FILES("com.mhcure.logfiles.ask_to_save.decrypted.files"),
    MESSAGE_TOTAL_TIME_TO_RUN_PROGRAM("com.mhcure.userInfo.message.totalTime.to.runProgram"),
    ASK_TO_SAVE_LOGS_IN_MULTIPLE_FILES("com.mhcure.logfiles.ask_to_save_logs_in_multiple_files"),
    INVALID_INPUT_TO_SAVE_DECRYPTED_FILE("com.mhcure.logfiles.invalid.entry_to_save_decrypted.files"),
    INVALID_INPUT_TO_CONTINUE_MERGING_LOG_FILE("com.mhcure.logfiles.invalid.entry_to_continue_merging_log.files"),
    ASK_MAXIMUM_LINES_IN_LOG_FILE("com.mhcure.userPrompt.message.ask_total_maximum_number_of_lines_in_each_log_file"),
    INVALID_ENTRY_TO_SAVE_LOGS_IN_MULTIPLE_FILES("com.mhcure.logfiles.invalid.entry_ask_to_save_logs_in_multiple_files");

    private final String key;

    MhMessageKeyEnum(String key) {
        this.key = key;
    }
}
