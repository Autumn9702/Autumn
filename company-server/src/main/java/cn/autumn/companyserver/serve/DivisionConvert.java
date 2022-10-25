package cn.autumn.companyserver.serve;

import cn.autumn.companyserver.entity.Args;
import cn.autumn.companyserver.entity.Region;
import cn.autumn.companyserver.model.ArgsModel;
import cn.autumn.companyserver.entity.Error;
import cn.autumn.companyserver.entity.Division;
import cn.autumn.companyserver.repository.DivisionRepository;
import cn.autumn.companyserver.repository.ErrorRepository;
import cn.autumn.companyserver.repository.RegionRepository;
import cn.autumn.model.LngLat;
import cn.autumn.util.BaiduUtil;
import cn.autumn.util.PositionUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author cf
 * Created in 16:02 2022/9/26
 */
@Service
public class DivisionConvert {

    @Resource
    private RegionRepository regionRepository;

    @Resource
    private ErrorRepository errorRepository;

    @Resource
    private DivisionRepository divisionRepository;

    public void processArData() throws InterruptedException {
        List<Region> regionAll = regionRepository.getRegionAll();
        List<Error> errorAll = errorRepository.getErrorAll();
        List<Error> newErrors = new ArrayList<>();
        List<String> deleteErr = new ArrayList<>();
        List<String> dns = divisionRepository.getDivisionNameAll();
        divErrPro(dns,errorAll, deleteErr);
        for (Region region : regionAll) {
            if (region.getLevel() > 3) continue;
            if ("市辖县".equals(region.getName()) || "市辖区".equals(region.getName())) continue;
            if (dns.contains(region.getName())) continue;
            if (errorAll.stream().anyMatch(e -> e.getName().equals(region.getName()))) continue;
            if (newErrors.stream().anyMatch(e -> e.getName().equals(region.getName()))) continue;
            String s = requestToData(region.getName());
            if (s == null) {
                errorSave(errorAll, newErrors, new Error(region.getName()));
                continue;
            }
            String data = BaiduUtil.filterStr(s);
            Map<Double, Double> epm = BaiduUtil.strToMap(data);
            List<LngLat> coordinates = BaiduUtil.convertCoordinate(epm);

            Args args = new Args();
            args.setPoints(new ArrayList<>());
            args.getPoints().add(coordinates);

            Division division = new Division();
            division.setName(region.getName());
            division.setCoordType("WGS84");
            division.setParameter(args);
            divisionRepository.save(division);
            dns.add(region.getName());
            verifyDelete(errorAll, region.getName(), deleteErr);
            System.out.println("已存入行政区: " + region.getName());
            Thread.sleep(1000);
        }
        errorRepository.saveAll(newErrors);
    }

    public Map<Double, Double> ioToMap(String path) throws IOException {
        File f = new File(path);
        BufferedReader b = new BufferedReader(new FileReader(f));
        StringBuilder sb = new StringBuilder();
        String s;
        while ((s = b.readLine()) != null) {
            sb.append(s);
        }
        Object o = JSONObject.parseObject(sb.toString(), Object.class);
        return JSONObject.parseObject(JSONObject.toJSONString(o), new TypeReference<Map<Double, Double>>() {}.getType());

    }

    public String requestToData(String requestParameter) {
        String urlHead = "https://api.map.baidu.com/?qt=s&wd=";
        String urlTail = "&ie=utf-8&oue=1&fromproduct=jsapi&v=2.1&res=api&callback=BMap._rd._cbk8624&ak=zPgkyADDtGL8fI20nddQQzonEEFKfKvG&seckey=RZcvwOLtU%2FiNKRQhsvZaejcutFf2B83fCNzDNV7S2Xs%3D%2Cg8X6lNzX3vQ989Yr1GuP1QRTYd_AMFhgOcrezIeOAjcz77oPeR_5s7HzEm9jH8_cRF1sdIKGczxzgRE3GK7FxLW9KRJgkTv4OGLTbXH6ha9bI7inFfB49OFvELpELIpog2lmLMww_2B-AGX4tPz5NQauy2QVyzKRzUtLGhGIGGpP3cBPQHObF038kEOWBZ7s&timeStamp=1663927849469&sign=7776682ff68b";
        String s = HttpUtil.get(urlHead + requestParameter + urlTail);
        s = s.substring(43, s.length() - 1);
        JSONObject jsonObject = JSONObject.parseObject(s);
        Object content = jsonObject.get("content");
        if (content instanceof JSONObject) {
            JSONObject ext;
            try {
                ext = ((JSONObject) content).getJSONObject("ext");
            } catch (ClassCastException ce) {
                return null;
            }

            JSONObject defaultInfo = ext.getJSONObject("detail_info");
            JSONObject guokeGeo = defaultInfo.getJSONObject("guoke_geo");
            if (Objects.isNull(guokeGeo)) {
                return null;
            }
            return guokeGeo.getString("geo");
        } else if (content instanceof JSONArray) {
            JSONArray contentArray = (JSONArray) content;

            for (Object o : contentArray) {

                if (o instanceof JSONObject) {
                    JSONObject ext;
                    try {
                        ext = ((JSONObject) o).getJSONObject("ext");
                    } catch (ClassCastException ce) {
                        continue;
                    }
                    if (Objects.isNull(ext)) {
                        continue;
                    }
                    JSONObject defaultInfo = ext.getJSONObject("detail_info");
                    if (Objects.isNull(defaultInfo)) continue;
                    JSONObject guokeGeo = defaultInfo.getJSONObject("guoke_geo");
                    if (Objects.isNull(guokeGeo)) continue;
                    String geo = guokeGeo.getString("geo");
                    if (Objects.isNull(geo)) continue;
                    return geo;
                }

            }

        }
        return null;
    }


