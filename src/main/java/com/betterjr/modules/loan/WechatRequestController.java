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
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.common.web.Servlets;

@Controller
@RequestMapping(value = "/Wechat/Scf/Request")
public class WechatRequestController {
    private static final Logger logger = LoggerFactory.getLogger(WechatRequestController.class);

    @Reference(interfaceClass = IScfWechatRequestService.class)
    private IScfWechatRequestService billRequestService;

    @Reference(interfaceClass = IScfRequestService.class)
    private IScfRequestService requestService;

    @RequestMapping(value = "/addRequest", method = RequestMethod.POST)
    public @ResponseBody String addBillRequest(HttpServletRequest request) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        logger.info("添加票据融资申请，入参:" + map.toString());

        try {
            fillLoginCustNo(map);
            return billRequestService.webAddRequest(map);
        }
        catch (Exception ex) {
            logger.error("添加票据融资申请:", ex);
            return AjaxObject.newError("addBillRequest service failed" + ex.getMessage()).toJson();
        }

    }

    @RequestMapping(value = "/requestByOffer", method = RequestMethod.POST)
    public @ResponseBody String requestByOffer(HttpServletRequest request, String offerId) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        logger.info("根据报价信息进行融资申请，入参:" + map.toString());

        try {
            return billRequestService.webRequestByOffer(offerId);
        }
        catch (Exception ex) {
            logger.error("根据报价信息进行融资申请:", ex);
            return AjaxObject.newError("requestByOffer service failed").toJson();
        }

    }

    @RequestMapping(value = "/queryRequestList", method = RequestMethod.POST)
    public @ResponseBody String queryBillRequestList(HttpServletRequest request, int flag, int pageNum, int pageSize) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        String[] queryTerm = new String[] { "tradeStatus" };
        map = Collections3.filterMap(map, queryTerm);
        logger.info("查询票据融资申请，入参:" + map.toString());

        try {
            return billRequestService.webQueryRequestList(map, flag, pageNum, pageSize);
        }
        catch (Exception ex) {
            logger.error("查询票据融资申请:", ex);
            return AjaxObject.newError("queryRequestList service failed").toJson();
        }

    }

    @RequestMapping(value = "/findRequestByBill", method = RequestMethod.POST)
    public @ResponseBody String findRequestByBill(HttpServletRequest request, String billId) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        logger.info("根据票据id查询融资申请，入参:" + map.toString());

        try {
            return billRequestService.webFindRequestByBill(billId);
        }
        catch (Exception ex) {
            logger.error("根据票据id查询融资申请:", ex);
            return AjaxObject.newError("findRequestByBill service failed").toJson();
        }

    }

    @RequestMapping(value = "/findRequestByNo", method = RequestMethod.POST)
    public @ResponseBody String findRequestByNo(HttpServletRequest request, String requestNo) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        logger.info("根据申请编号查询融资申请，入参:" + map.toString());

        try {
            return billRequestService.webFindRequestByNo(requestNo);
        }
        catch (Exception ex) {
            logger.error("查询票据融资申请:", ex);
            return AjaxObject.newError("findRequestByNo service failed").toJson();
        }

    }

    private void fillLoginCustNo(Map<String, Object> anMap) {
        if (UserUtils.supplierUser() || UserUtils.sellerUser()) {
            anMap.put("custNo", UserUtils.getDefCustInfo().getCustNo().toString());
        } else if (UserUtils.factorUser()) {
            anMap.put("factorNo", UserUtils.getDefCustInfo().getCustNo().toString());
        } else {
            anMap.put("coreCustNo", UserUtils.getDefCustInfo().getCustNo().toString());
        }
    }

}
