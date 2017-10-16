package com.betterjr.modules.sys.security;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.betterjr.common.data.CustPasswordType;
import com.betterjr.common.data.SimpleDataEntity;
import com.betterjr.common.data.UserType;
import com.betterjr.common.security.SecurityConstants;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.Digests;
import com.betterjr.common.utils.Encodes;
import com.betterjr.common.web.Servlets;
import com.betterjr.modules.account.data.CustContextInfo;
import com.betterjr.modules.account.dubboclient.CustLoginDubboClientService;
import com.betterjr.modules.account.dubboclient.CustOperatorDubboClientService;
import com.betterjr.modules.account.dubboclient.CustPassDubboClientService;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.cert.dubboclient.CustCertDubboClientService;
import com.betterjr.modules.cert.entity.CustCertInfo;
import com.betterjr.modules.wechat.data.api.AccessToken;
import com.betterjr.modules.wechat.data.api.Follower;
import com.betterjr.modules.wechat.dubboclient.CustWeChatDubboClientService;
import com.betterjr.modules.wechat.entity.CustWeChatInfo;
import com.betterjr.modules.wechat.util.WechatDefHandler;
import com.betterjr.modules.wechat.util.WechatKernel;

public class WechatAuthorizingRealm extends AuthorizingRealm {
    private static final Logger logger = LoggerFactory.getLogger(WechatAuthorizingRealm.class);

    private static final int INTERATIONS = 1024;
    private static final int SALT_SIZE = 20;
    private static final String ALGORITHM = "SHA-256";

    private CustCertDubboClientService certService;

    private CustLoginDubboClientService userService;

    private CustOperatorDubboClientService operatorService;

    private CustPassDubboClientService passService;

    private CustWeChatDubboClientService wechatService;

    /**
     * 给ShiroDbRealm提供编码信息，用于密码密码比对 描述
     */
    public WechatAuthorizingRealm() {
        super();
        final HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(ALGORITHM);
        matcher.setHashIterations(INTERATIONS);

        setCredentialsMatcher(matcher);
    }

    @Override
    public boolean supports(final AuthenticationToken token) {

        return true;
    }

    /**
     * 认证回调函数, 登录时调用.
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(final AuthenticationToken authcToken)
            throws AuthenticationException {

        CustOperatorInfo user = null;
        String passWD = null;
        String saltStr = null;
        CustContextInfo contextInfo = null;
        boolean mobileLogin = false;
        logger.warn("this work for doGetAuthenticationInfo");
        List<SimpleDataEntity> userPassData = null;
        try {
            CustCertInfo certInfo = null;
            if ((authcToken instanceof BetterjrWechatToken) == true) {
                final BetterjrWechatToken wechatToken = (BetterjrWechatToken) authcToken;
                final WechatKernel wk = new WechatKernel(wechatService.getMpAccount(),
                        new WechatDefHandler(wechatService), new HashMap<>());

                final AccessToken at = findAccessToken(wk, wechatToken);
                saltStr = "985a44369b063938a6a7";
                passWD = "8438d772e1eac7d8e57aecaae5fb0b8c2e369283cbe31857d89dc87430160a2b";
                mobileLogin = true;
                if (at != null) {
                    final Map<String, Object> mapResult = wechatService.saveLogin(at);
                    final Object operator = mapResult.get("operator");
                    final Object message = mapResult.get("message");
                    if (operator != null && operator instanceof CustOperatorInfo) {
                        user = (CustOperatorInfo) operator;
                    }
                    if (user == null) {
                        final Follower follower = wechatService.findFollower(at.getOpenId());
                        final CustWeChatInfo weChatInfo = wechatService.findWechatUserByOpenId(at.getOpenId());
                        if (follower != null) { // 必须找到这个订阅用户
                            if (weChatInfo == null) {
                                wechatService.saveNewWeChatInfo(wechatService.getAppId(), at.getOpenId(),
                                        follower.getSubscribe());
                            } else {
                                weChatInfo.setSubscribeStatus(String.valueOf(follower.getSubscribe()));
                                wechatService.saveWeChatInfo(weChatInfo);
                            }
                        } else {
                            if (weChatInfo != null) {
                                weChatInfo.setSubscribeStatus("0");
                                wechatService.saveWeChatInfo(weChatInfo);
                            }
                        }
                        final UserType ut = UserType.NONE_USER;
                        // 构造匿名用户
                        final ShiroUser shiroUser = new ShiroUser(ut, 0L, "1X2Y3W4o5m6", user, null, null, mobileLogin,
                                null, userPassData);
                        shiroUser.addParam("accessToken", at);
                        final byte[] salt = Encodes.decodeHex(saltStr);

                        logger.info(
                                "wechat --- 构建匿名用户 -- user:" + (shiroUser == null ? "null" : shiroUser.getUserType()));
                        return new SimpleAuthenticationInfo(shiroUser, passWD, ByteSource.Util.bytes(salt), getName());
                    } else {
                        contextInfo = userService.saveFormLogin(user);
                        certInfo = certService.findFirstCertInfoByOperOrg(user.getOperOrg());
                        wechatToken.setUsername(user.getName());

                        userPassData = passService.findPassAndSalt(user.getId(),
                                new String[] { CustPasswordType.PERSON_TRADE.getPassType(),
                                        CustPasswordType.ORG_TRADE.getPassType() });
                        if (user.getStatus().equals("1") == false) {
                            throw new DisabledAccountException("操作员被要求暂停业务或者已经被注销");
                        }

                        UserType ut = UserType.ORG_USER;
                        // 如果是默认操作员，则是管理员
                        if (user.getDefOper() != null && user.getDefOper()) {
                            ut = UserType.OPERATOR_ADMIN;
                        }

                        final ShiroUser shiroUser = new ShiroUser(ut, user.getId(), user.getName(), user, null,
                                certInfo, mobileLogin, contextInfo, userPassData);
                        shiroUser.addParam("accessToken", at);
                        final byte[] salt = Encodes.decodeHex(saltStr);

                        logger.info(
                                "wechat --- 正常登陆用户 -- user:" + (shiroUser == null ? "null" : shiroUser.getUserType()));
                        return new SimpleAuthenticationInfo(shiroUser, passWD, ByteSource.Util.bytes(salt), getName());
                    }
                }
            }
            logger.info("非微信账号访问系统");
            // 非微信账号登陆
            return null;
        }
        catch (final Exception ex) {
            logger.error("登陆发生错误", ex);
            return null;
        }

    }

    /**
     * @param anWk
     * @return
     */
    private AccessToken findAccessToken(final WechatKernel anWk, final BetterjrWechatToken anWechatToken) {
        final String sysMode = wechatService.getSysMode();
        AccessToken at = null;
        switch (sysMode) {
        case "dev":
            at = createTestAccessToken(anWechatToken);
            break;
        default:
            at = anWk.findUserAuth2(anWechatToken.getTicket());
        }

        return at;
    }

