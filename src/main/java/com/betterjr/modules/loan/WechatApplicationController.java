package com.betterjr.modules.loan;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.credit.IScfCreditService;
import com.betterjr.modules.customer.ICustRelationService;
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
	public @ResponseBody String findCredit1(HttpServletRequest request, Long custNo) {
		try {
			return custRelationService.webQueryFactorKeyAndValue(custNo);
		}
		catch (Exception ex) {
			logger.error("查询授信列表:", ex);
			return AjaxObject.newError("findCreditList service failed").toJson();
		}
		
	}
}
