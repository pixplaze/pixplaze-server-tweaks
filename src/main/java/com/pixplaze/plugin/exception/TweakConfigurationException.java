package com.pixplaze.plugin.exception;

public class TweakConfigurationException extends RuntimeException {

    public TweakConfigurationException() {
        this(TweakConfigurationException.getMessageForConfig("tweaks"));
    }

    public TweakConfigurationException(String message) {
        super(message);
    }

    public static String getMessageForConfig(String configurationSectionName) {
        return "Section \"%s\" in config.yml is not defined!".formatted(configurationSectionName);
    }
}
