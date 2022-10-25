package cn.autumn.wxserver.server;

import cn.autumn.entity.business.wxserver.TemplateMsg;
import cn.autumn.entity.mysql.WxUser;
import cn.autumn.enumeration.wxserver.UrlAddress;
import cn.autumn.util.Calc;
import cn.autumn.util.DateUtil;
import cn.autumn.util.NumberUtil;
import cn.autumn.util.Util;
import cn.autumn.wxserver.config.WeChatConfig;
import cn.autumn.wxserver.mapper.CityCodeMapper;
import cn.autumn.wxserver.mapper.WxUserMapper;
import cn.autumn.wxserver.service.interfaces.TaskService;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cf
 * Created in 9:25 2022/8/24
 */
@Component
public class WechatMessageServe implements TaskService {

    @Value("${baby.parameter.acquaintance}")
    private String acquaintance;

    @Value("${baby.parameter.formal_connect}")
    private String formalConnect;

    @Value("${baby.parameter.first_connect}")
    private String firstConnect;

    @Value("${wechat.msgMo}")
    private String msgMo;
    @Value("${wechat.msgAf}")
    private String msgAf;
    @Value("${wechat.msgEv}")
    private String msgEv;

    @Resource
    private WeChatConfig weChatConfig;

    @Resource
    private WxUserMapper wxUserMapper;

    @Resource
    private CityCodeMapper cityCodeMapper;

    public String getAccessToken() {
        String acsTkUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&";
        String reqUrl = acsTkUrl + "appid=" + weChatConfig.getAppId() + "&secret=" + weChatConfig.getAppKey();
        String resp = HttpUtil.get(reqUrl);
        return Util.strToJsonObj(resp).get("access_token").getAsString();
    }

    public JsonObject getUserList(String acsTk) {
        String reqUrl = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=" + acsTk + "&next_openid=";
        String resp = HttpUtil.get(reqUrl);
        return Util.strToJsonObj(resp);
    }

    public void sendMsg(TemplateMsg msgVo, String token, String userId, int flag) {

        String reqUrl = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="  + token;

        Map<String, Object> content = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> jsonObj = new HashMap<>();

        jsonObj.put("value",msgVo.getTitle());
        jsonObj.put("color","#173177");
        data.put("first",jsonObj);

        jsonObj = new HashMap<>(2);
        jsonObj.put("value",msgVo.getWord1());
        jsonObj.put("color","#FFA500");
        data.put("word1",jsonObj);
        jsonObj = new HashMap<>(2);
        jsonObj.put("value",msgVo.getWord2());
        jsonObj.put("color","#87CEFA");
        data.put("word2",jsonObj);
        jsonObj = new HashMap<>(2);
        jsonObj.put("value",msgVo.getWord3());
        jsonObj.put("color","#00FF7F");
        data.put("word3",jsonObj);
        jsonObj = new JSONObject();
        jsonObj.put("value",msgVo.getWord4());
        jsonObj.put("color","#D3D3D3");
        data.put("word4",jsonObj);
        jsonObj = new JSONObject();
        jsonObj.put("value",msgVo.getWord5());
        jsonObj.put("color","#FFA500");
        data.put("word5",jsonObj);
        jsonObj = new JSONObject();
        jsonObj.put("value",msgVo.getWord6());
        jsonObj.put("color","#87CEFA");
        data.put("word6",jsonObj);
        jsonObj = new JSONObject();
        jsonObj.put("value",msgVo.getWord7());
        jsonObj.put("color","#00FF7F");
        data.put("word7",jsonObj);
        jsonObj = new JSONObject();
        jsonObj.put("value",msgVo.getWord8());
        jsonObj.put("color","#D3D3D3");
        data.put("word8",jsonObj);
        jsonObj = new JSONObject();
        jsonObj.put("value",msgVo.getRemark());
        jsonObj.put("color","#FF6347");
        data.put("remark",jsonObj);
        jsonObj = new JSONObject();
        jsonObj.put("value",msgVo.getAcquaintance());
        jsonObj.put("color","#FF6347");
        data.put("acquaintance",jsonObj);
        jsonObj = new JSONObject();
        jsonObj.put("value",msgVo.getFirstConnect());
        jsonObj.put("color","#FF6347");
        data.put("firstConnect",jsonObj);
        jsonObj = new JSONObject();
        jsonObj.put("value",msgVo.getFormalConnect());
        jsonObj.put("color","#FF6347");
        data.put("formalConnect",jsonObj);
        jsonObj = new JSONObject();
        jsonObj.put("value",msgVo.getBirth());
        jsonObj.put("color","#FF6347");
        data.put("birth",jsonObj);
        jsonObj = new JSONObject();
        jsonObj.put("value",msgVo.getInfo());
        jsonObj.put("color","#7CFC00");
        data.put("info",jsonObj);
        jsonObj = new JSONObject();
        jsonObj.put("value",msgVo.getFlag());
        jsonObj.put("color","#FF69B4");
        data.put("flag",jsonObj);

        content.put("touser",userId);
        content.put("url",msgVo.getJumpUrl());
        content.put("template_id", flag == 1 ? msgMo : msgAf);
        content.put("data", data);
        String resp = HttpUtil.post(reqUrl, JSONUtil.parseFromMap(content).toString());
        Util.strToJsonObj(resp);
    }

