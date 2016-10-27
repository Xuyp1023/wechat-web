// Copyright (c) 2014-2016 Bytter. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2016年9月23日, liuwl, creation
// ============================================================================
package com.betterjr.modules.wechat.handler.url;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.modules.wechat.dispatcher.UrlControl;

/**
 * @author liuwl
 *
 */
public class BeginUrlHandler implements UrlHandler {
    private static final Pattern COMMA_PATTERN = Pattern.compile(","); // 约定通过 ,分割 state

    /*
     * (non-Javadoc)
     *
     * @see com.betterjr.modules.wechat.handler.url.UrlHandler#handle(java.lang.String, com.betterjr.modules.wechat.dispatcher.UrlControl)
     */
    @Override
    public void handle(final String anState, final UrlControl anUrlControl) throws Exception {
        if (BetterStringUtils.isBlank(anState)) {
            final Subject subject = SecurityUtils.getSubject();
            anUrlControl.setUrl("./wechat/index.html#/home");
            return;
        }

        final String[] states = COMMA_PATTERN.split(anState);

        anUrlControl.addParam(UrlControl.FUNC_CODE, states[0]);
        if (states.length > 1) {
            final List<String> params = new ArrayList<>();
            for (int i = 0; i < states.length - 1; i++) {
                params.add(new String(states[i + 1]));
            }

            anUrlControl.addParam(UrlControl.FUNC_PARAMS, params);
        }

        anUrlControl.nextHandler();
    }

}
