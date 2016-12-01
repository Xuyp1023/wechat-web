// Copyright (c) 2014-2016 Betty. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2016年9月18日, liuwl, creation
// ============================================================================
package com.betterjr.modules.wechat.controller;

import static com.betterjr.common.web.ControllerExceptionHandler.exec;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.betterjr.common.web.AjaxObject;
import com.betterjr.common.web.Servlets;
import com.betterjr.modules.document.entity.CustFileItem;
import com.betterjr.modules.document.service.DataStoreService;
import com.betterjr.modules.document.utils.FileWebClientUtils;
import com.betterjr.modules.wechat.dubboclient.CustWeChatDubboClientService;

/**
 * @author liuwl
 *
 */
@Controller
@RequestMapping(value = "/Wechat/Platform")
public class WechatPlatformController {

    private static final Logger logger = LoggerFactory.getLogger(WechatPlatformController.class);

    @Autowired
    private DataStoreService  dataStoreService;
    @Resource
    private CustWeChatDubboClientService wechatDubboService;

    /**
     * 首次登陆验证交易密码
     */
    @RequestMapping(value = "/checkFristTradePass", method = RequestMethod.POST)
    public @ResponseBody String checkFristTradePass(final String tradePassword) {
        return exec(() -> wechatDubboService.webSaveFristLoginTradePassword(tradePassword), "验证交易密码失败！", logger);
    }

    /**
     * 获取 AppId
     */
    @RequestMapping(value = "/getAppId", method = RequestMethod.POST)
    public @ResponseBody String getAppId() {
        try {
            return AjaxObject.newOk("获取 AppId成功", wechatDubboService.getAppId()).toJson();
        }
        catch (final Exception e) {
            return AjaxObject.newError("获取 AppId失败").toJson();
        }
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
     * 下载文件
     */
    @RequestMapping(value = "/fileDownload")
    public @ResponseBody void fileDownload(final Long id, final HttpServletResponse response) {
        try {
            final Object openIdObj = Servlets.getSession().getAttribute("wechat_openId");
            if (openIdObj != null) {
                final CustFileItem fileItem = wechatDubboService.fileDownload(id);
                FileWebClientUtils.fileDownload(dataStoreService, response, fileItem);
            }
        }
        catch (final Exception e) {
            logger.error("下载文件失败，请检查");
        }
    }

    /**
     * 查找文件列表
     */
    @RequestMapping(value = "/fileList", method = RequestMethod.POST)
    public @ResponseBody String fileList(final Long batchNo) {
        try {
            return AjaxObject.newOk("查询文件列表成功", wechatDubboService.findCustFiles(batchNo)).toJson();
        }
        catch (final Exception e) {
            return AjaxObject.newError("查询文件列表失败").toJson();
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

    @RequestMapping(value = "/toSuccess", method = { RequestMethod.POST, RequestMethod.GET })
    public void toSuccess(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
        final Object openIdObj = Servlets.getSession().getAttribute("wechat_openId");
        final String appId = wechatDubboService.getAppId();
        final String wechatUrl = wechatDubboService.getWechatUrl();
        if (openIdObj != null) {
            final String url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appId + "&redirect_uri=" + wechatUrl + "/wechatOauth2&response_type=code&scope=snsapi_base&state=10,1#wechat_redirect";
            resp.sendRedirect(url);
        }
    }
    /**
     * 开户
     */
    /*    @RequestMapping(value = "/addEnroll", method = RequestMethod.POST)
    public @ResponseBody String addCustEnroll(final HttpServletRequest request, final String coreCustNo, final String fileList) {
        try {
            final Object openIdObj = Servlets.getSession().getAttribute("wechat_openId");
            if (openIdObj != null) {
                final Map anMap = Servlets.getParametersStartingWith(request, "");
                logger.info("微信端客户开户,入参:" + anMap.toString());
                final String openId = String.valueOf(openIdObj);
                return AjaxObject.newOk("开户成功", weChatCustEnrollService.addCustEnroll(anMap, coreCustNo, openId, fileList)).toJson();
            }
            return AjaxObject.newError("开户失败").toJson();
        }
        catch (final Exception e) {
            return AjaxObject.newError("开户失败").toJson();
        }
    }
     */
    /**
     * 开户信息
     */
    /*    @RequestMapping(value = "/findEnroll", method = RequestMethod.POST)
    public @ResponseBody String findCustEnroll() {
        try {
            return AjaxObject.newOk("获取开户信息成功", wechatDubboService.findCustEnroll()).toJson();
        }
        catch (final Exception e) {
            return AjaxObject.newError("获取开户信息失败").toJson();
        }
    }
     */

}
