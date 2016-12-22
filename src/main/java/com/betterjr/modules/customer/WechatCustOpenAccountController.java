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
import com.alibaba.dubbo.rpc.RpcException;
import com.betterjr.common.exception.BytterException;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.common.web.Servlets;

@Controller
@RequestMapping("/Wechat/Platform/Account")
public class WechatCustOpenAccountController {

    @Reference(interfaceClass = ICustOpenAccountService.class)
    private ICustOpenAccountService custOpenAccountService;


    private static final Logger logger = LoggerFactory.getLogger(WechatCustOpenAccountController.class);

    /**
     * 检查申请机构名称是否存在
     */
    @RequestMapping(value = "/checkCustExistsByCustName", method = RequestMethod.POST)
    public @ResponseBody String checkCustExistsByCustName(String custName) {
        logger.info("检查申请机构名称是否存在,入参: " + custName);
        return exec(() -> custOpenAccountService.webCheckCustExistsByCustName(custName), "检查申请机构名称是否存在失败", logger);
    }
    
    /**
     * 检查组织机构代码证是否存在
     */
    @RequestMapping(value = "/checkCustExistsByIdentNo", method = RequestMethod.POST)
    public @ResponseBody String checkCustExistsByIdentNo(String identNo){
        logger.info("检查组织机构代码证是否存在,入参: " + identNo);
        return exec(() -> custOpenAccountService.webCheckCustExistsByIdentNo(identNo), "检查组织机构代码证是否存在失败", logger);
    }
    
    /**
     * 检查营业执照号码是否存在
     */
    @RequestMapping(value = "/checkCustExistsByBusinLicence", method = RequestMethod.POST)
    public @ResponseBody String checkCustExistsByBusinLicence(String businLicence) {
        logger.info("检查营业执照号码是否存在,入参: " + businLicence);
        return exec(() -> custOpenAccountService.webCheckCustExistsByBusinLicence(businLicence), "检查营业执照号码是否存在失败", logger);
    }
    
    /**
     * 检查银行账号是否存在
     */
    @RequestMapping(value = "/checkCustExistsByBankAccount", method = RequestMethod.POST)
    public @ResponseBody String checkCustExistsByBankAccount(String bankAccount) {
        logger.info("检查银行账号是否存在,入参: " + bankAccount);
        return exec(() -> custOpenAccountService.webCheckCustExistsByBankAccount(bankAccount), "检查银行账号是否存在失败", logger);
    }
    
    /**
     * 检查电子邮箱是否存在
     */
    @RequestMapping(value = "/checkCustExistsByEmail", method = RequestMethod.POST)
    public @ResponseBody String checkCustExistsByEmail(String email) {
        logger.info("检查电子邮箱是否存在,入参: " + email);
        return exec(() -> custOpenAccountService.webCheckCustExistsByEmail(email), "检查电子邮箱是否存在失败", logger);
    }
    
    /**
     * 检查银行账号是否存在
     */
    @RequestMapping(value = "/checkCustExistsByMobileNo", method = RequestMethod.POST)
    public @ResponseBody String checkCustExistsByMobileNo(String mobileNo) {
        logger.info("检查手机号码是否存在,入参: " + mobileNo);
        return exec(() -> custOpenAccountService.webCheckCustExistsByMobileNo(mobileNo), "检查手机号码是否存在失败", logger);
    }
    
    /**
     * 客户开户资料暂存
     */
    @RequestMapping(value = "/saveAccInfo", method = RequestMethod.POST)
    public @ResponseBody String saveOpenAccountInfo(HttpServletRequest request, Long id, String fileList) {
        Map<String, Object> anMap = Servlets.getParametersStartingWith(request, "");
        final Object openIdObj = Servlets.getSession().getAttribute("wechat_openId");
        if (openIdObj != null) {
            final String openId = String.valueOf(openIdObj);
            anMap.put("wechatOpenId", openId);
            logger.info("客户开户资料暂存,入参：" + anMap.toString());
            return exec(() -> custOpenAccountService.webSaveOpenAccountInfo(anMap, id, fileList), "暂存失败", logger);
        }
        return AjaxObject.newError("开户失败").toJson();
    }
    
    /**
     * 微信查询开户资料
     */
    @RequestMapping(value = "/findAccountTmpInfo", method = RequestMethod.POST)
    public @ResponseBody String findAccountTmpInfo() {
        try {
            final Object openIdObj = Servlets.getSession().getAttribute("wechat_openId");
            if (openIdObj != null) {
                final String openId = String.valueOf(openIdObj);
                return custOpenAccountService.webFindAccountTmpInfo(openId);
            }
            return AjaxObject.newError("获取开户信息失败").toJson();
        }
        catch (final Exception e) {
            return AjaxObject.newError("获取开户信息失败").toJson();
        }
    }
    
    /**
     * 开户资料附件保存
     */
    @RequestMapping(value = "/saveSingleFileLink", method = RequestMethod.POST)
    public @ResponseBody String saveSingleFileLink(Long id, final String fileTypeName, final String fileMediaId) {
        return exec(() -> custOpenAccountService.webSaveSingleFileLink(id, fileTypeName, fileMediaId), "开户资料附件保存", logger);
    }
    
    /**
     * 根据batchNo生成对应文件类型Map Json对象(微信使用)
     */
    @RequestMapping(value = "/findAccountFileByBatChNo", method = RequestMethod.POST)
    public @ResponseBody String findAccountFileByBatChNo(Long batchNo) {
        logger.info("附件查询,入参：" + batchNo );
        try {

            return custOpenAccountService.webFindAccountFileByBatChNo(batchNo);
        }
        catch (RpcException e) {
            logger.error(e.getMessage(), e);
            if (BytterException.isCauseBytterException(e)) {
                return AjaxObject.newError(e.getCause().getMessage()).toJson();
            }
            return AjaxObject.newError("附件查询失败").toJson();
        }
        catch (Exception e) {
            logger.error(e.getMessage(), e);
            return AjaxObject.newError("附件查询失败").toJson();
        }
    }
    
    /**
     * 微信发送手机验证码
     */
    @RequestMapping(value = "/sendValidMessage", method = RequestMethod.POST)
    public @ResponseBody String sendValidMessage(String mobileNo) {
        return exec(() -> custOpenAccountService.webSendValidMessage(mobileNo), "发送手机短信验证码", logger);
    }
    
    /**
     * 删除附件信息
     */
    @RequestMapping(value = "/deleteSingleFile", method = RequestMethod.POST)
    public @ResponseBody String deleteSingleFile(Long id) {
        return exec(() -> custOpenAccountService.webDeleteSingleFile(id), "删除附件", logger);
    }
    
    /**
     * 微信查询开户成功后资料
     * --不能直接通过wechat标识去查询tmp表，因为若是微信绑定已开户账户，则tmp表中wechat标识为空--
     */
    @RequestMapping(value = "/findSuccessAccountInfo", method = RequestMethod.POST)
    public @ResponseBody String findSuccessAccountInfo() {
        try {
            final Object openIdObj = Servlets.getSession().getAttribute("wechat_openId");
            if (openIdObj != null) {
                final String openId = String.valueOf(openIdObj);
                return custOpenAccountService.webFindSuccessAccountInfo(openId);
            }
            return AjaxObject.newError("获取开户信息失败").toJson();
        }
        catch (final Exception e) {
            return AjaxObject.newError("获取开户信息失败").toJson();
        }
    }
}
