package com.betterjr.modules.loan;

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
import com.betterjr.modules.agreement.IScfElecAgreementService;
import com.betterjr.modules.credit.IScfCreditService;
import com.betterjr.modules.customer.ICustRelationService;
import com.betterjr.modules.document.ICustFileService;
import com.betterjr.modules.order.IScfOrderService;
import com.betterjr.modules.product.IScfProductService;

@Controller
@RequestMapping(value = "/Wechat/Scf/Application")
public class WechatApplicationController {
    private static final Logger logger = LoggerFactory.getLogger(WechatApplicationController.class);

    @Reference(interfaceClass = IScfOrderService.class)
    private IScfOrderService scfOrderService;

    @Reference(interfaceClass = IScfProductService.class)
    private IScfProductService scfProductService;

    @Reference(interfaceClass = IScfCreditService.class)
    private IScfCreditService scfCreditService;

    @Reference(interfaceClass = ICustRelationService.class)
    private ICustRelationService custRelationService;

    @Reference(interfaceClass = IScfRequestService.class)
    private IScfRequestService requestService;

    @Reference(interfaceClass = IScfElecAgreementService.class)
    private IScfElecAgreementService scfElecAgreementService;

    @Reference(interfaceClass = ICustFileService.class)
    private ICustFileService custFileService;

    @RequestMapping(value = "/findSubjectMaster", method = RequestMethod.POST)
    public @ResponseBody String queryBillRequestList(HttpServletRequest request, String id, String type) {
        try {
            return scfOrderService.webFindSubjectMaster(id, type);
        }
        catch (Exception ex) {
            logger.error("查询标的物:", ex);
            return AjaxObject.newError("findSubjectMaster service failed").toJson();
        }
    }

    @RequestMapping(value = "/findProductList", method = RequestMethod.POST)
    public @ResponseBody String findProduct(HttpServletRequest request) {
        try {
            return scfProductService.webQueryProductKeyAndValue();
        }
        catch (Exception ex) {
            logger.error("查询产品列表:", ex);
            return AjaxObject.newError("findProductList service failed").toJson();
        }

    }

    @RequestMapping(value = "/findCreditList", method = RequestMethod.POST)
    public @ResponseBody String findCredit(HttpServletRequest request, Long custNo, Long coreCustNo, Long factorNo) {
        try {
            return scfCreditService.webFindCreditSimpleData(custNo, coreCustNo, factorNo);
        }
        catch (Exception ex) {
            logger.error("查询授信列表:", ex);
            return AjaxObject.newError("findCreditList service failed").toJson();
        }

    }

    @RequestMapping(value = "/queryFactorKeyAndValue", method = RequestMethod.POST)
    public @ResponseBody String findCredit(HttpServletRequest request, Long custNo) {
        try {
            return custRelationService.webQueryFactorKeyAndValue(custNo);
        }
        catch (Exception ex) {
            logger.error("查询授信列表:", ex);
            return AjaxObject.newError("findCreditList service failed").toJson();
        }

    }

    @RequestMapping(value = "/findRequestByNo", method = RequestMethod.POST)
    public @ResponseBody String findRequestByNo(HttpServletRequest request, String requestNo) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        logger.info("根据申请编号查询融资申请，入参:" + map.toString());

        try {
            return requestService.webFindRequestDetail(map, requestNo);
        }
        catch (Exception ex) {
            logger.error("查询融资申请:", ex);
            return AjaxObject.newError("findRequestByNo service failed").toJson();
        }

    }

    @RequestMapping(value = "/findScheme", method = RequestMethod.POST)
    public @ResponseBody String confirmScheme(HttpServletRequest request, String requestNo) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        logger.info("查询保理方案，入参:" + map.toString());

        try {
            return requestService.webFindSchemeDetail(requestNo);
        }
        catch (Exception ex) {
            logger.error("查询保理方案:", ex);
            return AjaxObject.newError("findScheme service failed").toJson();
        }

    }

    @RequestMapping(value = "/queryRequest", method = RequestMethod.POST)
    public @ResponseBody String supplierQueryRequest(HttpServletRequest request, String flag, int pageNum,
            int pageSize) {
        Map<String, Object> anMap = Servlets.getParametersStartingWith(request, "");
        try {
            return requestService.webCustQueryRequest(anMap, flag, pageNum, pageSize);
        }
        catch (Exception e) {
            logger.error("查询融资列表失败", e);
            return AjaxObject.newError("查询融资列表失败:" + e.getMessage()).toJson();
        }
    }

    @RequestMapping(value = "/queryOtherFile", method = RequestMethod.POST)
    public @ResponseBody String queryOtherFile(String requestNo) {
        logger.info("queryOtherFile 入参：" + requestNo);
        try {
            return scfElecAgreementService.webQueryOtherFile(requestNo);
        }
        catch (RpcException btEx) {
            logger.error("requestNo查询其它资料异常：" + btEx.getMessage());
            if (btEx.getCause() != null && btEx.getCause() instanceof BytterException) {
                return AjaxObject.newError(btEx.getCause().getMessage()).toJson();
            }
            return AjaxObject.newError("查询出现异常").toJson();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return AjaxObject.newError("requestNo查询其它资料异常：").toJson();
        }
    }

    @RequestMapping(value = "/queryRequestFile", method = RequestMethod.POST)
    public @ResponseBody String queryRequestFile(Long batchNo) {
        try {
            return AjaxObject.newOk(custFileService.findCustFiles(batchNo)).toJson();
        }
        catch (Exception e) {
            logger.error("查询融资材料失败", e);
            return AjaxObject.newError("查询融资材料失败:" + e.getMessage()).toJson();
        }
    }

    @RequestMapping(value = "/findRequestByInfoId", method = RequestMethod.POST)
    public @ResponseBody String findRequestByInfoId(Long infoId, String infoType) {
        try {
            return AjaxObject.newOk(scfOrderService.webFindRequestByInfoId(infoId, infoType)).toJson();
        }
        catch (Exception e) {
            logger.error("根据资料id和资料类型查询融资实体", e);
            return AjaxObject.newError("根据资料id和资料类型查询融资实体:" + e.getMessage()).toJson();
        }
    }

    @RequestMapping(value = "/confirmScheme", method = RequestMethod.POST)
    public @ResponseBody String confirmScheme(HttpServletRequest request, String requestNo, String smsCode) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        logger.info("确认保理方案，入参:" + map.toString());

        try {
            return requestService.webConfirmScheme(requestNo, "0", smsCode);
        }
        catch (Exception ex) {
            logger.error("确认保理方案:", ex);
            return AjaxObject.newError("confirmScheme service failed").toJson();
        }

    }
}
