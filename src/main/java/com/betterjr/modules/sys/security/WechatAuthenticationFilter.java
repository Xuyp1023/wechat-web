package com.betterjr.modules.sys.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.betterjr.common.security.SignHelper;
import com.betterjr.common.service.SpringContextHolder;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.common.web.Servlets;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.wechat.dubboclient.CustWeChatDubboClientService;

public class WechatAuthenticationFilter extends BaseFormAuthenticationFilter {

    private static Logger logger = LoggerFactory.getLogger(WechatAuthenticationFilter.class);

    private static final String TICKET_PARAMETER = "code";

    private String failureUrl;

    @Override
    protected boolean preHandle(final ServletRequest anRequest, final ServletResponse anResponse) throws Exception {
        final ShiroUser shiroUser = UserUtils.getPrincipal();
        logger.info("wechat --- preHandle -- user:" + (shiroUser == null ? "null" : shiroUser.getUserType()));
        return super.preHandle(anRequest, anResponse);
    }

    @Override
    protected void postHandle(final ServletRequest anRequest, final ServletResponse anResponse) throws Exception {
        super.postHandle(anRequest, anResponse);
        final ShiroUser shiroUser = UserUtils.getPrincipal();
        logger.info("wechat --- postHandle -- user:" + (shiroUser == null ? "null" : shiroUser.getUserType()));
    }

    @Override
    public void doFilterInternal(final ServletRequest anRequest, final ServletResponse anResponse,
            final FilterChain anChain) throws ServletException, IOException {
        super.doFilterInternal(anRequest, anResponse, anChain);
        final ShiroUser shiroUser = UserUtils.getPrincipal();
        logger.info("wechat --- doFilterInternal -- user:" + (shiroUser == null ? "null" : shiroUser.getUserType()));
    }

    @Override
    protected AuthenticationToken createToken(final ServletRequest request, final ServletResponse response) {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final String ticket = httpRequest.getParameter(TICKET_PARAMETER);

        String tmpIp;
        if (request instanceof HttpServletRequest) {
            final HttpServletRequest workRequest = (HttpServletRequest) request;
            tmpIp = Servlets.getRemoteAddr(workRequest);
        } else {
            tmpIp = "";
        }
        final String username = SignHelper.randomBase64(20);
        final String password = "1X2Y3W4o5m6";

        final BetterjrWechatToken token = new BetterjrWechatToken(ticket, username, password, tmpIp);

        return token;
    }

    @Override
    protected boolean onAccessDenied(final ServletRequest request, final ServletResponse response) throws Exception {
        logger.error("this is onAccessDenied");
        if (request instanceof HttpServletRequest) {
            final HttpServletRequest workRequest = (HttpServletRequest) request;
            logger.info("onAccessDenied this request Session ID = " + workRequest.getSession().getId());
        }
        // WebUtils.issueRedirect(request, response, failureUrl);
        final AuthenticationToken token = createToken(request, response);
        getSubject(request, response).login(token);
        return true;
    }

    @Override
    protected boolean onLoginFailure(final AuthenticationToken token, final AuthenticationException ae,
            final ServletRequest request, final ServletResponse response) {

        final Subject subject = getSubject(request, response);
        if (subject.isAuthenticated() || subject.isRemembered()) {
            try {
                issueSuccessRedirect(request, response);
            }
            catch (final Exception e) {
                logger.error("Cannot redirect to the default success url", e);
            }
        } else {
            try {
                WebUtils.issueRedirect(request, response, failureUrl);
            }
            catch (final IOException e) {
                logger.error("Cannot redirect to failure url : {}", failureUrl, e);
            }
        }
        return true;
    }

    public void setFailureUrl(final String failureUrl) {
        this.failureUrl = failureUrl;
    }

    @Override
    protected void issueSuccessRedirect(final ServletRequest request, final ServletResponse response) throws Exception {
        String tmpKey = request.getParameter("state");
        tmpKey = getSuccessUrl();
        // 检查当前用户
        final CustWeChatDubboClientService wechatClientService = SpringContextHolder
                .getBean(CustWeChatDubboClientService.class);
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();
        if (wechatClientService.checkFristLogin(operator.getId())) {
            tmpKey = "/static/wechat/frist.html";
        }

        WebUtils.redirectToSavedRequest(request, response, tmpKey);
    }

    @Override
    protected boolean onLoginSuccess(final AuthenticationToken token, final Subject subject,
            final ServletRequest request, final ServletResponse response) throws Exception {
        System.out.println("this onLoginSuccess");
        System.out.println(subject);
        super.onLoginSuccess(token, subject, request, response);
        return true;
    }

}