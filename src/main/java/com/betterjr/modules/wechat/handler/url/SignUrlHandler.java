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

import com.alibaba.dubbo.config.annotation.Reference;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.modules.agreement.IScfElecAgreementService;
import com.betterjr.modules.agreement.entity.ScfElecAgreement;
import com.betterjr.modules.wechat.dispatcher.UrlControl;

/**
 * @author liuwl
 *
 */
public class SignUrlHandler implements UrlHandler {

    @Reference(interfaceClass = IScfElecAgreementService.class)
    private IScfElecAgreementService agreementService;

    public static final String SIGN_FUNC_CODE = "41";

    // private final ScfRequestService requestService;

    /**
     *
     */
    public SignUrlHandler() {
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

        if (StringUtils.equals(func, SIGN_FUNC_CODE)) {
            final List<String> params = anUrlControl.getParam(UrlControl.FUNC_PARAMS);
            final String requestNo = params.get(0);

            ScfElecAgreement agreement = agreementService.findOneElecAgreement(requestNo);
            if (agreement != null) {
                if ("6".equals(agreement.getAgreeType()) || "7".equals(agreement.getAgreeType())) {
                    anUrlControl.setUrl("./wechat/index.html#/sign/do/" + params.get(0));

                } else {

                    anUrlControl.setUrl("./wechat/flow.html#/flow/todoList");
                }
            } else {
                anUrlControl.setUrl("./wechat/flow.html#/flow/todoList");

            }
        }

        anUrlControl.nextHandler();
    }

}
