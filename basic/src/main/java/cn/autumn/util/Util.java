package cn.autumn.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

/**
 * @author cf
 * Created in 9:32 2022/8/24
 */
public class Util {

    private static final Logger log = LoggerFactory.getLogger(Util.class);

    private static final Gson GSON = new Gson();

    public static Logger log() {
        return log;
    }

    public static Gson gson() { return GSON; }

    public static JsonObject strToJsonObj(String jsStr) {
        return JsonParser.parseString(jsStr).getAsJsonObject();
    }

    public static SimpleDateFormat format(String format) {
        return new SimpleDateFormat(format);
    }

    public static SimpleDateFormat formatDateTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public static SimpleDateFormat formatDate() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    public static SimpleDateFormat formatTime() {
        return new SimpleDateFormat("HH:mm:ss");
    }

    public static SimpleDateFormat formatHour() {
        return new SimpleDateFormat("HH");
    }
}
