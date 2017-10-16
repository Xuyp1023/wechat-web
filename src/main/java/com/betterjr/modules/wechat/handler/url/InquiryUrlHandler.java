// Copyright (c) 2014-2016 Bytter. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2016年9月23日, liuwl, creation
// ============================================================================
package com.betterjr.modules.wechat.handler.url;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.modules.wechat.dispatcher.UrlControl;

/**
 * @author liuwl
 *
 */
public class InquiryUrlHandler implements UrlHandler {
    public static final String INQUIRY_FUNC_CODE = "40";

    // private final ScfRequestService requestService;

    /**
     *
     */
    public InquiryUrlHandler() {
        // requestService = SpringContextHolder.getBean(ScfRequestService.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.betterjr.modules.wechat.handler.func.FuncHandler#handle(java.lang.String)
     */
    @Override
    public void handle(final String anState, final UrlControl anUrlControl) throws Exception {
        final String func = anUrlControl.getParam(UrlControl.FUNC_CODE);

        if (StringUtils.equals(func, INQUIRY_FUNC_CODE)) {
            final List<String> params = anUrlControl.getParam(UrlControl.FUNC_PARAMS);
            anUrlControl.setUrl("./wechat/index.html#/inquiry/detail/" + params.get(0));
        }

        anUrlControl.nextHandler();
    }

}
