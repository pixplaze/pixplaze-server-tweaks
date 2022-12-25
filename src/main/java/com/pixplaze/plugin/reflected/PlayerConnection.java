package com.pixplaze.plugin.reflected;

public class PlayerConnection implements ApiProvider {
    @Override
    public String getProvidedClassPackage() {
        return "net.minecraft.server.network";
    }

    @Override
    public String getProvidedClassName() {
        return "PlayerConnection";
    }

    
}