    public void errorSave(List<Error> old, List<Error> newError, Error verifyError) {
        boolean contains = old.contains(verifyError);
        if (!contains) {
            newError.add(verifyError);
            if (newError.size() > 10) {
                errorRepository.saveAll(newError);
                System.out.println("存入异常区域: " + newError.size() + "条");
                old.addAll(newError);
                newError.clear();
            }
        }
    }

    public void divErrPro(List<String> div, List<Error> err, List<String> delete) {
        for (Error e : err) {
            if (div.contains(e.getName())) {
                System.out.println("---- " + e.getName() + " ----");
                delete.add(e.getName());
            }
        }

        if (!delete.isEmpty()) {
            errorRepository.deleteErrorByName(delete);
            System.out.println("|||| 过滤数据: " + delete.size() + "条");
            delete.clear();
        }
    }

    public void verifyDelete(List<Error> errorAll, String regionName, List<String> deleteErr) {
        if (errorAll.removeIf(error -> error.getName().equals(regionName))) {
            System.out.println("已处理数组格式行政区: " + regionName);
            deleteErr.add(regionName);
            if (deleteErr.size() > 10) {
                errorRepository.deleteErrorByName(deleteErr);
                deleteErr.clear();
                System.out.println("剔除异常行政区");
            }
        }
    }

    public boolean arBd09ToWgs84(ArgsModel am) {
        Division division = divisionRepository.getDivisionByName(am.getName());
        if (Objects.nonNull(division)) return false;
        List<List<LngLat>> p = am.getPoints();

        for (List<LngLat> lngLats : p) {

            for (LngLat point : lngLats) {
                Point xy = PositionUtil.bd09ToWgs84(point.getLat(), point.getLng());
                point.setLat(xy.getX());
                point.setLng(xy.getY());
            }
            lngLats.add(lngLats.get(0));
        }
        System.out.println(p.size());
        division = new Division();
        division.setName(am.getName());
        division.setCoordType("WGS84");
        Args parameter = new Args();
        parameter.setPoints(p);
        division.setParameter(parameter);
        Division save = divisionRepository.save(division);
//        if (Objects.nonNull(save.getId())) errorRepository.deleteErrorByName(save.getName());
        return true;
    }

    public void manualProcessDivision(String divisionName) throws InterruptedException {

        List<Error> errorAll = errorRepository.getErrorAll();
        List<Error> newErrors = new ArrayList<>();
        List<String> deleteErr = new ArrayList<>();
        List<String> dns = divisionRepository.getDivisionNameAll();
        divErrPro(dns,errorAll, deleteErr);

        if (errorAll.stream().anyMatch(e -> e.getName().equals(divisionName))) return;
        String s = requestToData(divisionName);
        if (s == null) {
            errorSave(errorAll, newErrors, new Error(divisionName));
            return;
        }
        String data = BaiduUtil.filterStr(s);
        Map<Double, Double> epm = BaiduUtil.strToMap(data);
        List<LngLat> coordinates = BaiduUtil.convertCoordinate(epm);

        Args args = new Args();
        args.setPoints(new ArrayList<>());
        args.getPoints().add(coordinates);

        Division division = new Division();
        division.setName(divisionName);
        division.setCoordType("WGS84");
        division.setParameter(args);
        divisionRepository.save(division);
        dns.add(divisionName);
        verifyDelete(errorAll, divisionName, deleteErr);
        System.out.println("已存入行政区: " + divisionName);
        Thread.sleep(1000);

        errorRepository.saveAll(newErrors);
    }

    public static void main(String[] args) throws ScriptException, FileNotFoundException, NoSuchMethodException {
        String s = te("北京");
        System.out.println(s);
    }

    private static String te(String name) throws FileNotFoundException, ScriptException, NoSuchMethodException {
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine js = sem.getEngineByName("javascript");
        js.eval(new FileReader("./resource/html/index.html"));
        if(js instanceof Invocable){
            Invocable in = (Invocable) js;
            //调用JS方法，sorting为JS文件中的方法名，后面三个为方法需要的三个参数
            String result = (String) in.invokeFunction("getMapPoint",name);
            return result;
        }
        return null;
    }
}
