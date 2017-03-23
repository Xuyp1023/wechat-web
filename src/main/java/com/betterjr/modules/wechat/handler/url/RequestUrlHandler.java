// Copyright (c) 2014-2016 Bytter. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2016年9月23日, liuwl, creation
// ============================================================================
package com.betterjr.modules.wechat.handler.url;

import java.util.List;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.modules.wechat.dispatcher.UrlControl;

/**
 * @author liuwl
 *
 */
public class RequestUrlHandler implements UrlHandler {
    public static final String REQUEST_FUNC_CODE = "30";

    //private final ScfRequestService requestService;

    /**
     *
     */
    public RequestUrlHandler() {
        //    requestService = SpringContextHolder.getBean(ScfRequestService.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.betterjr.modules.wechat.handler.func.FuncHandler#handle(java.lang.String)
     */
    @Override
    public void handle(final String anState, final UrlControl anUrlControl) throws Exception {
        final String func = anUrlControl.getParam(UrlControl.FUNC_CODE);

        if (BetterStringUtils.equals(func, REQUEST_FUNC_CODE)) {
            final List<String> params = anUrlControl.getParam(UrlControl.FUNC_PARAMS);
            final String requestNo = params.get(0);// ?JSESSIONID=" + subject.getSession().getId() + "
            //final ScfRequest request = requestService.findByRequestNo(requestNo);
            final Subject subject = SecurityUtils.getSubject();
            //anUrlControl.setUrl("./wechat/index.html#/finance/detail/finance/" + params.get(0));
            anUrlControl.setUrl("./wechat/index.html#/financeBusi/detail/"+params.get(0));
        }

        anUrlControl.nextHandler();
    }

}
