package me.gleeming.tracker.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeUtility {
    /**
     * Get duration from string
     *
     * @param toParse String to parse
     * @return Duration
     */
    public static long parseDuration(String toParse) {
        try {
            toParse = toParse.toUpperCase();
            long value = Long.parseLong(toParse.substring(0, toParse.length() - 1));

            if(toParse.endsWith("S")) value = value * 1000;
            else if(toParse.endsWith("M")) value = value * 1000 * 60;
            else if(toParse.endsWith("H")) value = value * 1000 * 60 * 60;
            else if(toParse.endsWith("D")) value = value * 1000 * 60 * 60 * 12;
            else return -1;

            return value;
        } catch(Exception ignored) { return -1; }
    }

    /**
     * Returns a long time frame
     * Example: 1 hour 25 minutes and 26 seconds
     *
     * @param seconds Amount of seconds to format
     * @return Formatted String
     */
    public static String toLongFrame(long seconds) {
        List<String> valuesInOrder = new ArrayList<>();
        HashMap<String, Long> values = new HashMap<>();

        long day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - TimeUnit.DAYS.toHours(day);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.DAYS.toMinutes(day) - TimeUnit.HOURS.toMinutes(hours);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.DAYS.toSeconds(day) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minute);

        if(day > 0) {
            values.put(" days", day);
            valuesInOrder.add(" days");
        }

        if(hours > 0) {
            values.put(" hours", hours);
            valuesInOrder.add(" hours");
        }

        if(minute > 0) {
            values.put(" minutes", minute);
            valuesInOrder.add(" minutes");
        }

        if(second > 0) {
            values.put(" seconds", second);
            valuesInOrder.add(" seconds");
        }

        if(valuesInOrder.size() == 1) return values.get(valuesInOrder.get(0)) + valuesInOrder.get(0);

        Iterator<String> valueIterator = valuesInOrder.iterator();
        StringBuilder stringBuilder = new StringBuilder();
        while(valueIterator.hasNext()) {
            String value = valueIterator.next();
            if(valueIterator.hasNext()) stringBuilder.append(values.get(value)).append(value).append(" ");
            else stringBuilder.append("and ").append(values.get(value)).append(value);
        }

        if(stringBuilder.toString().equals("")) return "Now";
        return stringBuilder.toString();
    }

}
