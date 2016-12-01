package com.betterjr.modules.sys.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.dubbo.config.annotation.Reference;
import com.betterjr.common.data.UserType;
import com.betterjr.common.service.SpringContextHolder;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.common.web.Servlets;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.customer.ICustOpenAccountService2;
import com.betterjr.modules.sys.security.ShiroUser;
import com.betterjr.modules.wechat.data.api.AccessToken;
import com.betterjr.modules.wechat.dispatcher.UrlDispatcher;
import com.betterjr.modules.wechat.dubboclient.CustWeChatDubboClientService;

@Controller
@RequestMapping(value = "/")
public class WechatLoginController {
    private static Logger logger = LoggerFactory.getLogger(WechatLoginController.class);

    @Reference(interfaceClass = ICustOpenAccountService2.class)
    private ICustOpenAccountService2 custOpenAccountService;

    /**
     * 管理登录
     * @throws IOException
     */
    @RequestMapping(value = "/wechatOauth2", method = { RequestMethod.GET, RequestMethod.POST })
    public void author2Login(final HttpServletRequest request, final HttpServletResponse response, final Model model) throws IOException {

        final ShiroUser shiroUser = UserUtils.getPrincipal();
        if (shiroUser != null) { // 这种情况表明登陆正常
            // 检查当前用户
            final CustWeChatDubboClientService wechatClientService = SpringContextHolder.getBean(CustWeChatDubboClientService.class);

            final AccessToken at = shiroUser.getParam("accessToken");
            if (at == null) {
                response.sendRedirect("wechat/403.html");
                return;
            }
            String openId = at.getOpenId();
            Servlets.getSession().setAttribute("wechat_openId", openId);
            if (shiroUser.getUserType().equals(UserType.NONE_USER)) {
                logger.info("匿名用户登陆，去到开户页!");
                
                String status = custOpenAccountService.findOpenAccountStatus(openId);
                if (BetterStringUtils.equals(status, "1")) {
                    response.sendRedirect("wechat/index.html#/register/waitAudit");
                } else if (BetterStringUtils.equals(status, "2")) {
                    response.sendRedirect("wechat/index.html#/register/waitActive");
                } else{
                    response.sendRedirect("wechat/index.html#/register/basic");
                }
                return;
            }
            else {
                final CustOperatorInfo operator = UserUtils.getOperatorInfo();
                if (operator != null) {
                    if (wechatClientService.checkFristLogin(operator.getId())) {
                        logger.info("首次登陆，去到验证交易密码页!");
                        response.sendRedirect("wechat/index.html#/accountBind");
                        return;
                    } else {
                        final String state = request.getParameter("state");
                        String url = UrlDispatcher.dispatch(state);
                        if (state != null && state.equals("10,1") == true) {
                            url = "./wechat/index.html#/register/accountSuccess/";
                        }
                        logger.info("正常用户，进入相应页面!" + url);
                        response.sendRedirect(url);
                        return;
                    }
                }
            }
        }

        response.sendRedirect("wechat/403.html");
    }
}
