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
public class SignUrlHandler implements UrlHandler {
    public static final String SIGN_FUNC_CODE = "41";

    //private final ScfRequestService requestService;

    /**
     *
     */
    public SignUrlHandler() {
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

        if (BetterStringUtils.equals(func, SIGN_FUNC_CODE)) {
            final List<String> params = anUrlControl.getParam(UrlControl.FUNC_PARAMS);
            final String requestNo = params.get(0);
            //anUrlControl.setUrl("./wechat/index.html#/sign/do/"+params.get(0));
            anUrlControl.setUrl("./wechat/flow.html#/flow/todoList");
        }

        anUrlControl.nextHandler();
    }

}
