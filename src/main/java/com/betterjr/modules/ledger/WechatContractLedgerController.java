package com.betterjr.modules.ledger;

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
import com.betterjr.common.web.Servlets;
import com.betterjr.modules.customer.ICustRelationService;

@Controller
@RequestMapping("/Wechat/Scf/ContractLedger")
public class WechatContractLedgerController {

    private static final Logger logger = LoggerFactory.getLogger(WechatContractLedgerController.class);

    @Reference(interfaceClass = IContractLedgerService.class)
    public IContractLedgerService contractLedgerService;

    @Reference(interfaceClass = ICustRelationService.class)
    private ICustRelationService custRelationService;

    @RequestMapping(value = "/queryContractLedger", method = RequestMethod.POST)
    public @ResponseBody String queryContractLedger(HttpServletRequest request, String queryType, int pageNum,
            int pageSize) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        logger.info("微信端-分页查询贸易合同台账信息，入参:" + map.toString() + "，queryType=" + queryType);
        return exec(() -> contractLedgerService.webQueryContractLedgerByPage(map, queryType, pageNum, pageSize),
                "微信端-分页查询贸易合同台账", logger);
    }

    /***
     * 查询经销商/供应商关联的核心企业
     * @param custNo
     * @return
     */
    @RequestMapping(value = "/findRelationCoreCust", method = RequestMethod.POST)
    public @ResponseBody String findRelationCoreCust(Long custNo) {
        logger.info("微信端-查询经销商/供应商关联的核心企业，入参:" + custNo);
        return exec(() -> custRelationService.webQueryCoreKeyAndValue(custNo), "微信端-核心企业下拉列表查询失败", logger);
    }

    /***
     * 查询合同台账详情
     * @param contractId
     * @return
     */
    @RequestMapping(value = "/findContractLedgerByContractId", method = RequestMethod.POST)
    public @ResponseBody String findContractLedgerByContractId(Long contractId) {
        logger.info("微信端：findContractLedgerByContractId，入参：contractid:" + contractId);
        return exec(() -> contractLedgerService.webFindContractLedgerByContractId(contractId), "查询合同台账信息", logger);
    }

    /***
     * 添加合同台账
     * @param request
     * @return
     */
    @RequestMapping(value = "/addContractLedger", method = RequestMethod.POST)
    public @ResponseBody String addContractLedger(HttpServletRequest request) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        logger.info("微信端-添加合同台账，入参:" + map);
        return exec(() -> contractLedgerService.webWechatAddContractLedger(map), "微信端-添加合同台账", logger);
    }

    @RequestMapping(value = "/saveContractLedgerFile", method = RequestMethod.POST)
    public @ResponseBody String saveContractLedgerFile(Long contractId, String fileTypeName, String fileMediaId) {
        logger.info("微信端-保存合同附件，入参:contractId=" + contractId + "，fileTypeName=" + fileTypeName + "，fileMediaId="
                + fileMediaId);
        return exec(() -> contractLedgerService.webSaveContractLedgerFile(contractId, fileTypeName, fileMediaId),
                "微信端-添加合同台账", logger);
    }

    /***
     * 修改合同台账信息
     * @param request
     * @return
     */
    @RequestMapping(value = "/saveContractLedger", method = RequestMethod.POST)
    public @ResponseBody String saveContractLedger(HttpServletRequest request) {
        Map<String, Object> map = Servlets.getParametersStartingWith(request, "");
        logger.info("微信端-修改合同台账，入参:" + map);
        return exec(() -> contractLedgerService.webWechatSaveContractLedger(map), "微信端-修改合同台账", logger);
    }

    /***
     * 修改合同台账状态
     * @param contractId
     * @param status
     * @return
     */
    @RequestMapping(value = "/saveContractLedgerStatus", method = RequestMethod.POST)
    public @ResponseBody String saveContractLedgerStatus(Long contractId, String status) {
        logger.info("contractId:" + contractId + "，status：" + status);
        return exec(() -> contractLedgerService.webSaveContractLedgerStatus(contractId, status), "合同状态变更", logger);
    }

    /***
     * 根据合同编号查询合同附件信息
     * @param contractId
     * @return
     */
    @RequestMapping(value = "/findFileByContractId", method = RequestMethod.POST)
    public @ResponseBody String findFileByContractId(Long contractId) {
        logger.info("contractId:" + contractId);
        return exec(() -> contractLedgerService.webFindFileByContractId(contractId), "根据合同编号查询合同附件信息", logger);
    }

    @RequestMapping(value = "/deleteContractById", method = RequestMethod.POST)
    public @ResponseBody String deleteContractById(Long contractId) {
        logger.info("deleteContractById contractId:" + contractId);
        return exec(() -> contractLedgerService.webDeleteContractById(contractId), "删除台账合同", logger);
    }

    @RequestMapping(value = "/deleteContractFile", method = RequestMethod.POST)
    public @ResponseBody String deleteContractFile(Long fileId) {
        logger.info("deleteContractById fileId:" + fileId);
        return exec(() -> contractLedgerService.webDeleteContractFile(fileId), "合同附件", logger);
    }
}
