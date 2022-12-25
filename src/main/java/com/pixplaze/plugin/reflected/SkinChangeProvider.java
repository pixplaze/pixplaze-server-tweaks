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

import static com.pixplaze.plugin.reflected.ReflectionProvider.*;

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
}
