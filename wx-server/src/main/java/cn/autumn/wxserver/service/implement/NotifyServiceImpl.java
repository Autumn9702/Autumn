package cn.autumn.wxserver.service.implement;

import cn.autumn.entity.business.ExeSeq;
import cn.autumn.entity.mysql.CityCode;
import cn.autumn.entity.mysql.WxUser;
import cn.autumn.enumeration.wxserver.Order;
import cn.autumn.model.LabelValueModel;
import cn.autumn.util.BeanUtil;
import cn.autumn.util.DateUtil;
import cn.autumn.util.Util;
import cn.autumn.wxserver.config.DynamicTask;
import cn.autumn.wxserver.config.exp.OrderException;
import cn.autumn.wxserver.mapper.CityCodeMapper;
import cn.autumn.wxserver.mapper.WxUserMapper;
import cn.autumn.wxserver.model.MessageModel;
import cn.autumn.wxserver.server.WechatMessageServe;
import cn.autumn.wxserver.service.interfaces.NotifyService;
import cn.autumn.wxserver.service.interfaces.TaskService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: autumn
 * created in 2022/9/7 Class MsgServiceImpl
 */
@Service
public class NotifyServiceImpl implements NotifyService, TaskService {

    @Resource
    private WechatMessageServe msgService;

    @Resource
    private WxUserMapper wxUserMapper;

    @Resource
    private CityCodeMapper cityCodeMapper;

    @Resource
    private DynamicTask dynamicTask;

