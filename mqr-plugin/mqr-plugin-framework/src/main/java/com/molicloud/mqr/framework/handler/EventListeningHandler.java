package com.molicloud.mqr.framework.handler;

import cn.hutool.core.util.StrUtil;
import com.molicloud.mqr.common.enums.SettingEnum;
import com.molicloud.mqr.common.vo.RobotInfoVo;
import com.molicloud.mqr.plugin.core.PluginParam;
import com.molicloud.mqr.plugin.core.enums.RobotEventEnum;
import com.molicloud.mqr.framework.event.PluginResultEvent;
import com.molicloud.mqr.framework.util.PluginUtil;
import com.molicloud.mqr.service.SysSettingService;
import kotlin.coroutines.CoroutineContext;
import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.event.EventHandler;
import net.mamoe.mirai.event.ListeningStatus;
import net.mamoe.mirai.event.SimpleListenerHost;
import net.mamoe.mirai.message.FriendMessageEvent;
import net.mamoe.mirai.message.GroupMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * 事件监听处理器
 *
 * @author feitao yyimba@qq.com
 * @since 2020/11/4 5:01 下午
 */
@Slf4j
@Component
public class EventListeningHandler extends SimpleListenerHost {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private SysSettingService sysSettingService;

    /**
     * 监听群消息
     *
     * @param event
     * @return
     */
    @EventHandler
    public ListeningStatus onGroupMessage(GroupMessageEvent event) {
        // 实例化插件入参对象
        PluginParam pluginParam = new PluginParam();
        pluginParam.setFrom(String.valueOf(event.getSender().getId()));
        pluginParam.setTo(String.valueOf(event.getGroup().getId()));
        pluginParam.setData(event.getMessage().contentToString());
        pluginParam.setRobotEventEnum(RobotEventEnum.GROUP_MSG);
        pluginParam.setAt(isAt(event));
        pluginParam.setAdmins(robotAdmins());
        // 处理消息事件
        handlerMessageEvent(pluginParam);
        // 保持监听
        return ListeningStatus.LISTENING;
    }

    /**
     * 监听好友消息
     *
     * @param event
     * @return
     */
    @EventHandler
    public ListeningStatus onFriendsMessage(FriendMessageEvent event) {
        // 实例化插件入参对象
        PluginParam pluginParam = new PluginParam();
        pluginParam.setFrom(String.valueOf(event.getFriend().getId()));
        pluginParam.setTo(String.valueOf(event.getBot().getId()));
        pluginParam.setData(event.getMessage().contentToString());
        pluginParam.setRobotEventEnum(RobotEventEnum.FRIEND_MSG);
        pluginParam.setAt(false);
        pluginParam.setAdmins(robotAdmins());
        // 处理消息事件
        handlerMessageEvent(pluginParam);
        // 保持监听
        return ListeningStatus.LISTENING;
    }

    @Override
    public void handleException(CoroutineContext context, Throwable exception) {
        throw new RuntimeException("在事件处理中发生异常", exception);
    }

    /**
     * 处理消息事件
     *
     * @param pluginParam
     */
    private void handlerMessageEvent(PluginParam pluginParam) {
        // 封装插件结果处理事件
        PluginResultEvent pluginResultEvent = new PluginResultEvent();
        pluginResultEvent.setPluginParam(pluginParam);
        // 执行插件，执行成功则推送异步处理的事件
        if (PluginUtil.executePlugin(pluginResultEvent)) {
            eventPublisher.publishEvent(pluginResultEvent);
        }
    }

    /**
     * 获取机器人管理员列表
     *
     * @return
     */
    private String[] robotAdmins() {
        RobotInfoVo robotInfoVo = sysSettingService.getSysSettingByName(SettingEnum.ROBOT_INFO, RobotInfoVo.class);
        if (robotInfoVo == null) {
            return new String[]{};
        }
        return robotInfoVo.getAdmins();
    }

    /**
     * 判断群消息事件中是否At了机器人
     *
     * @param event
     * @return
     */
    private boolean isAt(GroupMessageEvent event) {
        StringBuffer atBuffer = new StringBuffer();
        atBuffer.append("[mirai:at:");
        atBuffer.append(event.getBot().getId());
        atBuffer.append(",@");
        atBuffer.append(event.getBot().getNick());
        atBuffer.append("]");
        return StrUtil.contains(event.getMessage().toString(), atBuffer.toString());
    }
}