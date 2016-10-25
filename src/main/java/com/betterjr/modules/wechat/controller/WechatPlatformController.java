// Copyright (c) 2014-2016 Betty. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2016年9月18日, liuwl, creation
// ============================================================================
package com.betterjr.modules.wechat.controller;

import static com.betterjr.common.web.ControllerExceptionHandler.exec;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.wechat.dubboclient.CustWeChatDubboClientService;

/**
 * @author liuwl
 *
 */
@Controller
@RequestMapping(value = "/Platform/Wechat")
public class WechatPlatformController {

    private static final Logger logger = LoggerFactory.getLogger(WechatPlatformController.class);

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
     * 获取JSTicket Signature
     */
    @RequestMapping(value = "/getJSSignature", method = RequestMethod.POST)
    public @ResponseBody String getJSSignature(final String url) {
        try {
            return AjaxObject.newOk("获取JSTicket Signature成功", wechatDubboService.getJSSignature(url)).toJson();
        }
        catch (final Exception e) {
            return AjaxObject.newError("获取JSTicket Signature失败").toJson();
        }
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

    /*    *//**
     * 上传文件资料
     *//*
    @RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
    public @ResponseBody String fileUpload(final String fileTypeName, final String fileMediaId) {
        try {
            return AjaxObject.newOk("保存附件成功", wechatDubboService.fileUpload(fileTypeName, fileMediaId)).toJson();
        }
        catch (final Exception e) {
            return AjaxObject.newError("保存附件失败").toJson();
        }
    }

    @RequestMapping(value = "/fileList", method = RequestMethod.POST)
    public @ResponseBody String fileList(final Long batchNo) {
        try {
            return AjaxObject.newOk("查询文件列表成功", wechatDubboService.findCustFiles(batchNo)).toJson();
        }
        catch (final Exception e) {
            return AjaxObject.newError("查询文件列表失败").toJson();
        }
    }

    @RequestMapping(value = "/findEnroll", method = RequestMethod.POST)
    public @ResponseBody String findCustEnroll() {
        try {
            return AjaxObject.newOk("获取开户信息成功", wechatDubboService.findCustEnroll()).toJson();
        }
        catch (final Exception e) {
            return AjaxObject.newError("获取开户信息失败").toJson();
        }
    }*/
}
