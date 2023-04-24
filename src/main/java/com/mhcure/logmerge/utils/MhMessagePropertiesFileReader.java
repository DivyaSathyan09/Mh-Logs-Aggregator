package com.mhcure.logmerge.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MhMessagePropertiesFileReader {
    private static final String MESSAGE_PROPERTY_FILE_PREFIX = "messages";

    private static ResourceBundle moduleMessagesResourceBundle;

    static {
        try {
            moduleMessagesResourceBundle = ResourceBundle.getBundle(MESSAGE_PROPERTY_FILE_PREFIX);
        } catch (MissingResourceException missingResourceException) {
            missingResourceException.printStackTrace();
        }
    }

    public static String getMessage(String key, Object... arguments) {
        String message = null;
        if (moduleMessagesResourceBundle != null) {
            message = getMessage(moduleMessagesResourceBundle, key, arguments);
            if (!message.equals(key)) {
                return message;
            }
        }
        return key;
    }

    public static String getMessage(ResourceBundle resourceBundle, String key, Object... arguments) {
        if (resourceBundle.containsKey(key)) {
            String error = resourceBundle.getString(key);
            return MessageFormat.format(error, arguments);
        }
        // log.debug("Key not found in ResourceBundle. baseName={}", resourceBundle.getBaseBundleName());
        return key;
    }
}
