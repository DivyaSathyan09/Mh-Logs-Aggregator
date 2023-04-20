package com.mhcure.logmerge.helper;


public class MhFileAggregatoHelper {

	public static void printInstructionsOnConsole(String msg) {
		System.out.println("**********************************************************");
		System.out.println(msg);
		System.out.println("**********************************************************");
	}

	public static boolean isFileEncrypted(String fileName) {
		return fileName.toLowerCase().endsWith(".encrypted");
	}
}
