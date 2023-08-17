package com.game_trade.utils;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CertifiUtil {
    // APPCODE
    private static final String APPCODE = "66394aebe1644d5e83d2b1bebf57e88c";

    // API地址
    private static final String URL = "https://eid.shumaidata.com/eid/check";

    // 认证成功
    public static final String SUCCESS = "1";
    // 认证结果
    public static final String RES = "res";
    public static Map<String,String> checkCertifi(String name, String idcard) throws IOException {
        Map<String, String> params = new HashMap<>();
        params.put("idcard", idcard);
        params.put("name", name);
        Map<String,String> result = postForm(params);
        log.info("实名认证结果：" + result);
        return result;
    }


//     {
//     "code": "0", //返回码，0：成功，非0：失败（详见错误码定义）
//         //当code=0时，再判断下面result中的res；当code!=0时，表示调用已失败，无需再继续
//         "message": "成功", //返回码说明
//         "result": {
//     "name": "冯天", //姓名
//             "idcard": "350301198011129422", //身份证号
//             "res": "1", //核验结果状态码，1 一致；2 不一致；3 无记录
//             "description": "一致",  //核验结果状态描述
//             "sex": "男",
//             "birthday": "19940320",
//             "address": "江西省南昌市东湖区"
//     }
    private static Map<String,String> postForm(Map<String, String> params) throws IOException {
        OkHttpClient client = new OkHttpClient.Builder().build();
        FormBody.Builder formbuilder = new FormBody.Builder();
        for (String key : params.keySet()) {
            formbuilder.add(key, params.get(key));
        }
        FormBody body = formbuilder.build();
        Request request = new Request.Builder().url(CertifiUtil.URL).addHeader("Authorization", "APPCODE " + CertifiUtil.APPCODE).post(body).build();
        Response response = client.newCall(request).execute();
        log.info("返回状态码" + response.code() + ",message:" + response.message());
        assert response.body() != null;
        String resp = response.body().string();
        log.info("API返回结果" + resp);
        JSONObject jsonObject = JSON.parseObject(resp);
        String code = jsonObject.getString("code");
        if (!"0".equals(code)) {
            log.info("实名认证接口调用失败");
            return null;
        } else {
            JSONObject result = jsonObject.getJSONObject("result");
            String res = result.getString(RES);
            String name = result.getString("name");
            String idCard = result.getString("idcard");
            log.info("姓名：" + name + ",身份证号：" + idCard + ",认证结果：" + res);
            Map<String,String> map = new HashMap<>();
            map.put("name",name);
            map.put("idCard",idCard);
            map.put(RES,res);
            return map;
        }
    }


}
