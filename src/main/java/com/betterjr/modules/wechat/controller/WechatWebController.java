package com.betterjr.modules.wechat.controller;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.betterjr.common.data.UserType;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.common.web.Servlets;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.sys.security.ShiroUser;
import com.betterjr.modules.wechat.dubboclient.CustWeChatDubboClientService;
import com.betterjr.modules.wechat.util.WechatDefHandler;
import com.betterjr.modules.wechat.util.WechatKernel;

/**
 * WeChat WEB容器环境接入
 *
 * @author zhoucy
 */

@Controller
@RequestMapping(value = "/Wechat/wxRequest")
public class WechatWebController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    @Resource
    private CustWeChatDubboClientService wechatDubboService;

    protected WechatKernel initKernel(final Map<String, String> anMap) {
        final WechatKernel wk = new WechatKernel(wechatDubboService.getMpAccount(), new WechatDefHandler(wechatDubboService), anMap);
        return wk;
    }

    /**
     * 与微信服务器互动
     *
     * @param req
     *            微信服务器请求9
     * @param resp
     *            响应微信服务器
     * @throws IOException
     */
    @RequestMapping(value = "/dispatcher", method = { RequestMethod.POST, RequestMethod.GET })
    public void wxDispatcher(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final Map<String, String> map = Servlets.getParameters(req);
        final WechatKernel wk = initKernel(map);
        String respmsg = "success";
        if ("GET".equals(req.getMethod())) {
            respmsg = wk.check();
        }
        else {
            respmsg = wk.handle(req.getInputStream());
        }
        // 输出回复消息
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");
        resp.getWriter().print(respmsg);
    }

    /**
     * 检查用户
     */
    @RequestMapping(value = "/checkUser", method = { RequestMethod.POST, RequestMethod.GET })
    public @ResponseBody String checkUser(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final ShiroUser shiroUser = UserUtils.getPrincipal();
        if (shiroUser != null) {
            final CustOperatorInfo operator = UserUtils.getOperatorInfo();
            if (UserType.NONE_USER.equals(shiroUser.getUserType()) == true) {
                return AjaxObject.newOk("检查成功", 2).toJson();
            } else if (operator != null){
                return AjaxObject.newOk("检查成功", 1).toJson();
            }
        }
        return AjaxObject.newOk("检查成功", 0).toJson();
    }


    /**
     * 与微信服务器互动
     *
     * @param req
     *            微信服务器请求
     * @param resp
     *            响应微信服务器
     * @throws IOException
     */
    /*@RequestMapping(value = "/oauth2", method = { RequestMethod.POST, RequestMethod.GET })
    public void wxOauth2(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final Map<String, String> map = Servlets.getParameters(req);
        final WechatKernel wk = initKernel(map);
        for (final Map.Entry<String, String> ent : map.entrySet()) {
            logger.debug("this is oauth2 values " + ent.getKey() + " = " + ent.getValue());
        }
        final AccessToken at = wk.findUserAuth2(map.get("code"));
        logger.debug("wxOauth2 AccessToken" + at);

        if (BetterStringUtils.isNotBlank(at.getOpenId())) {
            Servlets.getSession().setAttribute("wechat_openId", at.getOpenId());

            final String state = req.getParameter("state");
            String url = UrlDispatcher.dispatch(state);
            final CustWeChatInfo wechatUser = wechatDubboService.findWechatUserByOpenId(at.getOpenId());
            if (wechatUser != null) {
                final Long operId = wechatUser.getOperId();
                if (operId != null) {
                    if (BetterStringUtils.equals(state, "10,1")) {
                        final String appId = wechatDubboService.getAppId();
                        final String wechatUrl = wechatDubboService.getWechatUrl();
                        url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appId + "&redirect_uri=" + wechatUrl + "/wechatOauth2&response_type=code&scope=snsapi_base&state=10,2#wechat_redirect";
                    }
                }
            }
            else { // 登记此微信用户
                final Follower follower = wechatDubboService.findFollower(at.getOpenId());
                if (follower != null) {
                    wechatDubboService.saveNewWeChatInfo(at.getOpenId(), at.getOpenId(), follower.getSubscribe());
                }
            }
            resp.sendRedirect(url);
        }
        else {
            resp.sendRedirect("/error.html");
        }
    }*/
}
