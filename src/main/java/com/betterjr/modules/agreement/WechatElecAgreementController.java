package com.betterjr.modules.agreement;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcException;
import com.betterjr.common.config.ParamNames;
import com.betterjr.common.exception.BytterException;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.common.web.Servlets;
import com.betterjr.modules.agreement.IScfElecAgreementService;
import com.betterjr.modules.document.entity.CustFileItem;
import com.betterjr.modules.document.utils.CustFileClientUtils;

/****
 * 电子合同管理
 * @author hubl
 *
 */
@Controller
@RequestMapping(value = "/Wechat/Scf/ElecAgree")
public class WechatElecAgreementController {
    private static final Logger logger = LoggerFactory.getLogger(WechatElecAgreementController.class);
    
    @Reference(interfaceClass=IScfElecAgreementService.class)
    private IScfElecAgreementService scfElecAgreementService;
    
    @RequestMapping(value = "/queryElecAgreement", method = RequestMethod.POST)
    public @ResponseBody String queryElecAgreement(HttpServletRequest request, int pageNum, int pageSize) {
        Map anMap = Servlets.getParametersStartingWith(request, "");
        logger.info("分页查询电子合同, 入参:"+anMap.toString());
        try {
            fillLoginCustNo(anMap);
            return scfElecAgreementService.webQueryElecAgreementByPage(anMap, pageNum, pageSize);
        } catch (RpcException btEx) {
            logger.error("分页查询电子合同异常："+btEx.getMessage());
            if(btEx.getCause()!=null && btEx.getCause() instanceof BytterException){
                return AjaxObject.newError(btEx.getCause().getMessage()).toJson();
            }
            return AjaxObject.newError("分页查询电子合同失败").toJson();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return AjaxObject.newError("分页查询电子合同失败").toJson();
        }
    }
    
    @RequestMapping(value = "/cancelElecAgreement", method = RequestMethod.POST)
    public @ResponseBody String cancelElecAgreePage(String appNo,String describe) {
        logger.info("取消电子合同的流水号：" + appNo);
        try {
            return scfElecAgreementService.webCancelElecAgreement(appNo,describe);
        }
        catch (RpcException btEx) {
            logger.error("取消电子合同异常："+btEx.getMessage());
            if(btEx.getCause()!=null && btEx.getCause() instanceof BytterException){
                return AjaxObject.newError(btEx.getCause().getMessage()).toJson();
            }
            return AjaxObject.newError("取消电子合同失败，请检查").toJson();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return AjaxObject.newError("取消电子合同的流水号失败，请检查").toJson();
        }
    }
    
    @RequestMapping(value = "/findAgreePage", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody String findElecAgreePage(String appNo) {
        logger.info("生成电子合同的静态页面，流水号：" + appNo);
        try {
            return scfElecAgreementService.webFindElecAgreePage(appNo);
        }
        catch (RpcException btEx) {
            logger.error("生成电子合同的静态页面异常："+btEx.getMessage());
            if(btEx.getCause()!=null && btEx.getCause() instanceof BytterException){
                return AjaxObject.newError(btEx.getCause().getMessage()).toJson();
            }
            return AjaxObject.newError("生成静态页面失败").toJson();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return AjaxObject.newError("生成通知书的静态页面失败，请检查").toJson();
        }
    }
    
    @RequestMapping(value = "/findValidCode", method = RequestMethod.POST)
    public @ResponseBody String findValidCode(String appNo, String custType) {
        logger.info("获取签署合同的验证码，流水号:" + appNo + " custType:" + custType);
        try {
            return scfElecAgreementService.webFindValidCode(appNo,custType);
        }
        catch (RpcException btEx) {
            logger.error("获取签署合同的验证码异常："+btEx.getMessage());
            if(btEx.getCause()!=null && btEx.getCause() instanceof BytterException){
                return AjaxObject.newError(btEx.getCause().getMessage()).toJson();
            }
            return AjaxObject.newError("获取签署合同的验证码失败").toJson();
        }
        catch (Exception ex) {
            return AjaxObject.newError("获取签署合同的验证码失败，请检查").toJson();
        }
    }
    
    @RequestMapping(value = "/sendValidCode", method = RequestMethod.POST)
    public @ResponseBody String sendValidCode(String appNo, String custType, String vCode,String tradePwd) {
        logger.info("发送并验证签署合同的验证码，流水号:" + appNo + " custType:" + custType + " vCode:" + vCode+",tradePwd:"+tradePwd);
        try {
            return scfElecAgreementService.webSendValidCode(appNo,custType,vCode);
        }
        catch (RpcException btEx) {
            logger.error("验证签署合同的验证码异常："+btEx.getMessage());
            if(btEx.getCause()!=null && btEx.getCause() instanceof BytterException){
                return AjaxObject.newError(btEx.getCause().getMessage()).toJson();
            }
            return AjaxObject.newError("验证签署合同的验证码失败").toJson();
        }
        catch (Exception ex) {
            return AjaxObject.newError("发送并验证签署合同的验证码失败，请检查").toJson();
        }
    }
    
    @RequestMapping(value = "/findElecAgreeByRequestNo", method = RequestMethod.POST)
    public @ResponseBody String findElecAgreeByRequestNo(String requestNo,String signType) {
        logger.info("入参： 申请单号:" + requestNo+"，类型："+signType);
        try {
           return scfElecAgreementService.webFindElecAgreeByOrderNo(requestNo, signType);
        }
        catch (RpcException btEx) {
            logger.error("查询电子合同异常："+btEx.getMessage());
            if(btEx.getCause()!=null && btEx.getCause() instanceof BytterException){
                return AjaxObject.newError(btEx.getCause().getMessage()).toJson();
            }
            return AjaxObject.newError("失败").toJson();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            return AjaxObject.newError("查询电子合同异常").toJson();
        }
    }
    
    @RequestMapping(value = "/downloadAgreePDF", method = { RequestMethod.GET, RequestMethod.POST })
    public void downloadElecAgreePDF(HttpServletResponse response, String appNo) {
        logger.info("下载电子合同的PDF格式文件，流水号：" + appNo);
        CustFileItem fileItem = scfElecAgreementService.webFindPdfFileInfo(appNo);
        CustFileClientUtils.fileDownload(response, fileItem,scfElecAgreementService.webFindParamPath(ParamNames.OPENACCO_FILE_DOWNLOAD_PATH));
    }
    
    /**
     * 查询详情
     * @param appNo
     * @return
     */
    @RequestMapping(value = "/findElecAgreeInfo", method = RequestMethod.POST)
    public @ResponseBody String findElecAgreeInfo(String appNo) {
        logger.info("入参：appno:" + appNo);
        try {
           return scfElecAgreementService.webFindElecAgreementInfo(appNo);
        }
        catch (RpcException btEx) {
            logger.error("查询电子合同详情异常："+btEx.getMessage());
            if(btEx.getCause()!=null && btEx.getCause() instanceof BytterException){
                return AjaxObject.newError(btEx.getCause().getMessage()).toJson();
            }
            return AjaxObject.newError("查询电子合同详情失败").toJson();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(),ex);
            return AjaxObject.newError("查询电子合同详情异常").toJson();
        }
    }
    
    /***
     * 获取当前登录客户号
     * @param anMap
     */
    private void fillLoginCustNo(Map<String, Object> anMap) {
        if(UserUtils.supplierUser() || UserUtils.sellerUser()){
            anMap.put("custNo", UserUtils.getDefCustInfo().getCustNo());
        }
        else if(UserUtils.factorUser()){
            anMap.put("factorNo", UserUtils.getDefCustInfo().getCustNo());
        }
    } 
}
