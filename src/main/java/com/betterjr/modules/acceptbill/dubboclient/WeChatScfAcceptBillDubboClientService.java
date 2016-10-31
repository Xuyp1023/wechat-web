// Copyright (c) 2014-2016 Bytter. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2016年10月31日, liuwl, creation
// ============================================================================
package com.betterjr.modules.acceptbill.dubboclient;

import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.betterjr.modules.acceptbill.IScfAcceptBillService;

/**
 * @author liuwl
 *
 */
@Service
public class WeChatScfAcceptBillDubboClientService {
    @Reference(interfaceClass = IScfAcceptBillService.class)
    private IScfAcceptBillService scfAcceptBillService;

    public String findBillStatus(final Long anId) {
        return scfAcceptBillService.findAcceptBillStatusById(anId);
    }
}
