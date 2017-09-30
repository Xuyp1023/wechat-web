package com.betterjr.modules.agreement;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcException;
import com.betterjr.common.exception.BytterException;
import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.common.web.Servlets;
import com.betterjr.modules.account.dubboclient.CustOperatorDubboClientService;
import com.betterjr.modules.document.entity.CustFileItem;
import com.betterjr.modules.document.service.DataStoreService;
import com.betterjr.modules.document.utils.FileWebClientUtils;

/****
 * 电子合同管理
 * 
 * @author hubl
 *
 */
@Controller
@RequestMapping(value = "/Wechat/Scf/ElecAgree")
public class WechatElecAgreementController {
    private static final Logger logger = LoggerFactory.getLogger(WechatElecAgreementController.class);

    @Reference(interfaceClass = IScfElecAgreementService.class)
    private IScfElecAgreementService scfElecAgreementService;

    @Autowired
    private DataStoreService dataStoreService;
    @Autowired
    private CustOperatorDubboClientService custOperatorDubboClientService;

    @RequestMapping(value = "/queryElecAgreement", method = RequestMethod.POST)
    public @ResponseBody String queryElecAgreement(final HttpServletRequest request, final int pageNum, final int pageSize) {
        final Map anMap = Servlets.getParametersStartingWith(request, "");
        logger.info("分页查询电子合同, 入参:" + anMap.toString());
        try {
            final Long custNo = custOperatorDubboClientService.findCustNo();
            if (custNo == null) {
                throw new BytterTradeException("当前登录客户号获取失败");
            }
            anMap.put("custNo", custNo);
            return scfElecAgreementService.webQueryElecAgreementByPage(anMap, pageNum, pageSize);
        }
        catch (final RpcException btEx) {
            logger.error("分页查询电子合同异常：" + btEx.getMessage());
            if (btEx.getCause() != null && btEx.getCause() instanceof BytterException) {
                return AjaxObject.newError(btEx.getCause().getMessage()).toJson();
            }
            return AjaxObject.newError("分页查询电子合同失败").toJson();
        }
        catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            return AjaxObject.newError("分页查询电子合同失败").toJson();
        }
    }

    @RequestMapping(value = "/cancelElecAgreement", method = RequestMethod.POST)
    public @ResponseBody String cancelElecAgreePage(final String appNo, final String describe) {
        logger.info("取消电子合同的流水号：" + appNo);
        try {
            return scfElecAgreementService.webCancelElecAgreement(appNo, describe);
        }
        catch (final RpcException btEx) {
            logger.error("取消电子合同异常：" + btEx.getMessage());
            if (btEx.getCause() != null && btEx.getCause() instanceof BytterException) {
                return AjaxObject.newError(btEx.getCause().getMessage()).toJson();
            }
            return AjaxObject.newError("取消电子合同失败，请检查").toJson();
        }
        catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            return AjaxObject.newError("取消电子合同的流水号失败，请检查").toJson();
        }
    }

    @RequestMapping(value = "/findAgreePage", method = { RequestMethod.GET, RequestMethod.POST })
    public @ResponseBody String findElecAgreePage(final String appNo) {
        logger.info("生成电子合同的静态页面，流水号：" + appNo);
        try {
            return scfElecAgreementService.webSaveElecAgreeImages(appNo);
        }
        catch (final RpcException btEx) {
            logger.error("生成电子合同的静态页面异常：" + btEx.getMessage());
            if (btEx.getCause() != null && btEx.getCause() instanceof BytterException) {
                return AjaxObject.newError(btEx.getCause().getMessage()).toJson();
            }
            return AjaxObject.newError("生成静态页面失败").toJson();
        }
        catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            return AjaxObject.newError("生成通知书的静态页面失败，请检查").toJson();
        }
    }

    @RequestMapping(value = "/findValidCode", method = RequestMethod.POST)
    public @ResponseBody String findValidCode(final String appNo, final String custType) {
        logger.info("获取签署合同的验证码，流水号:" + appNo + " custType:" + custType);
        try {
            return scfElecAgreementService.webFindValidCode(appNo, custType);
        }
        catch (final RpcException btEx) {
            logger.error("获取签署合同的验证码异常：" + btEx.getMessage());
            if (btEx.getCause() != null && btEx.getCause() instanceof BytterException) {
                return AjaxObject.newError(btEx.getCause().getMessage()).toJson();
            }
            return AjaxObject.newError("获取签署合同的验证码失败").toJson();
        }
        catch (final Exception ex) {
            return AjaxObject.newError("获取签署合同的验证码失败，请检查").toJson();
        }
    }

    @RequestMapping(value = "/sendValidCode", method = RequestMethod.POST)
    public @ResponseBody String sendValidCode(final String appNo, final String custType, final String vCode) {
        logger.info("发送并验证签署合同的验证码，流水号:" + appNo + " custType:" + custType + " vCode:" + vCode);
        try {
            return scfElecAgreementService.webSendValidCode(appNo, custType, vCode);
        }
        catch (final RpcException btEx) {
            logger.error("验证签署合同的验证码异常：" + btEx.getMessage());
            if (btEx.getCause() != null && btEx.getCause() instanceof BytterException) {
                return AjaxObject.newError(btEx.getCause().getMessage()).toJson();
            }
            return AjaxObject.newError("验证签署合同的验证码失败").toJson();
        }
        catch (final Exception ex) {
            return AjaxObject.newError("发送并验证签署合同的验证码失败，请检查").toJson();
        }
    }

    @RequestMapping(value = "/findElecAgreeByRequestNo", method = RequestMethod.POST)
    public @ResponseBody String findElecAgreeByRequestNo(final String requestNo, final String signType) {
        logger.info("入参： 申请单号:" + requestNo + "，类型：" + signType);
        try {
            return scfElecAgreementService.webFindElecAgreeByOrderNo(requestNo, signType);
        }
        catch (final RpcException btEx) {
            logger.error("查询电子合同异常：" + btEx.getMessage());
            if (btEx.getCause() != null && btEx.getCause() instanceof BytterException) {
                return AjaxObject.newError(btEx.getCause().getMessage()).toJson();
            }
            return AjaxObject.newError("失败").toJson();
        }
        catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            return AjaxObject.newError("查询电子合同异常").toJson();
        }
    }

    @RequestMapping(value = "/downloadAgreePDF", method = { RequestMethod.GET, RequestMethod.POST })
    public void downloadElecAgreePDF(final HttpServletResponse response, final String appNo) {
        logger.info("下载电子合同的PDF格式文件，流水号：" + appNo);
        final CustFileItem fileItem = scfElecAgreementService.webFindPdfFileInfo(appNo);
        FileWebClientUtils.fileDownload(dataStoreService, response, fileItem);
    }

    /**
     * 查询详情
     * 
     * @param appNo
     * @return
     */
    @RequestMapping(value = "/findElecAgreeInfo", method = RequestMethod.POST)
    public @ResponseBody String findElecAgreeInfo(final String appNo) {
        logger.info("入参：appno:" + appNo);
        try {
            return scfElecAgreementService.webFindElecAgreementInfo(appNo);
        }
        catch (final RpcException btEx) {
            logger.error("查询电子合同详情异常：" + btEx.getMessage());
            if (btEx.getCause() != null && btEx.getCause() instanceof BytterException) {
                return AjaxObject.newError(btEx.getCause().getMessage()).toJson();
            }
            return AjaxObject.newError("查询电子合同详情失败").toJson();
        }
        catch (final Exception ex) {
            logger.error(ex.getMessage(), ex);
            return AjaxObject.newError("查询电子合同详情异常").toJson();
        }
    }

    @RequestMapping(value = "/downloadAgreeImage", method = { RequestMethod.GET, RequestMethod.POST })
    public void downloadElecAgreeImage(final HttpServletResponse response, final String appNo, final Long batchNo, final Long id) {

        final CustFileItem fileItem = scfElecAgreementService.webFindSignedImage(appNo, batchNo, id);

        FileWebClientUtils.fileDownload(dataStoreService, response, fileItem);
    }
}
