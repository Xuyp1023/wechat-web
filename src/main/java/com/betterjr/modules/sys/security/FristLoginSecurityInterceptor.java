// Copyright (c) 2014-2017 Bytter. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2017年2月10日, liuwl, creation
// ============================================================================
package com.betterjr.modules.sys.security;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.betterjr.common.data.UserType;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.wechat.dubboclient.CustWeChatDubboClientService;

/**
 * @author liuwl
 *
 */
public class FristLoginSecurityInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(FristLoginSecurityInterceptor.class);

    @Inject
    private CustWeChatDubboClientService wechatClientService;

    private List<String> excludedUrls = new ArrayList<>();

    public List<String> getExcludedUrls() {
        return excludedUrls;
    }

    public void setExcludedUrls(final List<String> excludedUrls) {
        this.excludedUrls = excludedUrls;
    }

    /**
     * 检查是否是例外的URI地址
     *
     * @param requestUri
     *            申请地址
     * @return
     */
    private boolean checkExcludeUrl(final String requestUri) {
        logger.info("requestUri = " + requestUri);
        for (final String url : excludedUrls) {
            if (requestUri.endsWith(url)) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.web.servlet.HandlerInterceptor#preHandle(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, java.lang.Object)
     */
    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response,
            final Object anHandler) throws Exception {
        final Long startTime = System.currentTimeMillis();
        request.setAttribute("startTime_fristLogin", startTime);

        boolean result = true;
        if ((anHandler != null) && (anHandler instanceof HandlerMethod)) {
            if (checkExcludeUrl(request.getRequestURI())) {
                return true;
            }

            final ShiroUser shiroUser = UserUtils.getPrincipal();
            if (shiroUser.getUserType().equals(UserType.NONE_USER)) {
                result = true;
            } else {
                final CustOperatorInfo operator = UserUtils.getOperatorInfo();
                if (operator != null) {
                    result = wechatClientService.checkFristLogin(operator.getId());
                } else {
                    result = true;
                }
            }

            if (result) {
                response.setContentType("application/json");
                response.getWriter().write(AjaxObject.newFristLogin("首次登陆,请验证交易密码！").toJson());
                return false;
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.web.servlet.HandlerInterceptor#postHandle(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.web.servlet.ModelAndView)
     */
    @Override
    public void postHandle(final HttpServletRequest request, final HttpServletResponse response, final Object anHandler,
            final ModelAndView anModelAndView) throws Exception {
        final Long startTime = (Long) request.getAttribute("startTime_fristLogin");
        if (startTime != null) {
            request.removeAttribute("startTime_fristLogin");
            final Long endTime = System.currentTimeMillis();
            logger.info("FristLoginSecurityInterceptor handlingTime :" + (endTime - startTime));
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.web.servlet.HandlerInterceptor#afterCompletion(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse, java.lang.Object, java.lang.Exception)
     */
    @Override
    public void afterCompletion(final HttpServletRequest anRequest, final HttpServletResponse anResponse,
            final Object anHandler, final Exception anEx) throws Exception {

    }

}
