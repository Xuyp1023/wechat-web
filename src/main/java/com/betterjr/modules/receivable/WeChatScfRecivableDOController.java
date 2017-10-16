package com.betterjr.modules.receivable;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.common.web.ControllerExceptionHandler;
import com.betterjr.common.web.ControllerExceptionHandler.ExceptionHandler;
import com.betterjr.common.web.Servlets;
import com.betterjr.modules.acceptbill.WeChatScfAcceptBillController;

@Controller
@RequestMapping(value = "/WeChat/Scf/ReceivableDO")
public class WeChatScfRecivableDOController {

    private static final Logger logger = LoggerFactory.getLogger(WeChatScfAcceptBillController.class);

    @Reference(interfaceClass = IScfReceivableService.class)
    private IScfReceivableService scfReceivableService;

    /**
     * 查询应收账款信息
     * @param request
     * @param isOnlyNormal
     * @param flag
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/queryReceivableDO", method = RequestMethod.POST)
    public @ResponseBody String queryReceivableDO(HttpServletRequest request, String flag, int pageNum, int pageSize,
            boolean isCust) {
        Map<String, Object> anMap = Servlets.getParametersStartingWith(request, "");
        anMap.put("custNo", UserUtils.getDefCustInfo().getCustNo().toString());
        if (anMap.containsKey("businStatus") && StringUtils.isBlank(anMap.get("businStatus").toString())) {
            anMap.put("businStatus", "1,2");
        }
        logger.info("汇票信息查询,入参：" + anMap.toString());

        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            @Override
            public String handle() {
                return scfReceivableService.webQueryEffectiveReceivable(anMap, flag, pageNum, pageSize, isCust);
            }
        }, "汇票信息查询失败", logger);
    }

}
