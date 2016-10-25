// Copyright (c) 2014-2016 Bytter. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2016年9月23日, liuwl, creation
// ============================================================================
package com.betterjr.modules.wechat.dispatcher;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.modules.wechat.handler.url.UrlHandler;

/**
 * @author liuwl
 *
 */
public final class UrlControl {
    private static final Logger logger = LoggerFactory.getLogger(UrlControl.class);

    public static final String FUNC_CODE = "FUNC_CODE";
    public static final String FUNC_PARAMS = "FUNC_PARAMS";

    public UrlControl(final String anState, final Iterator<UrlHandler> anUhandlerIterator) {
        state = anState;
        uhandlerIterator = anUhandlerIterator;
    }

    private final Iterator<UrlHandler> uhandlerIterator;

    private final Map<String, Object> context = new HashMap<>();

    private String url;

    private final String state;

    public <T extends Object> void addParam(final String anKey, final T anValue) {
        context.put(anKey, anValue);
    }

    @SuppressWarnings("unchecked")
    public  <T extends Object> T getParam(final String anKey) {
        return (T)context.get(anKey);
    }

    public void setUrl(final String anUrl) {
        this.url = anUrl;
    }

    public String getUrl() {
        return url;
    }

    public final void nextHandler() {
        if (BetterStringUtils.isNotBlank(url)) {
            logger.debug("wechat dispatcher url: " + url);
            return;
        }
        if (uhandlerIterator.hasNext()) {
            try {
                uhandlerIterator.next().handle(state, this);
            } catch (final Exception e) {
            }
        }
    }
}