    public void createMsgAndSend(List<String> toUsers) throws ParseException {
        List<WxUser> wxUsers = wxUserMapper.queryWxUserByListToUser(toUsers);
        String token = getAccessToken();

        String now = Util.formatTime().format(System.currentTimeMillis());
        String dateClose = Util.format(DateUtil.DEFAULT_DATE_CLOSE_FORMAT).format(System.currentTimeMillis());
        int wTimeIs = Integer.parseInt(Util.formatTime().format(new Date()).substring(0, 2));
        String greeting = currentTimeSayHi(wTimeIs);

        for (WxUser wxUser : wxUsers) {

            TemplateMsg message = new TemplateMsg();

            message.setTitle("当前: " + now + "   " + greeting);

            message.setRemark(Calc.calculate(wxUser.getBirthday()) + "");
            message.setBirth(Calc.getBirthDay(wxUser.getBirthday()) + "");

            String flag = "";

            if ("oHfKZ6Ih47hfHAJCLHgunxa8XJpU".equals(wxUser.getToUser())) {

                String weatherUrl = "https://api.ip138.com/weather/?code=";
                String weatherResp = HttpUtil.get(weatherUrl + wxUser.getCityCode() + "&type=1&token=9e7390e4c54435ad3feb41ada85262a9");

                JSONObject weatherData = JSONObject.parseObject(JSONObject.parseObject(weatherResp).getString("data"));

                String weather = weatherData.getString("dayWeather");
                String night = weatherData.getString("nightTemp");
                String day = weatherData.getString("dayTemp");
                message.setWord1(cityCodeMapper.queryCityName(wxUser.getCityCode()));
                message.setWord2(StringUtils.isEmpty(weather) ? "可恶!!没有查到天气!!" : weather);
                message.setWord3(StringUtils.isEmpty(day) ? "温度没查到~" : day + "℃");
                message.setWord4(StringUtils.isEmpty(night) ? "哇！我要哭了！！" : night + "℃");

                if (weather.contains("雨")){
                    flag = "今天有雨哟，记得带伞呀!";
                }
                if (!StringUtils.isEmpty(day) && Integer.parseInt(day) > 30){
                    flag = flag + "今日温度比较高，记得防晒，多喝点水哟。";
                }
                if (!StringUtils.isEmpty(night) && Integer.parseInt(night) < 10){
                    flag = flag + "夜间气温较低，注意保暖哦。";
                }

            }
            String holidaysResp = HttpUtil.get(UrlAddress.HOLIDAYS.getAddress() + dateClose);
            JSONObject holidayData = (JSONObject) JSONArray.parseArray(JSONObject.parseObject(JSONObject.parseObject(holidaysResp).getString("data")).getString("list")).get(0);

            message.setWord5(weekMessage(holidayData));

            message.setWord6(workHolidaysNotify(holidayData));

            changeDataCalc(message);

            message.setFlag("".equals(flag) ? "今日天气很好哦，今天的你离富婆又进一步了。" : flag);
            int calculate = Calc.calculate(wxUser.getBirthday());
            if (calculate % NumberUtil.HUNDRED == NumberUtil.ZERO || calculate % NumberUtil.FIFTY == NumberUtil.ZERO){
                message.setInfo(message.getInfo() + "。今天也是特别的日子呢，你已经来到这个世界" + calculate + "天了哟。");
            }
            if (Calc.getBirthDay(wxUser.getBirthday()) < NumberUtil.THIRTY){
                message.setInfo((!StringUtils.isEmpty(message.getInfo()) ? message.getInfo() + "。" : "")+ wxUser.getName() + "小仙女你的生日只有不到" + Calc.getBirthDay(wxUser.getBirthday()) + "天了哦");
            }
            sendMsg(message, token, wxUser.getToUser(), Calc.judgeNowTime());
            Util.log().info("Message send to: " + wxUser.getToUser());

        }

    }

