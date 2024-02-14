package net.uber.latifundia;

import org.bukkit.ChatColor;

import java.awt.*;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class GeneralUtils {

    public static void removeEntries(Map<UUID, Point> map, Point value) {
        Iterator<Map.Entry<UUID, Point>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Point> entry = iterator.next();
            if (entry.getValue().equals(value)) {
                iterator.remove();
            }
        }
    }

    public static String colour(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}
