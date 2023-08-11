package tk.xhuoffice.sessbilinfo;

import java.text.NumberFormat;
import java.time.Duration;
import java.util.Locale;

public class NumFormat {
    
    public static String time(int seconds) {
        Duration duration = Duration.ofSeconds(seconds);
        String formattedTime;
        if (duration.toMinutes() >= 60) {
            formattedTime = String.format("%02d:%02d:%02d", duration.toHours(), duration.toMinutes() % 60, duration.getSeconds() % 60);
        } else {
            formattedTime = String.format("%d:%02d", duration.toMinutes(), duration.getSeconds() % 60);
        }
        return formattedTime;
    }
    
    public static String num(int num) {
        return NumberFormat.getInstance(Locale.US).format(num);
    }
    
}
