package com.mhcure.logmerge.utils;

import lombok.Getter;

@Getter
public enum MhMessageKeyEnum {
    TEXT_FILES("com.mhcure.userInfo.message.files"),
    IGNORE_MERGING("com.mhcure.logfiles.ignore_merging"),
    MESSAGE_INVALID_FILES("com.mhcure.logfiles.invalid_file"),
    TOTAL_FILES_FOUND("com.mhcure.userInfo.message.totalfiles"),
    WRITE_BUFFERED_SIZE("com.mhcure.userInfo.message.buffersize"),
    MESSAGE_NO_FILES_FOUND("com.mhcure.userInfo.message.notfound"),
    MERGED_FILE_LOCATION("com.mhcure.userInfo.message.mergelogfiles"),
    MESSAGE_FINISHED_MERGING("com.mhcure.userInfo.message.mergingfiles"),
    MESSAGE_FOR_INVALID_ENTRY("com.mhcure.userInfo.message.invalidentry"),
    MESSAGE_LOG_FILES_LOCATION("com.mhcure.userPrompt.message.locationis"),
    MESSAGE_PROCESSING_FILE("com.mhcure.userInfo.message.processingfiles"),
    MESSAGE_FILES_TO_BE_MERGED("com.mhcure.userInfo.message.filesTBeMerge"),
    CORRECT_FOLDER_VALUE("com.mhcure.userPrompt.message.correctFolderValue"),
    MESSAGE_SPECIFIED_FOLDER("com.mhcure.userPrompt.message.folderspecified"),
    MESSAGE_INVALID_FILE_LOCATION("com.mhcure.userInfo.message.validlocation"),
    TOTAL_TIME_TO_READ_FILES("com.mhcure.userInfo.message.totaltime.to.readfiles"),
    TOTAL_TIME_TO_WRITE_FILES("com.mhcure.userInfo.message.totaltime.to.writefiles"),
    MESSAGE_TO_IGNORE_OUTPUT_FILES("com.mhcure.userInfo.message.ignore_outputfiles"),
    MESSAGE_TO_SAVE_DECRYPTED_FILES("com.mhcure.logfiles.ask_to_save.decrypted.files"),
    MESSAGE_TOTAL_TIME_TO_RUN_PROGRAM("com.mhcure.userInfo.message.totaltime.to.runprogram"),
    INVALID_INPUT_TO_SAVE_DECRYPTED_FILE("com.mhcure.logfiles.invalid.entry_to_save_decrypted.files"),
    INVALID_ENTRY_TO_SAVE_LOGS_IN_MULTIPLE_FILES("com.mhcure.logfiles.invalid.entry_ask_to_save_logs_in_multiple_files"),
    ASK_TO_SAVE_LOGS_IN_MULTIPLE_FILES("com.mhcure.logfiles.ask_to_save_logs_in_multiple_files");
    private final String key;

    MhMessageKeyEnum(String key) {

        this.key = key;
    }
}
