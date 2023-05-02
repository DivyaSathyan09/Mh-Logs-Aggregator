package com.mhcure.logmerge.helper;

import com.mhcure.logmerge.constants.MhFileConstants;

public class MhFileAggregatorHelper {

    public static void printInstructionsOnConsole(String msg) {
        System.out.println("**********************************************************");
        System.out.println(msg);
        System.out.println("**********************************************************");
    }

    public static void printToConsole(String messages) {
        System.out.println(messages);
    }

    public static boolean isFileEncrypted(String fileName) {
        return fileName.toLowerCase().endsWith(MhFileConstants.ENCRYPTED);
    }
}
