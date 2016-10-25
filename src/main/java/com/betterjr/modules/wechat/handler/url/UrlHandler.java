// Copyright (c) 2014-2016 Bytter. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2016年9月23日, liuwl, creation
// ============================================================================
package com.betterjr.modules.wechat.handler.url;

import com.betterjr.modules.wechat.dispatcher.UrlControl;

/**
 * @author liuwl
 *
 */
public interface UrlHandler {
    void handle(final String anState, final UrlControl anUrlControl) throws Exception;
}
