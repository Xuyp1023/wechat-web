// Copyright (c) 2014-2017 Bytter. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.2 : 2017年2月27日, liuwl, creation
// ============================================================================
package com.betterjr.modules.operator.controller;

import static com.betterjr.common.web.ControllerExceptionHandler.exec;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.account.dubbo.interfaces.ICustInfoService;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.operator.dubboclient.OperatorDubboClientService;

/**
 * @author liuwl
 *
 */
@Controller
@RequestMapping(path = "/Wechat/Operator")
public class OperatorController {
    private static final Logger logger = LoggerFactory.getLogger(OperatorController.class);

    @Autowired
    private OperatorDubboClientService custOperatorService;

    @Reference(interfaceClass=ICustInfoService.class)
    private ICustInfoService custInfoService;

    /**
     * 取当前用户名，当前用户对应公司名
     *
     * @param anMap
     * @return
     */
    @RequestMapping(value = "/findOperCustInfo", method = RequestMethod.POST, produces="application/json")
    public @ResponseBody String findOperCustInfo(final HttpServletRequest request) {
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();
        if (operator == null) {
            return AjaxObject.newError("没有找到登陆用户").toJson();
        }
        final Collection<CustInfo> custInfos = custInfoService.queryCustInfo();
        final Map<String, Object> info = new HashMap<>();
        info.put("operator", operator);
        info.put("custInfo", Collections3.getFirst(custInfos));

        return AjaxObject.newOk("用户信息查询成功", info).toJson();
    }


    /**
     * 取当前用户信息
     *
     * @param anMap
     * @return
     */
    @RequestMapping(value = "/findOperatorInfo", method = RequestMethod.POST, produces="application/json")
    public @ResponseBody String findOperatorInfo(final HttpServletRequest request) {
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();
        if (operator == null) {
            return AjaxObject.newError("没有获取登陆用户").toJson();
        }
        final Long operId = operator.getId();
        return exec(() -> custOperatorService.webFindOperatorById(operId), "当前用户信息查询  出错", logger);
    }
}
