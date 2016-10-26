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

import com.betterjr.common.web.AjaxObject;
import com.betterjr.common.web.Servlets;
import com.betterjr.modules.wechat.data.api.AccessToken;
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
     * 与微信服务器互动
     *
     * @param req
     *            微信服务器请求
     * @param resp
     *            响应微信服务器
     * @throws IOException
     */
    @RequestMapping(value = "/oauth2", method = { RequestMethod.POST, RequestMethod.GET })
    public void wxOauth2(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final Map<String, String> map = Servlets.getParameters(req);
        final WechatKernel wk = initKernel(map);
        for (final Map.Entry<String, String> ent : map.entrySet()) {
            logger.info("this is oauth2 values " + ent.getKey() + " = " + ent.getValue());
        }
        final AccessToken at = wk.findUserAuth2(map.get("code"));
        logger.info("wxOauth2 AccessToken"+at);
        resp.sendRedirect("http://atest.qiejf.com/better/p/pages/login.html");
    }

    /**
     * 获取JSTicket Signature
     */
    @RequestMapping(value = "/getJSSignature", method = RequestMethod.POST)
    public @ResponseBody String getJSSignature(final String url) {
        try {
            final Object openIdObj = Servlets.getSession().getAttribute("wechat_openId");
            if (openIdObj != null) {
                return AjaxObject.newOk("获取JSTicket Signature成功", wechatDubboService.getJSSignature(url)).toJson();
            }
            return AjaxObject.newError("获取JSTicket Signature失败").toJson();
        }
        catch (final Exception e) {
            return AjaxObject.newError("获取JSTicket Signature失败").toJson();
        }
    }

    /**
     * 上传文件资料
     */
    @RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
    public @ResponseBody String fileUpload(final String fileTypeName, final String fileMediaId) {
        try {
            final Object openIdObj = Servlets.getSession().getAttribute("wechat_openId");
            if (openIdObj != null) {
                return AjaxObject.newOk("保存附件成功", wechatDubboService.fileUpload(fileTypeName, fileMediaId)).toJson();
            }
            return AjaxObject.newError("保存附件失败").toJson();
        }
        catch (final Exception e) {
            return AjaxObject.newError("保存附件失败").toJson();
        }

    }

    /**
     * 文件资料下载
     *
     * @param id
     *            ；文件编号
     * @param response
     * @return
     */
    /*@RequestMapping(value = "/fileDownload")
    public @ResponseBody void fileDownload(final Long id, final HttpServletResponse response) {
        try {
            final Object openIdObj = Servlets.getSession().getAttribute("wechat_openId");
            if (openIdObj != null) {
                final CustFileItem fileItem = fileItemService.findOne(id);
                CustFileClientUtils.fileDownload(response, fileItem);
            }
        }
        catch (final Exception e) {
            logger.error("下载文件失败，请检查");
        }
    }

    @RequestMapping(value = "/toHome", method = { RequestMethod.POST, RequestMethod.GET })
    public void toHome(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final Object openIdObj = Servlets.getSession().getAttribute("wechat_openId");
        final String appId = wechatDubboService.getAppId();
        final String wechatUrl = wechatDubboService.getWechatUrl();
        if (openIdObj != null) {
            final String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appId + "&redirect_uri=" + wechatUrl + "/wechatOauth2&response_type=code&scope=snsapi_base&state=10,6#wechat_redirect";
            resp.sendRedirect(url);
        }
    }

    @RequestMapping(value = "/checkUser", method = { RequestMethod.POST, RequestMethod.GET })
    public @ResponseBody String checkUser(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final ShiroUser shiroUser = UserUtils.getPrincipal();
        if (shiroUser != null) {
            if (shiroUser.getUserType() != UserType.NONE_USER) {
                return AjaxObject.newOk("检查成功", 1).toJson();
            }
        }
        return AjaxObject.newOk("检查成功", 0).toJson();
    }*/
}
