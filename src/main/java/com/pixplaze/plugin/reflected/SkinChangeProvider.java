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
import java.util.Collection;
import java.util.List;

public class SkinChangeProvider {
    private static final String playerConnectionClassName = "net.minecraft.server.network.PlayerConnection";
    private static final String craftPlayerClassName = "org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer";
    private static final String packetPlayOutPlayerInfoClassName = "net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo";

    private static PixplazeServerTweaks plugin;

    public static void changeSkin(Player player, String base64textures) {
        plugin = PixplazeServerTweaks.getInstance().orElseThrow(ProvidedClassException::new);
        var server = plugin.getServer();

        var craftPlayerClass = tryCreateClass(craftPlayerClassName);
        var playerConnectionClass = tryCreateClass(playerConnectionClassName);
        var craftPlayerObject = craftPlayerClass.cast(player);
        var playerHandleObject = tryInvokeMethod(craftPlayerObject, "getHandle");
        var playerProfileObject = tryInvokeMethod(craftPlayerObject, "getProfile");

        var playerConnectionObject = tryGetFieldValueByFieldType(playerHandleObject, playerConnectionClass);

        var packetPlayOutPlayerInfoClass = tryCreateClass(packetPlayOutPlayerInfoClassName);
        var enumPlayerInfoActionClass = tryCreateClass(
                String.join("$", packetPlayOutPlayerInfoClassName, "EnumPlayerInfoAction"));

        var enumPlayerInfoActionRemovePlayer = tryGetEnumValueObject(enumPlayerInfoActionClass, "REMOVE_PLAYER");
        var enumPlayerInfoActionAddPlayer = tryGetEnumValueObject(enumPlayerInfoActionClass, "ADD_PLAYER");

        var types = new Class<?>[] {enumPlayerInfoActionClass, Collection.class};
        var valuesRemove = new Object[] {enumPlayerInfoActionRemovePlayer, List.of(playerHandleObject)};
        var valuesAdd = new Object[] {enumPlayerInfoActionAddPlayer, List.of(playerHandleObject)};

        var packetRemoveObject = tryCreateObjectByStrongTypes0(packetPlayOutPlayerInfoClass,
                                                                     types,
                                                                     valuesRemove);
        var packetAddObject = tryCreateObjectByStrongTypes0(packetPlayOutPlayerInfoClass,
                                                                  types,
                                                                  valuesAdd);

        tryInvokeMethod(playerConnectionObject, "sendPacket", packetRemoveObject);
        var gameProfile = (GameProfile) playerProfileObject;
        gameProfile.getProperties().removeAll("textures");
        gameProfile.getProperties().put("textures", new Property("textures", base64textures));
        tryInvokeMethod(playerConnectionObject, "sendPacket", packetAddObject);
    }

