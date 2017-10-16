package com.betterjr.modules.product;

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
import com.betterjr.common.web.ControllerExceptionHandler;
import com.betterjr.common.web.ControllerExceptionHandler.ExceptionHandler;
import com.betterjr.common.web.Servlets;
import com.betterjr.modules.acceptbill.WeChatScfAcceptBillController;
import com.betterjr.modules.productconfig.IScfProductConfigService;
import com.betterjr.modules.supplieroffer.IScfCoreProductCustService;

@Controller
@RequestMapping(value = "/WeChat/Scf/coreProductCust")
public class WeChatScfCoreProductCustController {

    private static final Logger logger = LoggerFactory.getLogger(WeChatScfAcceptBillController.class);

    @Reference(interfaceClass = IScfCoreProductCustService.class)
    private IScfCoreProductCustService productService;

    @Reference(interfaceClass = IScfProductConfigService.class)
    private IScfProductConfigService productConfigService;

    /**
     * 查询核心企业分配给供应商的保理产品
     * @param request
     * @param custNo
     * @param coreCustNo
     * @return
     */
    @RequestMapping(value = "/queryCanUseProduct", method = RequestMethod.POST)
    public @ResponseBody String queryCanUseProduct(HttpServletRequest request, Long custNo, Long coreCustNo) {
        Map<String, Object> anMap = Servlets.getParametersStartingWith(request, "");
        logger.info("产品查询,入参：" + anMap.toString());
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            @Override
            public String handle() {
                return productService.webQueryCanUseProduct(custNo, coreCustNo);
            }
        }, "产品查询失败", logger);
    }

    /**
     * 查询保理产品下详细的信息
     * @param request
     * @param productCode
     * @return
     */
    @RequestMapping(value = "/findAssetDictByProduct", method = RequestMethod.POST)
    public @ResponseBody String findAssetDictByProduct(HttpServletRequest request, String productCode) {
        Map<String, Object> anMap = Servlets.getParametersStartingWith(request, "");
        logger.info("查询产品的附属品关系,入参：" + anMap.toString());
        return exec(() -> productConfigService.webFindAssetDictByProduct(productCode), "查询产品的附属品关系", logger);
    }
}