    private void changeDataCalc(TemplateMsg message) {
        long aqt, foc, fic;
        try {
            aqt = DateUtil.dateToTimestamp(acquaintance);
            foc = DateUtil.dateTimeToTimestamp(formalConnect);
            fic = DateUtil.dateTimeToTimestamp(firstConnect);
        } catch (ParseException pe) {
            Util.log().warn("Argument and format error.");
            return;
        }
        StringBuilder mayStart = new StringBuilder();
        long currentTime = System.currentTimeMillis();
        long aqtDays = (currentTime - (aqt + NumberUtil.TWENTY_HOURS_MILLIS)) / NumberUtil.DAY_MILLIS;

        acquaintance(mayStart, aqtDays);

        message.setAcquaintance(mayStart.toString());

        mayStart = new StringBuilder();

        firstConnect(mayStart, fic);

        message.setFirstConnect(mayStart.toString());

        mayStart = new StringBuilder();

        formalConnect(mayStart, foc);

        message.setFormalConnect(mayStart.toString());


    }

    private static void acquaintance(StringBuilder mayStart, double aqtDays) {
        mayStart.append("Meet: ");
        assembleMessage(mayStart, aqtDays);
    }

    private static void firstConnect(StringBuilder mayStart, long currTime) {
        double fid = calcDayNumberRatio(currTime, 0);
        double fidMinute = calcNumberRatioToMinute(currTime, false);

        mayStart.append("The first: ");
        assembleMessage(mayStart, fid);
        mayStart.append((int) fidMinute / (NumberUtil.SIXTY * NumberUtil.SIXTY))
                .append(((int) fidMinute / (NumberUtil.SIXTY * NumberUtil.SIXTY)) == 1 ? " hour " : " hours ")
                .append((int) (fidMinute % (NumberUtil.SIXTY * NumberUtil.SIXTY)) / NumberUtil.SIXTY)
                .append(((int) (fidMinute % (NumberUtil.SIXTY * NumberUtil.SIXTY)) == 1 ? " minute " : " minutes "))
                .append((int) fidMinute % NumberUtil.SIXTY).append((int) fidMinute % NumberUtil.SIXTY == 1 ? " second " : " seconds ")
                .append(".");
    }

    private static void formalConnect(StringBuilder mayStart, long currTime) {
        double fod = calcDayNumberRatio(currTime, NumberUtil.ONE_HOURS_MILLIS * NumberUtil.THIRTEEN_DOT_FIVE);
        double fodMinute = calcNumberRatioToMinute(currTime, true);

        mayStart.append("Meet with: ");
        assembleMessage(mayStart, fod);
        mayStart.append((int) fodMinute / NumberUtil.SIXTY).append((int) fodMinute / NumberUtil.SIXTY == 1 ? " hour " : " hours ")
                .append((int) fodMinute % NumberUtil.SIXTY).append((int) fodMinute % NumberUtil.SIXTY == 1 ? " minute " : " minutes ")
                .append(".");
    }

    private static void assembleMessage(StringBuilder builder, double days) {
        Integer year = calcDayOfYear((int) days);
        Integer month = calcDayOfMonth((int) days);
        Integer day = calcDay((int) days);

        if (year != null) {
            builder.append(year).append(year == 1 ? " year " : " years ");
        }

        if (year == null && month != null) {
            builder.append(month).append(" months ");
        }
//        }else if (year != null && month != null) {
//            builder.append(month).append("个月");
//        }

        if (day != null) {
            builder.append(day).append(day == 1 ? " day " : " days ");
        }
    }

