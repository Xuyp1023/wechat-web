package com.betterjr.modules.sys.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
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
    private static final Map<String, String> urlMap = createURLMap();

    private static Map createURLMap(){
        final Map<String, String> data = new HashMap();
        data.put("1", "/scf/app/account/register.html?state=1");
        data.put("2", "/scf/app/account/register.html?state=2");
        data.put("3", "/scf/app/account/register.html?state=3");
        data.put("4", "/scf/app/account/register.html?state=4");
        data.put("5", "/scf/app/account/register.html?state=5");
        data.put("6", "/scf/app/account/register.html?state=6");
        return data;
    }

    @Override
    protected AuthenticationToken createToken(final ServletRequest request, final ServletResponse response) {
        final HttpServletRequest httpRequest = (HttpServletRequest) request;
        final String ticket = httpRequest.getParameter(TICKET_PARAMETER);

        String tmpIp;
        if (request instanceof HttpServletRequest) {
            final HttpServletRequest workRequest = (HttpServletRequest) request;
            tmpIp = Servlets.getRemoteAddr(workRequest);
        }
        else {
            tmpIp = "";
        }
        final String username = SignHelper.randomBase64(20);
        final String password = "1X2Y3W4o5m6";

        final BetterjrWechatToken token = new BetterjrWechatToken(ticket, username, password, tmpIp );

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
    protected boolean onLoginFailure(final AuthenticationToken token, final AuthenticationException ae, final ServletRequest request, final ServletResponse response) {

        final Subject subject = getSubject(request, response);
        if (subject.isAuthenticated() || subject.isRemembered()) {
            try {
                issueSuccessRedirect(request, response);
            }
            catch (final Exception e) {
                logger.error("Cannot redirect to the default success url", e);
            }
        }
        else {
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

    public static String findWorkUrl(final String anKey){

        return urlMap.get(anKey);
    }

    @Override
    protected void issueSuccessRedirect(final ServletRequest request, final ServletResponse response) throws Exception {
        String tmpKey = request.getParameter("state");
        if (StringUtils.isBlank(tmpKey)){
            tmpKey = getSuccessUrl();
        }
        else{
            tmpKey = urlMap.get(tmpKey);
            if (StringUtils.isBlank(tmpKey)){
                tmpKey = getSuccessUrl();
            }
        }

        // 检查当前用户
        final CustWeChatDubboClientService wechatClientService = SpringContextHolder.getBean(CustWeChatDubboClientService.class);
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();
        if (wechatClientService.checkFristLogin(operator.getId())) {
            tmpKey = "/static/wechat/frist.html";
        }


        WebUtils.redirectToSavedRequest(request, response, tmpKey);
    }

    @Override
    protected boolean onLoginSuccess(final AuthenticationToken token, final Subject subject, final ServletRequest request, final ServletResponse response) throws Exception {
        System.out.println("this onLoginSuccess");
        System.out.println(subject);
        super.onLoginSuccess(token, subject, request, response);
        return true;
    }

}