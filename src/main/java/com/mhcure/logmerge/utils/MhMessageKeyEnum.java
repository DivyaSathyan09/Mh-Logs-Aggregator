package com.mhcure.logmerge.utils;

import lombok.Getter;

@Getter
public enum MhMessageKeyEnum {


    ASK_MAXIMUM_LINES_IN_LOG_FILE("com.mhcure.userPrompt.message.ask_total_maximum_number_of_lines_in_each_log_file"),
    ASK_TO_SAVE_LOGS_IN_MULTIPLE_FILES("com.mhcure.logfiles.ask_to_save_logs_in_multiple_files"),
    IGNORE_MERGING("com.mhcure.logfiles.ignore_merging"),
    IGNORE_INVALID_FILE("com.mhcure.logfiles.skip_invalid_files"),
    INVALID_DECRYPTED_LOCATION("com.mhcure.userInfo.message.validDecryptedLocation"),
    INVALID_ENTRY_TO_SAVE_LOGS_IN_MULTIPLE_FILES("com.mhcure.logfiles.invalid.entry_ask_to_save_logs_in_multiple_files"),
    INVALID_INPUT_TO_CONTINUE_MERGING_LOG_FILE("com.mhcure.logfiles.invalid.entry_to_continue_merging_log.files"),
    INVALID_INPUT_TO_SAVE_DECRYPTED_FILE("com.mhcure.logfiles.invalid.entry_to_save_decrypted.files"),
    Invalid_Key("com.mhcure.userInfo.message.invalid_key"),
    INVALID_LOGFILE_OUTPUT_LOCATION("com.mhcure.userInfo.message.validLogfilesOutputLocation"),
    MESSAGE_FINISHED_MERGING("com.mhcure.userInfo.message.mergingFiles"),
    MESSAGE_INVALID_FILE_LOCATION("com.mhcure.userInfo.message.validLocation"),
    MESSAGE_LOG_FILES_LOCATION("com.mhcure.userPrompt.message.locationIs"),
    MESSAGE_NO_FILES_FOUND("com.mhcure.userInfo.message.notfound"),
    MESSAGE_PROCESSING_FILE("com.mhcure.userInfo.message.processingFiles"),
    MESSAGE_SPECIFIED_FOLDER("com.mhcure.userPrompt.message.folderSpecified"),
    MESSAGE_TO_SAVE_DECRYPTED_FILES("com.mhcure.logfiles.ask_to_save.decrypted.files"),
    MESSAGE_TOTAL_TIME_TO_RUN_PROGRAM("com.mhcure.userInfo.message.totalTime.to.runProgram"),
    SAVE_DECRYPTED_FILES("com.mhcure.userInfo.message.save.decrypted.files"),
    TOTAL_FILES_FOUND("com.mhcure.userInfo.message.totalFiles"),
    TOTAL_TIME_TO_READ_FILES("com.mhcure.userInfo.message.totalTime.to.readFiles"),
    TOTAL_TIME_TO_WRITE_FILES("com.mhcure.userInfo.message.totalTime.to.writeFiles"),
    WRITE_BUFFERED_SIZE("com.mhcure.userInfo.message.bufferSize");

    private final String key;
    MhMessageKeyEnum(String key) {
        this.key = key;
    }
}
