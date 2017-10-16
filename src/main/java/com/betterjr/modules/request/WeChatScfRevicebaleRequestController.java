package com.betterjr.modules.request;

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
import com.betterjr.modules.customer.ICustMechBankAccountService;
import com.betterjr.modules.supplieroffer.IScfReceivableRequestService;

@Controller
@RequestMapping(value = "/WeChat/Scf/ReceivableRequest")
public class WeChatScfRevicebaleRequestController {

    private static final Logger logger = LoggerFactory.getLogger(WeChatScfAcceptBillController.class);

    @Reference(interfaceClass = IScfReceivableRequestService.class)
    private IScfReceivableRequestService requestService;

    @Reference(interfaceClass = ICustMechBankAccountService.class)
    private ICustMechBankAccountService bankAccountService;

    /**
     * 新增融资申请
     * @param request
     * @return
     * 
     *  receivableId
     */
    @RequestMapping(value = "/saveAddRequestTotal", method = RequestMethod.POST)
    public @ResponseBody String saveAddRequestTotal(HttpServletRequest request) {

        Map<String, Object> anMap = Servlets.getParametersStartingWith(request, "");
        logger.info("应付账款申请新增,入参：" + anMap.toString());
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            @Override
            public String handle() {
                return requestService.webSaveAddRequestTotal(anMap);
            }
        }, "应付账款提交申请失败", logger);

    }

    /**
     * 供应商确认申请提交
     * @param anMap
     * @param anRequestNo
     * @param anRequestPayDate
     * @param anDescription
     * @return
     * requestProductCode  保理产品productCode
     * custBankAccount
     * custBankAccountName
     * custBankName
     * wechaMarker  微信标记 
     * goodsBatchNo  合同附件
     * statementBatchNo 发票附件
     */
    @RequestMapping(value = "/saveSubmitRequestTotal", method = RequestMethod.POST)
    public @ResponseBody String saveSubmitRequestTotal(HttpServletRequest request, String requestNo,
            String requestPayDate, String description) {

        Map<String, Object> anMap = Servlets.getParametersStartingWith(request, "");
        logger.info("应付账款申请新增,入参：" + anMap.toString());
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            @Override
            public String handle() {
                return requestService.webSaveSubmitRequestTotal(anMap, requestNo, requestPayDate, description);
            }
        }, "申请提交失败", logger);

    }

    /**
     * 查询申请信息
     * @param requestNo
     * @return
     */
    @RequestMapping(value = "/findOneByRequestNo", method = RequestMethod.POST)
    public @ResponseBody String findOneByRequestNo(String requestNo) {
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            @Override
            public String handle() {
                return requestService.webFindOneByRequestNo(requestNo);
            }
        }, "查询申请信息失败", logger);

    }

    /**
     * 查询申请信息
     * @param requestNo
     * @return
     */
    @RequestMapping(value = "/findOneByReceivableId", method = RequestMethod.POST)
    public @ResponseBody String findOneByReceivableId(String refNo, String version) {
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            @Override
            public String handle() {
                return requestService.webFindRequestByReceivableId(refNo, version);
            }
        }, "查询申请信息失败", logger);

    }

    /**
     * 查询企业的账户列表
     * @param request
     * @param custNo
     * @return
     */
    @RequestMapping(value = "/queryBankAccountKeyAndValue", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String queryBankAccountKeyAndValue(HttpServletRequest request, Long custNo) {
        logger.debug("银行帐户信息-银行帐户列表 入参:custNo=" + custNo);
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            @Override
            public String handle() {
                return bankAccountService.webQueryBankAccountKeyAndValue(custNo);
            }
        }, "查询申请信息失败", logger);

    }

    /**
     * 查询账户的详细信息
     * @param request
     * @param bankAcco
     * @return
     */
    @RequestMapping(value = "/findCustMechBankAccount", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String findCustMechBankAccount(HttpServletRequest request, String bankAcco) {
        logger.debug("银行帐户信息-代录记录详情  入参: bankAcco=" + bankAcco);
        return exec(() -> bankAccountService.webFindCustMechBankAccount(bankAcco), "银行帐户信息-代录记录详情  错误", logger);
    }
}
