package com.molicloud.mqr.plugin.aireply;

import com.google.zxing.common.StringUtils;
import com.molicloud.mqr.plugin.core.AbstractPluginExecutor;
import com.molicloud.mqr.plugin.core.PluginParam;
import com.molicloud.mqr.plugin.core.PluginResult;
import com.molicloud.mqr.plugin.core.annotation.PHook;
import com.molicloud.mqr.plugin.core.enums.RobotEventEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * 智能回复插件
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/6 3:45 下午
 */
@Slf4j
@Component
public class AiReplyPluginExecutor extends AbstractPluginExecutor {

    private static String qihao = null;

    @PHook(name = "AiReply",
            defaulted = true,
            robotEvents = { RobotEventEnum.FRIEND_MSG, RobotEventEnum.GROUP_MSG })
    public PluginResult messageHandler(PluginParam pluginParam) {
        // 接收消息
        String message = String.valueOf(pluginParam.getData());

        if(null != message){
            if(message.contains("近 20 期")){
                qihao = message.substring(message.indexOf("\n283")+1,message.indexOf("期："));

                String body = null;
                try {
                    body = getCommandge(qihao, 1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (body != null) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // 实例化回复对象
                    PluginResult pluginResult = new PluginResult();
                    pluginResult.setProcessed(true);
                    pluginResult.setMessage(body);
                    return pluginResult;
                }
//            } else if(null != qihao && message.contains(qihao)){
//                // 实例化回复对象
//                PluginResult pluginResult = new PluginResult();
//                pluginResult.setProcessed(true);
//                pluginResult.setMessage("1");
//                return pluginResult;
            }
        }

        // 实例化回复对象
        PluginResult pluginResult = new PluginResult();
        pluginResult.setProcessed(true);
        pluginResult.setMessage(null);
        return pluginResult;
    }

    private String getCommandge(String qihao, int temp) throws JSONException {
        RestTemplate client = new RestTemplate();
        ResponseEntity<String> response = client.exchange("http://121.4.87.215:8582/digital/digitalAnalyseJnd/queryByRecordNumber?recordNumber="+qihao, HttpMethod.GET, null, String.class);
        if(response.getStatusCode() == HttpStatus.OK){
            String body = response.getBody();
            if(null == body){
                return null;
            }
            log.info("请求后数据："+body);
            if(body.contains("未找到对应数据")){
                if(temp>30){
                    return null;
                } else {
                    try {
                        temp++;
                        Thread.sleep(3000);
                        return getCommandge(qihao, temp);
                    } catch (InterruptedException | JSONException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            } else if(body.contains("无需提交")){
                return null;
            } else {
                JSONObject jsonObject = new JSONObject(body);
                JSONObject result = jsonObject.getJSONObject("result");

                String obj01 = result.getString("analyse01");
                String obj02 = result.getString("analyse02");
                Integer score = result.getInt("score");
                Integer digital = result.getInt("digital");

                if(null == obj01 && null == obj02){
                    return null;
                } else if(null != obj01 && null == obj02){
                    if ("大双".equals(obj01)) {
                        return "大"+(score*2)+"小双"+score;

                    } else if ("大单".equals(obj01)) {
                        if(digital>13){
                            return "大"+(score*2)+"小单"+score;
                        } else if (digital%2!=0) {
                            return "单"+(score*2)+"大双"+score;
                        } else {
                            return "大"+(score*2)+"小单"+score;
                        }
                    } else if ("小单".equals(obj01)) {
                        return "小"+(score*2)+"大单"+score;
                    } else if ("小双".equals(obj01)) {
                        if(digital<14){
                            return "小"+(score*2)+"大双"+score;
                        } else if (digital%2==0) {
                            return "双"+(score*2)+"小单"+score;
                        } else {
                            return "小"+(score*2)+"大双"+score;
                        }
                    } else {
                        return null;
                    }
                } else if(null == obj01 && null != obj02){
                    if ("大双".equals(obj02)) {
                        return "大"+(score*2)+"小双"+score;
                    } else if ("大单".equals(obj02)) {
                        return "大"+(score*2)+"小单"+score;
                    } else if ("小单".equals(obj02)) {
                        return "小"+(score*2)+"大单"+score;
                    } else if ("小双".equals(obj02)) {
                        return "小"+(score*2)+"大双"+score;
                    } else {
                        return null;
                    }
                } else {
                    if ("大双".equals(obj01)) {
                        return "大"+(score*2)+"小双"+score;
                    } else if ("大单".equals(obj01)) {
                        return "大"+(score*2)+"小单"+score;
                    } else if ("小单".equals(obj01)) {
                        return "小"+(score*2)+"大单"+score;
                    } else if ("小双".equals(obj01)) {
                        return "小"+(score*2)+"大双"+score;
                    } else if ("大双".equals(obj02)) {
                        return "大"+(score*2)+"小双"+score;
                    } else if ("大单".equals(obj02)) {
                        return "大"+(score*2)+"小单"+score;
                    } else if ("小单".equals(obj02)) {
                        return "小"+(score*2)+"大单"+score;
                    } else if ("小双".equals(obj02)) {
                        return "小"+(score*2)+"大双"+score;
                    } else {
                        return null;
                    }
                }
            }
        } else {
            return null;
        }
    }


}