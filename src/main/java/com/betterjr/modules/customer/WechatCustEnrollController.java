package com.betterjr.modules.customer;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.common.web.Servlets;
import com.betterjr.modules.wechat.IWeChatCustEnrollService;

/**
 * WEB容器环境接入
 */

@Controller
@RequestMapping(value = "/Wechat/Platform/Enroll")
public class WechatCustEnrollController {

    private static final Logger logger = LoggerFactory.getLogger(WechatCustEnrollController.class);

    @Reference(interfaceClass = IWeChatCustEnrollService.class)
    private IWeChatCustEnrollService weChatCustEnrollDubboService;

    @RequestMapping(value = "/addEnroll", method = RequestMethod.POST)
    public @ResponseBody String addCustEnroll(final HttpServletRequest request, final String fileList) {
        try {
            final Object openIdObj = Servlets.getSession().getAttribute("wechat_openId");
            if (openIdObj != null) {
                final Map anMap = Servlets.getParametersStartingWith(request, "");
                logger.info("微信端客户开户,入参:" + anMap.toString());
                final String openId = String.valueOf(openIdObj);
                return weChatCustEnrollDubboService.webAddCustEnroll(anMap, openId, fileList);
            }
            return AjaxObject.newError("开户失败").toJson();
        }
        catch (final Exception e) {
            return AjaxObject.newError("开户失败").toJson();
        }
    }

    @RequestMapping(value = "/findEnroll", method = RequestMethod.POST)
    public @ResponseBody String findCustEnroll() {
        try {
            return weChatCustEnrollDubboService.webFindCustEnroll();
        }
        catch (final Exception e) {
            return AjaxObject.newError("获取开户信息失败").toJson();
        }
    }
}
