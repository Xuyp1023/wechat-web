package com.betterjr.modules.enquiry;

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
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.common.web.ControllerExceptionHandler;
import com.betterjr.common.web.Servlets;
import com.betterjr.common.web.ControllerExceptionHandler.ExceptionHandler;

@Controller
@RequestMapping(value = "/Wechat/Scf/Enquiry")
public class WechatEnquiryController {
    private static final Logger logger = LoggerFactory.getLogger(WechatEnquiryController.class);
    
    @Reference(interfaceClass = IScfEnquiryService.class)
    private IScfEnquiryService enquiryService;
    
    @RequestMapping(value = "/queryEnquiryList", method = RequestMethod.POST)
    public @ResponseBody String queryEnquiryList(HttpServletRequest request, int flag, int pageNum, int pageSize) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        logger.info("查询询价，入参:"+ map.toString());
        
        try {
            return enquiryService.webQueryEnquiryList(map, flag, pageNum, pageSize);
        }
        catch (RpcException btEx) {
            if (btEx.getCause() != null && btEx.getCause() instanceof BytterException) {
                return AjaxObject.newError(btEx.getCause().getMessage()).toJson();
            }
            return AjaxObject.newError("queryEnquiryList service failed").toJson();
        }
        catch (Exception ex) {
            logger.error("查询询价:", ex);
            return AjaxObject.newError("queryEnquiryList service failed").toJson();
        }

    }

    @RequestMapping(value = "/addEnquiry", method = RequestMethod.POST)
    public @ResponseBody String addEnquiry(HttpServletRequest request) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        fillLoginCustNo(map);
        logger.info("保存询价，入参:"+ map.toString());
        
        try {
            return enquiryService.webAddEnquiry(map);
        }
        catch (Exception ex) {
            logger.error("保存询价:", ex);
            return AjaxObject.newError("addEnquiry service failed").toJson();
        }

    }
    
    @RequestMapping(value = "/saveModifyEnquiry", method = RequestMethod.POST)
    public @ResponseBody String saveModifyEnquiry(HttpServletRequest request, Long id) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        logger.info("修改询价，入参:"+ map.toString());
        
        try {
            return enquiryService.webSaveModifyEnquiry(map, id);
        }
        catch (Exception ex) {
            logger.error("修改询价:", ex);
            return AjaxObject.newError("saveModifyEnquiry service failed").toJson();
        }

    }

    @RequestMapping(value = "/findEnquiryDetail", method = RequestMethod.POST)
    public @ResponseBody String findEnquiryDetail(HttpServletRequest request, Long id) {
        logger.info("查看询价详情，入参:"+ id);
        
        try {
            return enquiryService.webFindEnquiryDetail(id);
        }
        catch (Exception ex) {
            logger.error("查看询价详情:", ex);
            return AjaxObject.newError("findEnquiryDetail service failed").toJson();
        }
    }
    
    @RequestMapping(value = "/queryOfferList", method = RequestMethod.POST)
    public @ResponseBody String queryOfferList(HttpServletRequest request, int flag, int pageNum, int pageSize) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        logger.info("分页查询报价列表:"+ map.toString());
        
        try {
            return enquiryService.webQueryOfferList(map, flag, pageNum, pageSize);
        }
        catch (RpcException btEx) {
            if (btEx.getCause() != null && btEx.getCause() instanceof BytterException) {
                return AjaxObject.newError(btEx.getCause().getMessage()).toJson();
            }
            return AjaxObject.newError("queryOfferList service failed").toJson();
        }
        catch (Exception ex) {
            logger.error("查询报价，入参:", ex);
            return AjaxObject.newError("queryOfferList service failed").toJson();
        }

    }
    
    
    @RequestMapping(value = "/searchOfferList", method = RequestMethod.POST)
    public @ResponseBody String searchOfferList(HttpServletRequest request) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        logger.info("分页查询报价列表:"+ map.toString());
        
        try {
            return enquiryService.webSearchOfferList(map);
        }
        catch (RpcException btEx) {
            if (btEx.getCause() != null && btEx.getCause() instanceof BytterException) {
                return AjaxObject.newError(btEx.getCause().getMessage()).toJson();
            }
            return AjaxObject.newError("webSearchOfferList service failed").toJson();
        }
        catch (Exception ex) {
            logger.error("查询报价，入参:", ex);
            return AjaxObject.newError("webSearchOfferList service failed").toJson();
        }

    }


    @RequestMapping(value = "/addOffer", method = RequestMethod.POST)
    public @ResponseBody String addOffer(HttpServletRequest request) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        logger.info("保存报价，入参:"+ map.toString());
        
        try {
            return enquiryService.webAddOffer(map);
        }
        catch (Exception ex) {
            logger.error("保存报价，入参:", ex);
            return AjaxObject.newError("saveOffer service failed").toJson();
        }

    }
    
    @RequestMapping(value = "/saveModifyOffer", method = RequestMethod.POST)
    public @ResponseBody String webSaveModifyOffer(HttpServletRequest request, Long id) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        logger.info("修改报价，入参:"+ map.toString());
        
        try {
            return enquiryService.webSaveModifyOffer(map, id);
        }
        catch (Exception ex) {
            logger.error("修改报价:", ex);
            return AjaxObject.newError("updateOffer service failed").toJson();
        }

    }

    @RequestMapping(value = "/findOfferDetail", method = RequestMethod.POST)
    public @ResponseBody String findOfferDetail(HttpServletRequest request, Long factorNo, String enquiryNo) {
        logger.info("查看报价详情，入参:factorNo:"+ factorNo + " enquiryNo:"+enquiryNo);
        
        try {
            return enquiryService.webFindOfferDetail(factorNo, enquiryNo);
        }
        catch (Exception ex) {
            logger.error("查看报价详情:", ex);
            return AjaxObject.newError("findOfferDetail service failed").toJson();
        }
    }
    
    @RequestMapping(value = "/queryEnquiryByfactorNo", method = RequestMethod.POST)
    public @ResponseBody String findEnquiryByfactorNo(HttpServletRequest request, int flag, int pageNum, int pageSize) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        logger.info("查看保理公司收到的询价queryEnquiryByfactorNo，入参:"+ map);
        
        try {
            return enquiryService.webQueryEnquiryByfactorNo(map, flag, pageNum, pageSize);
        }
        catch (Exception ex) {
            logger.error("查看保理公司收到的询价:", ex);
            return AjaxObject.newError("findEnquiryByfactorNo service failed").toJson();
        }
    }

    @RequestMapping(value = "/addOfferReply", method = RequestMethod.POST)
    public @ResponseBody String addOfferReply(HttpServletRequest request, String enquiryNo) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        logger.debug("保存报价回复,参数:"+ map);
        
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            public String handle() {
                return enquiryService.webAddOfferReply(map);
            }
        }, "保存报价回复", logger);
        
    }
    
    @RequestMapping(value = "/saveModifyOfferReply", method = RequestMethod.POST)
    public @ResponseBody String saveModifyOfferReply(HttpServletRequest request, String enquiryNo, Long id) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        logger.debug("修改报价回复,参数:"+ map);
        
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            public String handle() {
                return enquiryService.webSaveModifyOfferReply(map, id);
            }
        }, "修改报价回复", logger);
        
    }
    
    @RequestMapping(value = "/queryOfferReply", method = RequestMethod.POST)
    public @ResponseBody String queryOfferReply(HttpServletRequest request, String requestNo, int flag, int pageNum, int pageSize) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        logger.debug("分页报价回复,参数:"+ map);
        
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            public String handle() {
                return enquiryService.webQueryOfferReply(map, flag, pageNum, pageSize);
            }
        }, "分页查询报价回复", logger);
        
    }
    
   /* @RequestMapping(value = "/custDropEnquiry", method = RequestMethod.POST)
    public @ResponseBody String custDropEnquiry(HttpServletRequest request, Long offerId, String dropReason, String description) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        logger.debug("询价方-放弃资金方提供的报价:"+ map);
        
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            public String handle() {
                return enquiryService.webCustDropEnquiry(offerId, dropReason, description);
            }
        }, "询价方-放弃资金方提供的报价", logger);
        
    }*/
    
    @RequestMapping(value = "/factorDropOffer", method = RequestMethod.POST)
    public @ResponseBody String factorDropOffer(HttpServletRequest request, String enquiryNo, Long factorNo) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        logger.debug("资金方-放弃报价:"+ map);
        
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            public String handle() {
                return enquiryService.webFactorDropOffer(enquiryNo, factorNo);
            }
        }, "放弃报价", logger);
        
    }
    
    @RequestMapping(value = "/custDropOffer", method = RequestMethod.POST)
    public @ResponseBody String custDropFactorOffer(HttpServletRequest request, Long enquiryNo, Long offerId, String dropReason, String description) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        logger.debug("询价企业放弃 某个保理公司的报价,参数:"+ map);
        
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            public String handle() {
                return enquiryService.webCustDropOffer(enquiryNo, offerId, dropReason, description);
            }
        }, "放弃报价", logger);
        
    }
    
    @RequestMapping(value = "/queryOfferByEnquiryObject", method = RequestMethod.POST)
    public @ResponseBody String queryOfferByFactor(HttpServletRequest request, String enquiryNo) {
        logger.debug("查看向只定公司发出询价的报价情况,参数:"+ enquiryNo);
        
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            public String handle() {
                return enquiryService.webQueryOfferByEnquiryObject(enquiryNo);
            }
        }, "查看有哪些公司报了价", logger);
        
    }
    
    @RequestMapping(value = "/queryEnquiryObject", method = RequestMethod.POST)
    public @ResponseBody String webQueryEnquiryObject(HttpServletRequest request, String enquiryNo, Long factorNo) {
        logger.debug("查看向只定公司发出询价的报价情况,参数:"+ enquiryNo);
        
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            public String handle() {
                return enquiryService.webQueryEnquiryObject(enquiryNo, factorNo);
            }
        }, "查看有哪些公司报了价", logger);
        
    }
    
    @RequestMapping(value = "/querySingleOrderEnquiryList", method = RequestMethod.POST)
    public @ResponseBody String querySingleOrderEnquiryList(HttpServletRequest request, int flag, int pageNum, int pageSize) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        
        String[] queryTerm = new String[] { "custNo", "GTEactualDate", "LTEactualDate"};
        Map<String, Object> qyMap = Collections3.filterMap(map, queryTerm);
        fillLoginCustNo(qyMap);
        logger.debug("查看有哪些公司报了价,参数:"+ qyMap);
        
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            public String handle() {
                return enquiryService.webQuerySingleOrderEnquiryList(qyMap, flag, pageNum, pageSize);
            }
        }, "查看有哪些公司报了价", logger);
        
    }
    
    @RequestMapping(value = "/findSingleOrderEnquiryDetail", method = RequestMethod.POST)
    public @ResponseBody String findSingleOrderEnquiryDetail(HttpServletRequest request, String enquiryNo) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        map.put("id", enquiryNo);
        logger.debug("询价详情,参数:"+ map);
        
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            public String handle() {
                return enquiryService.webFindSingleOrderEnquiryDetail(enquiryNo);
            }
        }, "询价详情", logger);
        
    }
    
    private void fillLoginCustNo(Map<String, Object> anMap) {
        if(UserUtils.supplierUser() || UserUtils.sellerUser()){
            anMap.put("custNo", UserUtils.getDefCustInfo().getCustNo().toString());
        }
        else if(UserUtils.factorUser()){
            anMap.put("factorNo", UserUtils.getDefCustInfo().getCustNo().toString());
        }else{
            anMap.put("coreCustNo", UserUtils.getDefCustInfo().getCustNo().toString());
        }
    }
   
}
