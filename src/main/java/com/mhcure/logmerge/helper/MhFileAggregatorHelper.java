package com.mhcure.logmerge.helper;

public class MhFileAggregatorHelper {


    public static void printToConsole(String messages){
        System.out.println(messages);
    }

    public static boolean isFileEncrypted(String fileName) {
        return fileName.toLowerCase().endsWith(".encrypted");
    }
}