    /**
     * 测试账号
     *
     * @param anWechatToken
     * @return
     */
    private AccessToken createTestAccessToken(final BetterjrWechatToken anWechatToken) {
        final AccessToken accessToken = new AccessToken();
        final String code = anWechatToken.getTicket();
        switch (code) {
        case "1"://
            accessToken.setOpenId("oqJfawK1kv285J87PvYDIDyJGZIY");
            break;
        case "2":
            accessToken.setOpenId("oqJfawF9wTPihEnGXIhT98M7-g0A");
            break;
        }
        return accessToken;
    }

    private CustCertInfo checkValid(final X509Certificate anCert) {
        final CustCertInfo certInfo = certService.checkValidity(anCert);
        Servlets.getSession().setAttribute(SecurityConstants.CUST_CERT_INFO, certInfo);
        // AccessClientImpl.set(certInfo);
        return certInfo;
    }

    protected CustContextInfo formLogin(final CustOperatorInfo custOperatorInfo) {
        final String token = Servlets.getSession().getId();
        final CustContextInfo contextInfo = new CustContextInfo(token, null, null);
        CustContextInfo.putCustContextInfo(contextInfo);
        final CustOperatorInfo tmpInfo = custOperatorInfo;
        // tmpInfo.setName("欧尼");
        // tmpInfo.setOperCode("9857");
        // tmpInfo.setOperOrg("aXXAAAFWQWEQWEQWEEQWEXXXXddpppp");
        tmpInfo.initStatus();
        contextInfo.setOperatorInfo(tmpInfo);
        final List<CustInfo> custList = new ArrayList();
        contextInfo.login(custList);

        // 增加交易账户信息
        contextInfo.addTradeAccount(new ArrayList());

        // todo;登录信息和状态暂时不处理
        return contextInfo;
    }

    /**
     * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用.
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(final PrincipalCollection principals) {
        logger.warn("this work for doGetAuthorizationInfo 1231");
        final Collection<?> collection = principals.fromRealm(getName());
        if (Collections3.isEmpty(collection)) {
            return null;
        }

        final ShiroUser shiroUser = (ShiroUser) collection.iterator().next();

        final SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        if (UserType.NONE_USER.equals(shiroUser.getUserType()) == true) {
            info.addRole("NONE_USER"); // 匿名用户
        } else {
            info.addRole("NORM_USER"); // 普通用户
            for (final String userRule : shiroUser.fingUserRule()) {
                logger.warn("this use attach rule is :" + userRule);
                info.addRole(userRule);
            }
        }

        return info;
    }

    public static class HashPassword {
        public String salt;
        public String password;
    }

    public static HashPassword encrypt(final String plainText) {
        final HashPassword result = new HashPassword();
        final byte[] salt = Digests.generateSalt(SALT_SIZE);
        result.salt = Encodes.encodeHex(salt);

        final byte[] hashPassword = Digests.sha256(plainText.getBytes(), salt, INTERATIONS);
        result.password = Encodes.encodeHex(hashPassword);

        return result;
    }

    public static String findEncrypt(final String plainText, final String anSalt) {
        final byte[] salt = Encodes.decodeHex(anSalt);
        final byte[] hashPassword = Digests.sha256(plainText.getBytes(), salt, INTERATIONS);

        return Encodes.encodeHex(hashPassword);
    }

    /**
     * 更新用户授权信息缓存.
     */
    public void clearCachedAuthorizationInfo(final String principal) {
        final SimplePrincipalCollection principals = new SimplePrincipalCollection(principal, getName());
        clearCachedAuthorizationInfo(principals);
    }

    /**
     * 清除所有用户授权信息缓存.
     */
    public void clearAllCachedAuthorizationInfo() {
        final Cache<Object, AuthorizationInfo> cache = getAuthorizationCache();
        if (cache != null) {
            for (final Object key : cache.keys()) {
                cache.remove(key);
            }
        }
    }

}
