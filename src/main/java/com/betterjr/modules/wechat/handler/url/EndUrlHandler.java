// Copyright (c) 2014-2016 Bytter. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2016年9月23日, liuwl, creation
// ============================================================================
package com.betterjr.modules.wechat.handler.url;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.betterjr.modules.wechat.dispatcher.UrlControl;

/**
 * @author liuwl
 *
 */
public class EndUrlHandler implements UrlHandler {

    /*
     * (non-Javadoc)
     *
     * @see com.betterjr.modules.wechat.handler.url.UrlHandler#handle(java.lang.String, com.betterjr.modules.wechat.dispatcher.UrlControl)
     */
    @Override
    public void handle(final String anState, final UrlControl anUrlControl) throws Exception {
        // 结束
        final Subject subject = SecurityUtils.getSubject();
        anUrlControl.setUrl("./wechat/index.html?JSESSIONID=" + subject.getSession().getId() + "#/home");
    }

}
