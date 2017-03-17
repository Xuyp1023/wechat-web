// Copyright (c) 2014-2017 Bytter. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2017年3月15日, liuwl, creation
// ============================================================================
package com.betterjr.modules.credit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcException;
import com.betterjr.common.exception.BytterException;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.common.web.AjaxObject;

/**
 * @author liuwl
 *
 */
@Controller
@RequestMapping(value = "/Wechat/Scf/Credit")
public class WechatCreditController {
    private static final Logger logger = LoggerFactory.getLogger(WechatCreditController.class);

    @Reference(interfaceClass = IScfCreditService.class)
    private IScfCreditService scfCreditService;

    @RequestMapping(value = "/findCreditSum", method = RequestMethod.POST)
    public @ResponseBody String findCreditSum() {
        try {
            return scfCreditService.webFindCreditSumByCustNo(UserUtils.getDefCustInfo().getCustNo());
        }
        catch (final RpcException e) {
            logger.error(e.getMessage(), e);
            if (BytterException.isCauseBytterException(e)) {
                return AjaxObject.newError(e.getCause().getMessage()).toJson();
            }
            return AjaxObject.newError("授信额度信息查询失败").toJson();
        }
        catch (final Exception e) {
            logger.error(e.getMessage(), e);
            return AjaxObject.newError("授信额度信息查询失败").toJson();
        }
    }

}
