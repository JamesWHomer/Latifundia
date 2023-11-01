package net.uber.latifundia;

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

}
