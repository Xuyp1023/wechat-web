// Copyright (c) 2014-2016 Bytter. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2016年9月23日, liuwl, creation
// ============================================================================
package com.betterjr.modules.wechat.dispatcher;

import java.util.ArrayList;
import java.util.List;

import com.betterjr.modules.wechat.handler.url.BeginUrlHandler;
import com.betterjr.modules.wechat.handler.url.BillUrlHandler;
import com.betterjr.modules.wechat.handler.url.EndUrlHandler;
import com.betterjr.modules.wechat.handler.url.InquiryUrlHandler;
import com.betterjr.modules.wechat.handler.url.MenuUrlHandler;
import com.betterjr.modules.wechat.handler.url.RequestUrlHandler;
import com.betterjr.modules.wechat.handler.url.SignUrlHandler;
import com.betterjr.modules.wechat.handler.url.UrlHandler;

/**
 * @author liuwl
 *
 */
public final class UrlDispatcher {

    private static final List<UrlHandler> SYS_HANDLER = new ArrayList<UrlHandler>();

    static {
        SYS_HANDLER.add(new BeginUrlHandler()); // 先将 state分解

        SYS_HANDLER.add(new MenuUrlHandler()); // 处理菜单
        SYS_HANDLER.add(new BillUrlHandler()); // 处理票据
        SYS_HANDLER.add(new RequestUrlHandler()); // 处理融资申请
        SYS_HANDLER.add(new InquiryUrlHandler()); // 处理报价申请
        SYS_HANDLER.add(new SignUrlHandler()); // 处理报价申请

        SYS_HANDLER.add(new EndUrlHandler());
    }

    /**
     * 地址分发
     * @param anState
     * @return
     */
    public final static String dispatch(final String anState) {
        final UrlControl urlControl = new UrlControl(anState, SYS_HANDLER.iterator());

        urlControl.nextHandler();

        return urlControl.getUrl();
    }

}
