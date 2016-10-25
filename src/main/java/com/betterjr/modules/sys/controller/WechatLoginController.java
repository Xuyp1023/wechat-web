package com.betterjr.modules.sys.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.betterjr.common.service.SpringContextHolder;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.sys.security.ShiroUser;
import com.betterjr.modules.sys.security.WechatAuthenticationFilter;
import com.betterjr.modules.wechat.dubboclient.CustWeChatDubboClientService;

@Controller
@RequestMapping(value = "/")
public class WechatLoginController {

    /**
     * 管理登录
     */
    @RequestMapping(value = "/wechatOauth2", method = {RequestMethod.GET,RequestMethod.POST})
    public String author2Login(final HttpServletRequest request, final HttpServletResponse response, final Model model) {
        final ShiroUser principal = UserUtils.getPrincipal();

        // 检查当前用户
        final CustWeChatDubboClientService wechatClientService = SpringContextHolder.getBean(CustWeChatDubboClientService.class);
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();
        if (wechatClientService.checkFristLogin(operator.getId())) {
            return "/static/wechat/frist.html";
        }

        // 如果已经登录，则跳转到管理首页
        if (principal != null && principal.isMobileLogin()) {
            final String state = request.getParameter("state");
            //            logger.info("request state is :" + state);
            return "redirect:" + WechatAuthenticationFilter.findWorkUrl(state);
        }

        return "/static/wechat/main.html";

    }
}
