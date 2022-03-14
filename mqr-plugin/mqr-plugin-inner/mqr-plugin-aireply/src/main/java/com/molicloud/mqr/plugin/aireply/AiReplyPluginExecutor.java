package com.molicloud.mqr.plugin.aireply;

import cn.hutool.json.JSONObject;
import com.google.zxing.common.StringUtils;
import com.molicloud.mqr.plugin.core.AbstractPluginExecutor;
import com.molicloud.mqr.plugin.core.PluginParam;
import com.molicloud.mqr.plugin.core.PluginResult;
import com.molicloud.mqr.plugin.core.annotation.PHook;
import com.molicloud.mqr.plugin.core.enums.RobotEventEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PHook(name = "AiReply",
            defaulted = true,
            robotEvents = { RobotEventEnum.FRIEND_MSG, RobotEventEnum.GROUP_MSG })
    public PluginResult messageHandler(PluginParam pluginParam) {
        // 接收消息
        String message = String.valueOf(pluginParam.getData());

        if(null != message){
            if(message.contains("近 20 期")){
                String qihao = message.substring(message.indexOf("\n283")+1,message.indexOf("期："));

                String body = getCommandge(qihao);
                if (body != null) {
                    // 实例化回复对象
                    PluginResult pluginResult = new PluginResult();
                    pluginResult.setProcessed(true);
                    pluginResult.setMessage(body);
                    return pluginResult;
                }
            }
        }

        // 实例化回复对象
        PluginResult pluginResult = new PluginResult();
        pluginResult.setProcessed(true);
        pluginResult.setMessage(null);
        return pluginResult;
    }

    private String getCommandge(String qihao){
        RestTemplate client = new RestTemplate();
        ResponseEntity<String> response = client.exchange("http://121.4.87.215:8582/digital/digitalAnalyseJnd/queryByRecordNumber?recordNumber="+qihao, HttpMethod.GET, null, String.class);

        if(response.getStatusCode() == HttpStatus.OK){
            String body = response.getBody();
            if(null == body){
                return null;
            }
            if(body.contains("未找到对应数据")){
                try {
                    Thread.sleep(3000);
                    String b = getCommandge(qihao);
                    if(null != b){
                        JSONObject jsonObject = new JSONObject(b);
                        JSONObject result = jsonObject.getJSONObject("result");

                        Object obj01 = result.getObj("analyse01", null);
                        Object obj02 = result.getObj("analyse02", null);
                        Integer score = result.getInt("score", 50);

                        if(null == obj01 && null == obj02){
                            return null;
                        } else if(null != obj01 && null == obj02){
                            String s = obj01.toString();
                            if ("大双".equals(s)) {
                                return "大"+(score*2)+"小双"+score;
                            } else if ("大单".equals(s)) {
                                return "大"+(score*2)+"小单"+score;
                            } else if ("小单".equals(s)) {
                                return "小"+(score*2)+"大单"+score;
                            } else if ("小双".equals(s)) {
                                return "小"+(score*2)+"大双"+score;
                            } else {
                                return null;
                            }
                        } else if(null == obj01 && null != obj02){
                            String s = obj02.toString();
                            if ("大双".equals(s)) {
                                return "大"+(score*2)+"小双"+score;
                            } else if ("大单".equals(s)) {
                                return "大"+(score*2)+"小单"+score;
                            } else if ("小单".equals(s)) {
                                return "小"+(score*2)+"大单"+score;
                            } else if ("小双".equals(s)) {
                                return "小"+(score*2)+"大双"+score;
                            } else {
                                return null;
                            }
                        } else {
                            String s = obj01.toString();
                            if ("大双".equals(s)) {
                                return "大"+(score*2)+"小双"+score;
                            } else if ("大单".equals(s)) {
                                return "大"+(score*2)+"小单"+score;
                            } else if ("小单".equals(s)) {
                                return "小"+(score*2)+"大单"+score;
                            } else if ("小双".equals(s)) {
                                return "小"+(score*2)+"大双"+score;
                            } else {
                                return null;
                            }
                        }
                    } else {
                        return null;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return null;
                }
            } else if(body.contains("无需提交")){
                return null;
            } else {
                JSONObject jsonObject = new JSONObject(body);
                JSONObject result = jsonObject.getJSONObject("result");

                Object obj01 = result.getObj("analyse01", null);
                Object obj02 = result.getObj("analyse02", null);
                Integer score = result.getInt("score", 50);

                if(null == obj01 && null == obj02){
                    return null;
                } else if(null != obj01 && null == obj02){
                    String s = obj01.toString();
                    if ("大双".equals(s)) {
                        return "大"+(score*2)+"小双"+score;
                    } else if ("大单".equals(s)) {
                        return "大"+(score*2)+"小单"+score;
                    } else if ("小单".equals(s)) {
                        return "小"+(score*2)+"大单"+score;
                    } else if ("小双".equals(s)) {
                        return "小"+(score*2)+"大双"+score;
                    } else {
                        return null;
                    }
                } else if(null == obj01 && null != obj02){
                    String s = obj02.toString();
                    if ("大双".equals(s)) {
                        return "大"+(score*2)+"小双"+score;
                    } else if ("大单".equals(s)) {
                        return "大"+(score*2)+"小单"+score;
                    } else if ("小单".equals(s)) {
                        return "小"+(score*2)+"大单"+score;
                    } else if ("小双".equals(s)) {
                        return "小"+(score*2)+"大双"+score;
                    } else {
                        return null;
                    }
                } else {
                    String s = obj01.toString();
                    if ("大双".equals(s)) {
                        return "大"+(score*2)+"小双"+score;
                    } else if ("大单".equals(s)) {
                        return "大"+(score*2)+"小单"+score;
                    } else if ("小单".equals(s)) {
                        return "小"+(score*2)+"大单"+score;
                    } else if ("小双".equals(s)) {
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