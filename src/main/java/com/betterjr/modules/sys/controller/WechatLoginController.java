package com.betterjr.modules.sys.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.betterjr.common.data.UserType;
import com.betterjr.common.service.SpringContextHolder;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.common.web.Servlets;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.sys.security.ShiroUser;
import com.betterjr.modules.wechat.data.api.AccessToken;
import com.betterjr.modules.wechat.dubboclient.CustWeChatDubboClientService;

@Controller
@RequestMapping(value = "/")
public class WechatLoginController {
    private static Logger logger = LoggerFactory.getLogger(WechatLoginController.class);

    /**
     * 管理登录
     */
    @RequestMapping(value = "/wechatOauth2", method = { RequestMethod.GET, RequestMethod.POST })
    public String author2Login(final HttpServletRequest request, final HttpServletResponse response, final Model model) {
        final ShiroUser principal = UserUtils.getPrincipal();

        // 检查当前用户
        final CustWeChatDubboClientService wechatClientService = SpringContextHolder.getBean(CustWeChatDubboClientService.class);
        final ShiroUser shiroUser = UserUtils.getPrincipal();

        if (shiroUser.getUserType().equals(UserType.NONE_USER)) {
            logger.info("匿名用户登陆，去到开户页!");
            final AccessToken at = principal.getData();
            if (at != null) {
                Servlets.getSession().setAttribute("wechat_openId", at.getOpenId());
            }
            return "redirect:wechat/index.html#/register";
        }
        else {
            final CustOperatorInfo operator = UserUtils.getOperatorInfo();
            if (wechatClientService.checkFristLogin(operator.getId())) {
                return "redirect:wechat/index.html#/main";
            }

            // 如果已经登录，则跳转到管理首页
            if (principal != null && principal.isMobileLogin()) {
                final String state = request.getParameter("state");
                // logger.info("request state is :" + state);
                //
                logger.info("登陆，，并且是 移动端登陆");
                return "redirect:wechat/index.html#/main";
            }
            return "error";

        }

    }
}
