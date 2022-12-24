package com.pixplaze.plugin.reflected;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.pixplaze.plugin.PixplazeServerTweaks;
import com.pixplaze.plugin.reflected.exceptins.ProvidedClassException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.NoSuchElementException;

public class SkinChangeProvider {
    private static final String playerConnectionClassName = "net.minecraft.server.network.PlayerConnection";
    private static final String craftPlayerClassName = "org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer";
    private static final String packetPlayOutPlayerInfoClassName = "net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo";

    private static PixplazeServerTweaks plugin;

    public static void changeSkin(Player player, String base64textures) {
        plugin = PixplazeServerTweaks.getInstance().orElseThrow(ProvidedClassException::new);
        var server = plugin.getServer();

        var craftPlayerClass = tryCreateClass(craftPlayerClassName);
        var craftPlayerObject = craftPlayerClass.cast(player);
        var playerHandleObject = tryInvokeMethod(craftPlayerObject, "getHandle");
        var playerProfileObject = tryInvokeMethod(craftPlayerObject, "getProfile");

        server.sendMessage(Component.text("Server version: " + server.getVersion()).color(TextColor.color(Color.YELLOW.asRGB())));
        server.sendMessage(Component.text("Bukkit version: " + server.getBukkitVersion()).color(TextColor.color(Color.YELLOW.asRGB())));
        server.sendMessage(Component.text("Minecraft version: " + server.getMinecraftVersion()).color(TextColor.color(Color.YELLOW.asRGB())));
        server.sendMessage(Component.text(craftPlayerObject.getClass().getName()).color(TextColor.color(Color.YELLOW.asRGB())));
        server.sendMessage(Component.text(playerHandleObject.getClass().getName()).color(TextColor.color(Color.YELLOW.asRGB())));

        var playerConnectionObject = tryGetFieldValue(playerHandleObject, "playerConnection");
        var packetPlayOutPlayerInfoClass = tryCreateClass(packetPlayOutPlayerInfoClassName);
        var enumPlayerInfoActionClass = tryCreateClass(
                String.join("$", packetPlayOutPlayerInfoClassName, "EnumPlayerInfoAction"));
        var enumPlayerInfoActionRemovePlayer = tryGetEnumValueObject(enumPlayerInfoActionClass, "REMOVE_PLAYER");
        var enumPlayerInfoActionAddPlayer = tryGetEnumValueObject(enumPlayerInfoActionClass, "ADD_PLAYER");

        var gameProfile = (GameProfile) playerProfileObject;

        gameProfile.getProperties().removeAll("textures");
        gameProfile.getProperties().put("textures", new Property("textures", base64textures));

        tryInvokeMethod(playerConnectionObject, "sendPacket",
                tryCreateObject(packetPlayOutPlayerInfoClass, enumPlayerInfoActionRemovePlayer, playerHandleObject));

        tryInvokeMethod(playerConnectionObject, "sendPacket",
                tryCreateObject(packetPlayOutPlayerInfoClass, enumPlayerInfoActionAddPlayer, playerHandleObject));
//        var gameProfileObject = playerConnectionObject.;

    }

    public static Class<?> tryCreateClass(String className) {
        try {
            return Class.forName(craftPlayerClassName);
        } catch (ClassNotFoundException e) {
            throw new ProvidedClassException("Can not load %s class-provider. The reason could be an API change."
                    .formatted(className));
        }
    }

    public static Object tryCreateObject(Class<?> cls, Object ... arguments) {
        var argumentsTypes = new Class<?>[arguments.length];
        for (var i = 0; i < arguments.length; i++) {
            argumentsTypes[i] = arguments[i].getClass();
        }

        var constructorDisplayName = Arrays.stream(argumentsTypes)
                .map(Class::getTypeName)
                .reduce((curr, next) -> curr + "," + next)
                .map(args -> "%s(%s)".formatted(cls.getSimpleName(), args))
                .orElse(cls.getSimpleName());

        try {
            return cls.getDeclaredConstructor(argumentsTypes).newInstance(arguments);
        } catch (InstantiationException e) {
            throw new ProvidedClassException("Can not create %s class by constructor %s."
                    .formatted(cls.getName(), constructorDisplayName));
        } catch (IllegalAccessException e) {
            throw new ProvidedClassException("Can not access not public constructor %s of provided class %s."
                    .formatted(constructorDisplayName, cls.getName()));
        } catch (InvocationTargetException e) {
            throw new ProvidedClassException("Exception while invoking constructor of provided class %s. Cause %s: %s"
                    .formatted(cls.getName(), e.getCause().getClass().getSimpleName(), e.getMessage()));
        } catch (NoSuchMethodException e) {
            throw new ProvidedClassException("No such constructor %s in provided class %s."
                    .formatted(constructorDisplayName, cls.getName()));
        }
    }

    public static Object tryInvokeMethod(Object object, String methodName, Object ... arguments) {
        var argumentsTypes = new Class<?>[arguments.length];
        for (var i = 0; i < arguments.length; i++) {
            argumentsTypes[i] = arguments[i].getClass();
        }
        var methodDisplayName = Arrays.stream(argumentsTypes)
                .map(Class::getTypeName)
                .reduce((curr, next) -> curr + "," + next)
                .map(args -> "%s(%s)".formatted(methodName, args))
                .orElse(methodName);

        try {
            return object.getClass().getDeclaredMethod(methodName, argumentsTypes).invoke(object, arguments);
        } catch (IllegalAccessException e) {
            throw new ProvidedClassException("Can not access not public method %s of provided class %s."
                    .formatted(methodDisplayName, object.getClass().getName()));
        } catch (InvocationTargetException e) {
            throw new ProvidedClassException("Exception while invoking provided method %s. Cause %s: %s"
                    .formatted(methodDisplayName, e.getCause().getClass().getSimpleName(), e.getCause().getMessage()));
        } catch (NoSuchMethodException e) {
            throw new ProvidedClassException("No method %s in provided class %s."
                    .formatted(methodDisplayName, object.getClass().getName()));
        }
    }

    public static Object tryGetFieldValue(Object object, String fieldName) {
        try {
            return object.getClass().getField(fieldName).get(object);
        } catch (IllegalAccessException e) {
            throw new ProvidedClassException("Can not access not public field %s of provided class %s."
                    .formatted(fieldName, object.getClass().getName()));
        } catch (NoSuchFieldException e) {
            throw new ProvidedClassException("No field %s in provided class %s."
                    .formatted(fieldName, object.getClass().getName()));
        }
    }

    public static Object tryGetEnumValueObject(Class<?> enumClass, String name) {
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(item -> {
                    try {
                        return enumClass.getMethod("name").invoke(item).toString().equals(name);
                    } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                })
                .findFirst()
                .orElseThrow(() -> new ProvidedClassException("Can not get enum value object %s from provided class %s"
                        .formatted(name, enumClass.getName())));
    }
}
