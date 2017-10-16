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
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.modules.wechat.dispatcher.UrlControl;

/**
 * @author liuwl
 *
 */
public class MenuUrlHandler implements UrlHandler {
    public static final String MENU_FUNC_CODE = "10";

    /*    private static final String MENU_REGISTER = "1";
    private static final String MENU_ACCOUNT = "2";
    private static final String MENU_BILL = "3";
    private static final String MENU_PRODUCT = "4";
    private static final String MENU_FINANCE = "5";
    private static final String MENU_HOME = "6";
    private static final String MENU_FACTOR = "9";
    private static final String MENU_RELATION = "10";*/

    private static final String MENU_FINANCING = "1";
    private static final String MENU_INVESTMENT = "2";
    private static final String MENU_SELFINFO = "3";

    /*
     * (non-Javadoc)
     *
     * @see com.betterjr.modules.wechat.handler.url.UrlHandler#handle(java.lang.String)
     */
    @Override
    public void handle(final String anState, final UrlControl anUrlControl) throws Exception {
        final String func = anUrlControl.getParam(UrlControl.FUNC_CODE);

        if (StringUtils.equals(func, MENU_FUNC_CODE)) {
            final List<String> params = anUrlControl.getParam(UrlControl.FUNC_PARAMS);
            final Subject subject = SecurityUtils.getSubject();

            if (params.size() == 1) {
                /*switch (params.get(0)) {
                case MENU_REGISTER:
                    anUrlControl.setUrl("./wechat/index.html#/register/basic");
                    break;
                case MENU_ACCOUNT:
                    anUrlControl.setUrl("./wechat/index.html#/register/detail");
                    break;
                case MENU_BILL:
                    anUrlControl.setUrl("./wechat/index.html#/bill");
                    break;
                case MENU_PRODUCT:
                    anUrlControl.setUrl("./wechat/index.html#/product");
                    break;
                case MENU_FINANCE:
                    anUrlControl.setUrl("./wechat/index.html#/finance");
                    break;
                case MENU_HOME:
                    anUrlControl.setUrl("./wechat/index.html#/home");
                    break;
                case MENU_FACTOR:
                    anUrlControl.setUrl("./wechat/index.html#/register/factor");
                    break;
                case MENU_RELATION:
                    anUrlControl.setUrl("./wechat/index.html#/relation/manage");
                    break;
                }*/
                switch (params.get(0)) {
                case MENU_FINANCING:
                    anUrlControl.setUrl("./wechat/index.html#/home/finance");
                    break;
                case MENU_INVESTMENT:
                    anUrlControl.setUrl("./wechat/index.html#/home/invest");
                    break;
                case MENU_SELFINFO:
                    anUrlControl.setUrl("./wechat/index.html#/home/single");
                    break;
                }
            }
        }

        anUrlControl.nextHandler();
    }

}
