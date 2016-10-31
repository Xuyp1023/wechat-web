// Copyright (c) 2014-2016 Bytter. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2016年9月23日, liuwl, creation
// ============================================================================
package com.betterjr.modules.wechat.handler.url;

import java.util.List;

import com.betterjr.common.service.SpringContextHolder;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.modules.acceptbill.dubboclient.WeChatScfAcceptBillDubboClientService;
import com.betterjr.modules.wechat.dispatcher.UrlControl;

/**
 * @author liuwl
 *
 */
public class BillUrlHandler implements UrlHandler {
    public static final String BILL_FUNC_CODE = "20";


    public final WeChatScfAcceptBillDubboClientService acceptBillService;

    /**
     *
     */
    public BillUrlHandler() {
        //     billService = SpringContextHolder.getBean(ScfAcceptBillService.class);
        acceptBillService = SpringContextHolder.getBean(WeChatScfAcceptBillDubboClientService.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.betterjr.modules.wechat.handler.func.FuncHandler#handle(java.lang.String)
     */
    @Override
    public void handle(final String anState, final UrlControl anUrlControl) throws Exception {
        final String func = anUrlControl.getParam(UrlControl.FUNC_CODE);

        if (BetterStringUtils.equals(func, BILL_FUNC_CODE)) {
            final List<String> params = anUrlControl.getParam(UrlControl.FUNC_PARAMS);

            if (params.size() == 1) {
                try {
                    final Long billId = Long.valueOf(params.get(0));
                    final String billStatus = acceptBillService.findBillStatus(billId);
                    if (BetterStringUtils.equals(billStatus, "2") == false) {
                        anUrlControl.setUrl("./wechat/index.html#/bill/detail/" + params.get(0));
                    }
                    else {
                        anUrlControl.setUrl("./wechat/index.html#/finance/detail/bill/" + params.get(0));
                    }
                } catch (final Exception e) {
                    anUrlControl.setUrl("./wechat/index.html#/bill");
                }
            }
        }

        anUrlControl.nextHandler();
    }

}