    /* ===========================  Scheduled task  =========================== */
    @Scheduled(cron = "1 1 0 * * ?")
    public void everyDayInit() {
        long currentTime = System.currentTimeMillis();
        String today = Util.formatDate().format(new Date());
        List<WxUser> wxUsers = wxUserMapper.selectList(new QueryWrapper<>());
        List<LabelValueModel<Long, String>> values = new ArrayList<>();
        for (WxUser wxUser : wxUsers) {
            if (!wxUser.getNotifyTime().isEmpty()) {
                wxUser.getNotifyTime().forEach(time -> {
                    long exeTimestamp = 0;
                    try {
                        exeTimestamp = Util.formatDateTime().parse(today + " " + time).getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (currentTime < exeTimestamp) {
                        values.add(new LabelValueModel<>(exeTimestamp, wxUser.getToUser()));
                    }
                });
            }
        }

        List<ExeSeq<String>> exeSeqs = notifySort(values);
        if (CollectionUtils.isEmpty(exeSeqs)) {
            return;
        }
        dynamicTask.addQueue("NotifyServiceImpl", "regularNotify", exeSeqs);

    }

    public void regularNotify() throws ParseException {
        msgService = BeanUtil.getBean(msgService, WechatMessageServe.class);
        dynamicTask = BeanUtil.getBean(dynamicTask, DynamicTask.class);
        List<String> toUsers = DynamicTask.taskExecutorQueue.get("regularNotify").get(0).getValues();
        msgService.createMsgAndSend(toUsers);
        dynamicTask.nextTask("NotifyServiceImpl", "regularNotify");
    }

    @Override
    public void processOrder(MessageModel message) {
        Util.log().info("Process order: " + message.getContent());
        String result;
        if (message.getContent() == null) {
            return;
        }
        if (StringUtils.isEmpty(message.getContent())) {
            result = BeanUtil.generateTemplate();
        }else {
            result = storeJudge(message);
            if (StringUtils.isEmpty(result)) {
                result = BeanUtil.generateTemplate();
            }
        }
        String body = BeanUtil.beanToMsgBody(message.getFromUserName(), result);
        msgService.pushMessage(body);
    }


    /*  =============================  logic  =============================  */
    private String storeJudge(MessageModel message) {
        String odr;
        String result;
        try {
            odr = message.getContent().substring(0, message.getContent().indexOf("-"));
        } catch (StringIndexOutOfBoundsException idxEx) {
            odr = message.getContent();
        }
        String data = message.getContent().substring(message.getContent().indexOf("-") + 1);
        odr = orderValueOf(message.getFromUserName(), odr);
        String process = dataProcess(Order.valueOf(odr), message.getFromUserName(), data);
        if (!StringUtils.isEmpty(process)) {
            data = process;
        }
        switch (Order.valueOf(odr)) {
            case NAME:
                dbProcess(Order.NAME, message.getFromUserName(), data);
                result = "??????????????????";
                break;
            case BIRTHDAY:
                dbProcess(Order.BIRTHDAY, message.getFromUserName(), data);
                result = "??????????????????";
                break;
            case ADDRESS:
                result = dbProcess(Order.ADDRESS, message.getFromUserName(), data);
                break;
            case OPEN_NOTIFY:
                String opMsg = dbProcess(Order.OPEN_NOTIFY, message.getFromUserName(), data);
                result = StringUtils.isEmpty(opMsg) ? "???????????????: " + data : opMsg;
                break;
            case LOOK_USER_DATA:
                return dbProcess(Order.LOOK_USER_DATA, message.getFromUserName(), null);
            case LOOK_NOTIFY:
                return dbProcess(Order.LOOK_NOTIFY, message.getFromUserName(), null);
            case CLOSE_NOTIFY:
                String rstMsg = dbProcess(Order.CLOSE_NOTIFY, message.getFromUserName(), data);
                result = StringUtils.isEmpty(rstMsg) ? " ???????????????: " + data : rstMsg;
                break;
            case CLOSE_ALL_NOTIFY:
                dbProcess(Order.CLOSE_ALL_NOTIFY, message.getFromUserName(), null);
                result = "???" + Order.CLOSE_ALL_NOTIFY.getName();
                break;
            default:
                result = "????????????????????????";
        }
        return result;
    }

    private String dataProcess(Order order, String toUser, String data) {
        switch (order) {
            case NAME:
                if (data.length() > 8) {
                    throw new OrderException(toUser, "????????????");
                }
                break;
            case BIRTHDAY:
                try {
                    Util.formatDate().parse(data);
                } catch (ParseException pe) {
                    throw new OrderException(toUser, "??????????????????????????????");
                }
                break;
            case ADDRESS:
            case CLOSE_ALL_NOTIFY:
            case LOOK_USER_DATA:
            case LOOK_NOTIFY:
                break;
            default:
                return notifyFormatJudge(toUser, data);
        }
        return null;
    }

    private String dbProcess(Order order, String toUser, String data) {
        WxUser wxUser = wxUserMapper.queryWxUserByUser(toUser);
        boolean wxUserExist = true;
        if (Objects.isNull(wxUser)) {
            wxUserExist = false;
            wxUser = new WxUser();
            wxUser.setToUser(toUser);
            wxUser.setNotifyTime(new ArrayList<>());
            wxUser.setCreateTime(System.currentTimeMillis());
        }
        switch (order) {
            case NAME:
                wxUser.setName(data);
                break;
            case BIRTHDAY:
                wxUser.setBirthday(data);
                break;
            case ADDRESS:
                return processAddress(data, wxUser);
            case OPEN_NOTIFY:
                if (StringUtils.isEmpty(wxUser.getName()) || StringUtils.isEmpty(wxUser.getBirthday()) ||
                StringUtils.isEmpty(wxUser.getCityCode())) {
                    return "??????????????????/??????/????????????";
                }
                if (!wxUser.getNotifyTime().contains(data)){
                    wxUser.getNotifyTime().add(data);
                    dynamicTask.addQueue("NotifyServiceImpl", "regularNotify", new ExeSeq<>(DateUtil.formatTime(data), toUser));
                }
                break;
            case LOOK_USER_DATA:
                String cityName = cityCodeMapper.queryCityName(wxUser.getCityCode());
                return BeanUtil.beanToUserData(wxUser, cityName);
            case LOOK_NOTIFY:
                StringBuffer notify = new StringBuffer();
                notify.append(wxUser.getNotifyTime().isEmpty() ? "???????????????" : "??????????????????");
                wxUser.getNotifyTime().forEach(ny -> notify.append("\n").append(ny));
                return notify.toString();
            case CLOSE_NOTIFY:
                if (!wxUser.getNotifyTime().contains(data)) {
                    return "??????????????????, ????????????";
                }else {
                    wxUser.getNotifyTime().remove(data);
                    dynamicTask.deleteTaskToQueue("NotifyServiceImpl", "regularNotify", new ExeSeq<>(DateUtil.formatTime(data), toUser));
                }
                break;
            case CLOSE_ALL_NOTIFY:
                wxUser.getNotifyTime().clear();
                break;
        }
        if (wxUserExist) {
            wxUserMapper.updateById(wxUser);
        }else {
            wxUserMapper.insert(wxUser);
        }
        return null;
    }

    private String notifyFormatJudge(String toUser, String data) {
        if (data.equals(Order.MORNING.getName())) {
            return "09:00:00";
        }else if (data.equals(Order.NOON.getName())) {
            return "12:00:00";
        }else if (data.equals(Order.AFTERNOON.getName())) {
            return "18:00:00";
        }
        try {
            Util.formatTime().parse(data);
        } catch (ParseException pe){
            WxUser wxUser = wxUserMapper.selectOne(new LambdaQueryWrapper<WxUser>().eq(WxUser::getToUser, toUser));
            Util.log().warn(wxUser.getName() + ":????????? ??????????????????");
            return null;
        }
        return null;
    }

    private String processAddress(String data, WxUser wxUser) {
        StringBuilder temp = new StringBuilder();
        CityCode cityBean = cityCodeMapper.selectOne(new LambdaQueryWrapper<CityCode>().eq(CityCode::getCityName, data));

        if (Objects.isNull(cityBean)) {
            temp.append("???????????????????????????????????????\n");
            String prefix = data.substring(0, 2);
            List<CityCode> matchCity = cityCodeMapper.queryMatchCityName(prefix);
            if (matchCity.isEmpty()) {
                temp.append("??????????????????: chenfan9702\n").append("???????????????????????????");
            }else {
                temp.append("??????????????????????????????:");
                matchCity.forEach(city -> temp.append("\n  ").append(city));
                temp.append("\n?????????????????????????????????????????????????????????????????????");
            }
            return temp.toString();
        }
        Integer cityCode = Integer.parseInt(cityBean.getCityCode().toString());
        wxUser.setCityCode(cityCode);
        temp.append("??????????????????");
        if (StringUtils.isEmpty(wxUser.getId())) {
            wxUserMapper.insert(wxUser);
        } else {
            wxUserMapper.updateById(wxUser);
        }
        return temp.toString();
    }

    private String orderValueOf(String toUser, String order){
        switch (order) {
            case "??????":
                return "NAME";
            case "??????":
                return "BIRTHDAY";
            case "??????":
                return "ADDRESS";
            case "????????????":
                return "OPEN_NOTIFY";
            case "????????????":
                return "LOOK_NOTIFY";
            case "??????????????????":
                return "LOOK_USER_DATA";
            case "????????????":
                return "CLOSE_NOTIFY";
            case "??????????????????":
                return "CLOSE_ALL_NOTIFY";
            default:
                throw new OrderException(toUser, "???????????????");
        }

    }

    private List<ExeSeq<String>> notifySort(List<LabelValueModel<Long, String>> values) {
        values = values.stream().sorted(Comparator.comparing(LabelValueModel::getLabel)).collect(Collectors.toList());
        int size = values.size();
        List<ExeSeq<String>> exeSeqList = new ArrayList<>();
        if (values.isEmpty()) {
            return null;
        }
        ExeSeq<String> tmp = new ExeSeq<>(values.get(0).getLabel(), values.get(0).getValue());
        for (int i = 0; i < size; i++) {
            if ( i != 0) {
                if (values.get(i).getLabel().equals(values.get(i - 1).getLabel())){
                    tmp.getValues().add(values.get(i).getValue());
                }else {
                    exeSeqList.add(tmp);
                    tmp = new ExeSeq<>();
                    tmp.setExecutorTime(values.get(i).getLabel());
                    tmp.getValues().add(values.get(i).getValue());
                }
            }
        }
        exeSeqList.add(tmp);
        return exeSeqList;
    }

    @Override
    public String getClassName() {
        return "NotifyServiceImpl";
    }
}