    public static Class<?> tryCreateClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ProvidedClassException("Can not load %s class-provider. The reason could be an API change"
                    .formatted(className));
        }
    }

    /**
     * Attempts to create an instance of the class with the specified arguments, with a strong type match.
     * If a child is passed to the argument whose type does not strictly match the type of the target argument,
     * an exception will be thrown.
     * @param type type (class) which instance to create;
     * @param arguments class constructor types of arguments and arguments (1/2);
     * @return an instance of class;
     */
    public static Object tryCreateObject(Class<?> type, Object ... arguments) {
        var argumentTypes = new Class<?>[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            argumentTypes[i] = (Class<?>) arguments[i];
        }
        return tryCreateObjectByStrongTypes0(type, argumentTypes, arguments);
    }

    public static Object tryCreateObjectByStrongTypes(Class<?> type, Object ... arguments) {
        var countOfArguments = arguments.length / 2;
        var argumentTypes = new Class<?>[countOfArguments];
        var argumentObjects = new Object[countOfArguments];

        for (int i = 0, j = countOfArguments; i < countOfArguments; i++, j++) {
            argumentTypes[i] = (Class<?>) arguments[i];
            argumentObjects[i] = arguments[j];
            plugin.getServer().sendMessage(Component.text(argumentTypes[i].getName()));
            plugin.getServer().sendMessage(Component.text(argumentObjects[i].getClass().getName()));
        }

        return tryCreateObjectByStrongTypes0(type, argumentTypes, argumentObjects);
    }

    private static Object tryCreateObjectByStrongTypes0(Class<?> type, Class<?>[] argumentTypes, Object[] arguments) {
        var constructorDisplayName = toStringMethodArguments(type.getSimpleName(), argumentTypes);

        try {
            Arrays.stream(type.getConstructors()).forEach(constructor -> {
                plugin.getServer().sendMessage(
                        Component.text(toStringMethodArguments(type.getSimpleName(), constructor.getParameterTypes()))
                                .color(TextColor.color(Color.TEAL.asRGB())));
            });
            return type.getDeclaredConstructor(argumentTypes).newInstance(arguments);
        } catch (InstantiationException e) {
            throw new ProvidedClassException("Can not create %s class by constructor %s"
                    .formatted(type.getName(), constructorDisplayName));
        } catch (IllegalAccessException e) {
            throw new ProvidedClassException("Can not access not public constructor %s of provided class %s"
                    .formatted(constructorDisplayName, type.getName()));
        } catch (InvocationTargetException e) {
            throw new ProvidedClassException("Exception while invoking constructor of provided class %s. Cause %s: %s"
                    .formatted(type.getName(), e.getCause().getClass().getSimpleName(), e.getMessage()));
        } catch (NoSuchMethodException e) {
            throw new ProvidedClassException("No such constructor %s in provided class %s"
                    .formatted(constructorDisplayName, type.getName()));
        }
    }

    public static Object tryInvokeMethod(Object object, String methodName, Object ... arguments) {
        var argumentsTypes = new Class<?>[arguments.length];
        var server = plugin.getServer();
        for (var i = 0; i < arguments.length; i++) {
            argumentsTypes[i] = arguments[i].getClass();
            server.sendMessage(Component.text(argumentsTypes[i].getName())
                    .color(TextColor.color(Color.PURPLE.asRGB())));
        }
        var methodDisplayName = toStringMethodArguments(methodName, argumentsTypes);

        try {
            return object.getClass().getDeclaredMethod(methodName, argumentsTypes).invoke(object, arguments);
        } catch (IllegalAccessException e) {
            throw new ProvidedClassException("Can not access not public method %s of provided class %s"
                    .formatted(methodDisplayName, object.getClass().getName()));
        } catch (InvocationTargetException e) {
            throw new ProvidedClassException("Exception while invoking provided method %s. Cause %s: %s"
                    .formatted(methodDisplayName, e.getCause().getClass().getSimpleName(), e.getCause().getMessage()));
        } catch (NoSuchMethodException e) {
            throw new ProvidedClassException("No method %s in provided class %s"
                    .formatted(methodDisplayName, object.getClass().getName()));
        }
    }

    public static Object tryGetFieldValue(Object object, String fieldName) {
        try {
            return object.getClass().getField(fieldName).get(object);
        } catch (IllegalAccessException e) {
            throw new ProvidedClassException("Can not access not public field %s of provided class %s"
                    .formatted(fieldName, object.getClass().getName()));
        } catch (NoSuchFieldException e) {
            throw new ProvidedClassException("No field %s in provided class %s"
                    .formatted(fieldName, object.getClass().getName()));
        }
    }

    public static Object tryGetFieldValueByFieldType(Object object, Class<?> type) {
        var fields = object.getClass().getFields();

        try {
            for (var field : fields) {
                if (type.equals(field.getType())) {
                    return field.get(object);
                }
            }
        } catch (Exception e) {
            throw new ProvidedClassException("No field with type %s in provided class %s"
                    .formatted(type.getName(), object.getClass().getName()));
        }

        throw new ProvidedClassException("No field with type %s in provided class %s"
                .formatted(type.getName(), object.getClass().getName()));
    }

    public static Object tryGetEnumValueObject(Class<?> enumClass, String name) {
        var server = plugin.getServer();
        return Arrays.stream(enumClass.getEnumConstants())
                .filter(item -> {
                    try {
                        return enumClass.getMethod("name").invoke(item).toString().equals(name);
                    } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                })
                .findFirst()
//                .map(enumType -> {
//                    try {
//                        return enumType.getClass().cast(enumClass.getConstructor().newInstance());
//                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
//                             InstantiationException e) {
//                        throw new RuntimeException(e);
//                    }
//                })
                .orElseThrow(() -> new ProvidedClassException("Can not get enum value object %s from provided class %s"
                        .formatted(name, enumClass.getName())));
    }

    private static String toStringMethodArguments(String methodName, Class<?>[] argumentTypes) {
        return toStringMethodArguments(methodName, argumentTypes, false);
    }

    private static String toStringMethodArguments(String methodName, Class<?>[] argumentTypes, boolean verbose) {
        return Arrays.stream(argumentTypes)
                .map(classType -> {
                    if (verbose) {
                        return classType.getTypeName();
                    } else {
                        var classTypeName = classType.getTypeName();
                        var splitClassTypeName = classTypeName.split("\\.");
                        return splitClassTypeName[splitClassTypeName.length - 1];
                    }
                })
                .reduce((curr, next) -> curr + ", " + next)
                .map(args -> "%s(%s)".formatted(methodName, args))
                .orElse("%s()".formatted(methodName));
    }
}
