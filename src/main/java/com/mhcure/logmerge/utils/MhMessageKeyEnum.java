package com.mhcure.logmerge.utils;

import lombok.Getter;

@Getter
public enum MhMessageKeyEnum {
    MESSAGE_TO_IGNORE_OUTPUT_FILES("com.mhcure.userInfo.message.ignore_outputfiles"),
    MESSAGE_SPECIFIED_FOLDER("com.mhcure.userPrompt.message.folderspecified"),
    CORRECT_FOLDER_VALUE("com.mhcure.userPrompt.message.correctFolderValue"),
    MESSAGE_TOTAL_TIME_TO_RUN_PROGRAM("com.mhcure.userInfo.message.totaltime.to.runprogram"),
    WRITE_BUFFERED_SIZE("com.mhcure.userInfo.message.buffersize"),
    TOTAL_TIME_TO_WRITE_FILES("com.mhcure.userInfo.message.totaltime.to.writefiles"),
    TOTAL_TIME_TO_READ_FILES("com.mhcure.userInfo.message.totaltime.to.readfiles"),
    MERGED_FILE_LOCATION("com.mhcure.userInfo.message.mergelogfiles"),
    MESSAGE_FOR_INVALID_ENTRY("com.mhcure.userInfo.message.invalidentry"),
    MESSAGE_FILES_TO_BE_MERGED("com.mhcure.userInfo.message.filesTBeMerge"),
    TOTAL_FILES_FOUND("com.mhcure.userInfo.message.totalfiles"),
    MESSAGE_PROCESSING_FILE("com.mhcure.userInfo.message.processingfiles"),

    MESSAGE_FINISHED_MERGING("com.mhcure.userInfo.message.mergingfiles"),
    TEXT_FILES("com.mhcure.userInfo.message.files"),

    MESSAGE_LOG_FILES_LOCATION("com.mhcure.userPrompt.message.locationis"),
    MESSAGE_INVALID_FILE_LOCATION("com.mhcure.userInfo.message.validlocation"),
    MESSAGE_NO_FILES_FOUND("com.mhcure.userInfo.message.notfound"),
    MESSAGE_TO_SAVE_DECRYPTED_FILES("com.mhcure.logfiles.ask_to_save.decrypted.files"),
    INVALID_INPUT_TO_SAVE_DECRYPTED_FILE("com.mhcure.logfiles.invalid.entry_to_save_decrypted.files"),
    IGNORE_MERGING("com.mhcure.logfiles.ignore_merging"),
    MESSAGE_INVALID_FILES("com.mhcure.logfiles.invalid_file");

    private final String key;

    MhMessageKeyEnum(String key) {

        this.key = key;
    }
}
