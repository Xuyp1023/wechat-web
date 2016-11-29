package com.betterjr.modules.customer;

import static com.betterjr.common.web.ControllerExceptionHandler.exec;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.common.web.Servlets;

/**
 * 代录接口
 * 
 * @author wudy
 */
@Controller
@RequestMapping("/Wechat/Platform/Instead2")
public class WechatInsteadController2 {

    private static final Logger logger = LoggerFactory.getLogger(WechatInsteadController2.class);

    @Reference(interfaceClass = ICustInsteadService2.class)
    private ICustInsteadService2 insteadService;
    @Reference(interfaceClass = ICustInsteadService.class)
    private ICustInsteadService oldInsteadService;

    
    /**
     * 微信代录申请-申请代录(检查短信校验码)
     */
    @RequestMapping(value = "/wechatAddInsteadApply", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String wechatAddInsteadApply(HttpServletRequest request, final Long id, final String fileList) {
        final Object openIdObj = Servlets.getSession().getAttribute("wechat_openId");
        if (openIdObj != null) {
            Map<String, Object> anMap = Servlets.getParametersStartingWith(request, "");
            return exec(() -> insteadService.webWechatAddInsteadApply(anMap, id, fileList), "申请代录出错", logger);
        }
        else {
            return AjaxObject.newError("申请代录失败,请重新点击开户尝试").toJson();
        }
    }
    
    /**
     * 查询代录申请
     */
    @RequestMapping(value = "/findInsteadApplyByAccountTmpId", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String findInsteadApplyByAccountTmpId(final Long id) {
        return exec(() -> insteadService.webFindInsteadApplyByAccountTmpId(id), "查询代录申请出错", logger);
    }
    
    /**
     * 代录项目 确认通过
     * @return
     */
    @RequestMapping(value = "/confirmPassInsteadRecord", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String confirmPassInsteadRecord(final Long id, final String reason) {
        logger.debug("代录项目 确认通过 入参:id=" + id + " reason=" + reason);
        return exec(() -> oldInsteadService.webConfirmPassInsteadRecord(id, reason), "代录项目 确认通过 出错", logger);
    }
    
}