    private static Integer calcDayOfYear(int day) {

        if (day > NumberUtil.YEAR) {
            return day / NumberUtil.YEAR;
        }
        return null;
    }

    private static Integer calcDayOfMonth(int day) {
        int remainDay = day % NumberUtil.YEAR;
        if (remainDay > NumberUtil.THIRTY) {
            return remainDay / NumberUtil.THIRTY;
        }
        return null;
    }

    private static Integer calcDay(int day) {
        int i = (day % NumberUtil.YEAR) % NumberUtil.THIRTY;
        return i != NumberUtil.ZERO ? i : null;
    }

    private static double calcDayNumberRatio(long t, double rep) {

        long c = System.currentTimeMillis();

        return (c - (t + rep)) / NumberUtil.DAY_MILLIS;

    }

    public static long calcNumberRatioToMinute(long t, boolean minOrMil) {
        long c = System.currentTimeMillis();
        if (minOrMil) {
            return ((c - t) % NumberUtil.DAY_MILLIS) / (60 * 1000);
        }else {
            return ((c - t) % NumberUtil.DAY_MILLIS) / NumberUtil.THOUSAND;
        }
    }

    private String weekMessage(JSONObject holidayData) {
        String weekMsg;
        if ("工作日".equals(holidayData.getString("workday_cn"))) {
            weekMsg = holidayData.getString("week_cn") + "  今天也是要努力的一天呢！加油！！";
        } else {
            weekMsg = holidayData.getString("week_cn") + "  好耶！！可以睡大觉啦";
        }
        return weekMsg;
    }

    private String workHolidaysNotify(JSONObject holidayData) {
        String holidayMsg;
        if ("法定节假日".equals(holidayData.getString("holiday_legal_cn"))) {
            holidayMsg = holidayData.getString("holiday_cn") + "快乐呀！！";
        }else {
            if ("周末".equals(holidayData.getString("weekend_cn"))) {
                holidayMsg = "假期摆烂时间~诶嘿嘿";
            }else {
                if ("星期一".equals(holidayData.getString("week_cn"))) {
                    holidayMsg = "好耶！新的一周啦！！";
                } else if ("星期二".equals(holidayData.getString("week_cn"))) {
                    holidayMsg = "早晨起来，拥抱太阳，让身体充满，灿烂的阳光，满满的正能量！！";
                } else if ("星期三".equals(holidayData.getString("week_cn"))) {
                    holidayMsg = "看看我还有那些事要做，emmmm，怎会如此！v _ v";
                } else if ("星期四".equals(holidayData.getString("week_cn"))) {
                    holidayMsg = "今天星期四，明天星期五，再坚持一天，然后星期天！好耶！！";
                } else {
                    holidayMsg = "诶？我的心呢？哦，它飘到星期天那里去了！！";
                }
            }
        }
        return holidayMsg;
    }

    private String currentTimeSayHi(Integer wTimeIs) {
        String greeting = "";
        if (wTimeIs <= NumberUtil.NINE && wTimeIs != NumberUtil.ZERO) {
            greeting = "早上好，小飞棍来nio！";
        } else if (wTimeIs > NumberUtil.NINE && wTimeIs < NumberUtil.TWELVE ) {
            greeting = "上午好呀！！么么~";
        } else if (wTimeIs == NumberUtil.TWELVE) {
            greeting = "午好，多吃肉肉，嘿嘿";
        } else if (wTimeIs > NumberUtil.TWELVE && wTimeIs < NumberUtil.EIGHTEEN) {
            greeting = "下午好呀，想大哥~";
        } else if (wTimeIs >= NumberUtil.EIGHTEEN) {
            greeting = "晚上啦，可是晚上更想你了，_(:з」∠)_";
        } else {
            greeting = "早点睡呀，老公注意休息呀，害羞~";
        }
        return greeting;
    }

    public void pushMessage(String body) {
        String accessToken = getAccessToken();
        HttpUtil.post(WeChatConfig.MESSAGE_URL + accessToken, body);
    }


    @Override
    public String getClassName() {
        return "MsgService";
    }
}