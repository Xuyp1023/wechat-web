package com.betterjr.modules.acceptbill;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.common.web.ControllerExceptionHandler;
import com.betterjr.common.web.Servlets;
import com.betterjr.common.web.ControllerExceptionHandler.ExceptionHandler;

@Controller
@RequestMapping(value = "/WeChat/Scf/AcceptBill")
public class WeChatScfAcceptBillController {
    private static final Logger logger = LoggerFactory.getLogger(WeChatScfAcceptBillController.class);

    @Reference(interfaceClass = IScfAcceptBillService.class)
    private IScfAcceptBillService scfAcceptBillService;

    @RequestMapping(value = "/queryAcceptBill", method = RequestMethod.POST)
    public @ResponseBody String queryAcceptBill(HttpServletRequest request, String isOnlyNormal, String flag, int pageNum, int pageSize) {
        Map<String, Object> anMap = Servlets.getParametersStartingWith(request, "");
        anMap.put("custNo", UserUtils.getDefCustInfo().getCustNo().toString());
        logger.info("汇票信息查询,入参：" + anMap.toString());
        anMap.put("custNo", UserUtils.getDefCustInfo().getCustNo().toString());
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            public String handle() {
                return scfAcceptBillService.webQueryAcceptBill(anMap, isOnlyNormal, flag, pageNum, pageSize);
            }
        }, "汇票信息查询失败", logger);
    }


    @RequestMapping(value = "/modifyAcceptBill", method = RequestMethod.POST)
    public @ResponseBody String modifyAcceptBill(HttpServletRequest request, Long id, String fileList, String otherFileList) {
        Map anMap = Servlets.getParametersStartingWith(request, "");
        logger.info("汇票信息修改,入参：" + anMap.toString());
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            public String handle() {
                return scfAcceptBillService.webSaveModifyAcceptBill(anMap, id, fileList, otherFileList);
            }
        }, "汇票信息编辑失败", logger);
    }
    
    @RequestMapping(value = "/addAcceptBill", method = RequestMethod.POST)
    public @ResponseBody String addAcceptBill(HttpServletRequest request, String fileList, String otherFileList) {
        Map anMap = Servlets.getParametersStartingWith(request, "");
        logger.info("汇票信息登记,入参：" + anMap.toString());
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            public String handle() {
                return scfAcceptBillService.webAddAcceptBill(anMap, fileList, otherFileList);
            }
        }, "汇票信息登记失败", logger);
    }
    
    @RequestMapping(value = "/saveAduitAcceptBill", method = RequestMethod.POST)
    public @ResponseBody String saveAduitAcceptBill(Long id) {
        logger.info("汇票信息审核,入参：id=" + id);
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            public String handle() {
                return scfAcceptBillService.webSaveAduitAcceptBill(id);
            }
        }, "汇票信息审核失败", logger);
    }

    @RequestMapping(value = "/findAllFile", method = RequestMethod.POST)
    public @ResponseBody String findAllFile(Long id) {
        logger.info("汇票所有附件查询,入参：id=" + id);
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            public String handle() {
                return scfAcceptBillService.webFindAllFile(id);
            }
        }, "汇票所有附件查询", logger);
    }
    
    @RequestMapping(value = "queryFinancedByFactor", method = RequestMethod.POST)
    public @ResponseBody String queryFinancedByFactor(HttpServletRequest request, Long factorNo) {
        Map anMap = Servlets.getParametersStartingWith(request, "");
        logger.info("汇票所有附件查询,入参：factorNo" + factorNo + anMap);
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            public String handle() {
                return scfAcceptBillService.webQueryFinancedByFactor(anMap, factorNo);
            }
        }, "保理公司查询已融资汇票", logger);
    }
    
    @RequestMapping(value = "/findAcceptBillDetailsById", method = RequestMethod.POST)
    public @ResponseBody String findAcceptBillDetailsById(Long id) {
        logger.info("汇票详情,入参：id=" + id);
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            public String handle() {
                return scfAcceptBillService.webFindAcceptBillDetailsById(id);
            }
        }, "汇票详情", logger);
    }
    
    @RequestMapping(value = "/saveSingleFileLink", method = RequestMethod.POST)
    public @ResponseBody String saveSingleFileLink(Long id, Long fileId) {
        return ControllerExceptionHandler.exec(new ExceptionHandler() {
            public String handle() {
                return scfAcceptBillService.webSaveSingleFileLink(id, fileId);
            }
        }, "保存附件", logger);
    }
}
