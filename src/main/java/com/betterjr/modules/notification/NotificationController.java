// Copyright (c) 2014-2017 Bytter. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.2 : 2017年2月27日, liuwl, creation
// ============================================================================
package com.betterjr.modules.notification;

import static com.betterjr.common.web.ControllerExceptionHandler.exec;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.betterjr.common.web.Servlets;

/**
 * @author liuwl
 *
 */
@Controller
@RequestMapping(path = "/Wechat/Notification")
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Reference(interfaceClass = INotificationService.class)
    private INotificationService notificationService;

    /**
     * 未读消息列表-查询
     *
     * @return
     */
    @RequestMapping(value = "/queryUnreadNotification", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String queryUnreadNotification(final HttpServletRequest request, final int flag,
            final int pageNum, final int pageSize) {
        final Map<String, Object> anParam = Servlets.getParametersStartingWith(request, "");
        logger.debug("未读消息列表-查询 入参:anParam=" + anParam);
        return exec(() -> notificationService.webQueryUnreadNotification(anParam, flag, pageNum, pageSize),
                "未读消息列表-查询 出错", logger);
    }

    /**
     * 已读消息列表-查询
     *
     * @return
     */
    @RequestMapping(value = "/queryReadNotification", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String queryReadNotification(final HttpServletRequest request, final int flag,
            final int pageNum, final int pageSize) {
        final Map<String, Object> anParam = Servlets.getParametersStartingWith(request, "");
        logger.debug("已读消息列表-查询 入参:anParam=" + anParam);
        return exec(() -> notificationService.webQueryReadNotification(anParam, flag, pageNum, pageSize),
                "已读消息列表-查询 出错", logger);
    }

    /**
     * 未读消息数量-查询
     *
     * @return
     */
    @RequestMapping(value = "/countUnreadNotification", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String countUnreadNotification(final HttpServletRequest request) {
        return exec(() -> notificationService.webCountUnreadNotification(), "未读消息数量-查询 出错", logger);
    }

    /**
     * 消息详情
     *
     * @return
     */
    @RequestMapping(value = "/findNotification", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String countUnreadNotification(final HttpServletRequest request, final Long id) {
        logger.debug("消息详情 入参:id=" + id);
        return exec(() -> notificationService.webFindNotification(id), "消息详情查询 出错", logger);
    }

    /**
     * 设置消息已读状态
     *
     * @return
     */
    @RequestMapping(value = "/setReadNotificationStatus", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String setReadNotificationStatus(final HttpServletRequest request, final Long id) {
        logger.debug("设置消息已读状态 入参:id=" + id);
        return exec(() -> notificationService.webSetReadNotificationStatus(id), "设置消息已读状态 出错", logger);
    }
}
