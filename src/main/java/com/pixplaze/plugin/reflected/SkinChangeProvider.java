package com.pixplaze.plugin.reflected;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.pixplaze.plugin.PixplazeServerTweaks;
import com.pixplaze.plugin.reflected.exceptins.ProvidedClassException;
import com.pixplaze.plugin.reflected.provider.ReflectedClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

import static com.pixplaze.plugin.reflected.ReflectionUtils.*;
import static com.pixplaze.plugin.reflected.StringUtils.toStringTypes;

public class SkinChangeProvider {
    private static final String playerConnectionClassName = "net.minecraft.server.network.PlayerConnection";
    private static final String craftPlayerClassName = "org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer";
    private static final String packetClassName = "net.minecraft.network.protocol.Packet";
    private static final String packetPlayOutPlayerInfoClassName = "net.minecraft.network.protocol.game.PacketPlayOutPlayerInfo";

    private static PixplazeServerTweaks plugin;

    public static void changeSkin(Player player, String base64textures) {
        plugin = PixplazeServerTweaks.getInstance().orElseThrow(ProvidedClassException::new);
        var server = plugin.getServer();

        var craftPlayerClass = tryCreateClass(craftPlayerClassName);
        var playerConnectionClass = tryCreateClass(playerConnectionClassName);
        var craftPlayerObject = craftPlayerClass.cast(player);
        var playerHandleObject = tryInvokeMethodByName(craftPlayerObject, "getHandle");
        var playerProfileObject = tryInvokeMethodByName(craftPlayerObject, "getProfile");

        var gameProfile = (GameProfile) playerProfileObject;
        gameProfile.getProperties().removeAll("textures");
        gameProfile.getProperties().put("textures", new Property("textures", base64textures));

        var playerConnectionObject = tryGetFieldValueByFieldType(playerHandleObject, playerConnectionClass);

        var packetClass = tryCreateClass(packetClassName);
        var packetPlayOutPlayerInfoClass = tryCreateClass(packetPlayOutPlayerInfoClassName);
        var enumPlayerInfoActionClass = tryCreateClass(
                String.join("$", packetPlayOutPlayerInfoClassName, "EnumPlayerInfoAction"));

        var enumPlayerInfoActionRemovePlayer = tryGetEnumValueObject(enumPlayerInfoActionClass, "REMOVE_PLAYER");
        var enumPlayerInfoActionAddPlayer = tryGetEnumValueObject(enumPlayerInfoActionClass, "ADD_PLAYER");

        var valuesRemove = new Object[]{};
        var valuesAdd = new Object[]{};

//        ReflectedClass.of("classPath")
//                .constructor(type0, type1, type2)
//                .create(arg0, arg1, arg2)
//                .or()
//                .constructor(type2, type1, type0)
//                .create(arg2, arg1, arg0)
//                .call(arg3, arg4)
//
//        ReflectedClass.of("classPath")
//                .constructor(type0, type1, type2)
//                .create(arg0, arg1, arg2)
//                .call("methodName")

        var packetRemoveObject = tryCreateObjectByStrongTypes(packetPlayOutPlayerInfoClass,
                enumPlayerInfoActionClass,
                Collection.class,
                enumPlayerInfoActionRemovePlayer,
                List.of(playerHandleObject));

        var packetAddObject = tryCreateObjectByStrongTypes(packetPlayOutPlayerInfoClass,
                enumPlayerInfoActionClass,
                Collection.class,
                enumPlayerInfoActionAddPlayer,
                List.of(playerHandleObject));

        server.sendMessage(Component.text(toStringTypes(
                        enumPlayerInfoActionClass,
                        Collection.class,
                        enumPlayerInfoActionRemovePlayer,
                        List.of(playerHandleObject)))
                .color(TextColor.color(Color.TEAL.asRGB())));

        server.sendMessage(Component.text(toStringTypes(
                        enumPlayerInfoActionClass,
                        Collection.class,
                        enumPlayerInfoActionAddPlayer,
                        List.of(playerHandleObject)))
                .color(TextColor.color(Color.TEAL.asRGB())));

        tryInvokeMethodByArguments(playerConnectionObject,
                                   new Class[]{packetClass},
                                   new Object[]{packetRemoveObject});

        tryInvokeMethodByArguments(playerConnectionObject,
                new Class[]{packetClass},
                new Object[]{packetAddObject});
    }
}
