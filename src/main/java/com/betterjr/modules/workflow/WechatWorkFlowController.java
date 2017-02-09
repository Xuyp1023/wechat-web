// Copyright (c) 2014-2017 Bytter. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2017年1月10日, liuwl, creation
// ============================================================================
package com.betterjr.modules.workflow;

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
import com.betterjr.common.annotation.MetaData;
import com.betterjr.common.data.WebAccessType;
import com.betterjr.common.web.Servlets;

/**
 * @author liuwl
 *
 */
@Controller
@RequestMapping(value = "/Wechat/WorkFlow")
public class WechatWorkFlowController {
    private static final Logger logger = LoggerFactory.getLogger(WechatWorkFlowController.class);

    @Reference(interfaceClass = IWorkFlowService.class)
    private IWorkFlowService workFlowService;

    @RequestMapping(value = "/queryCurrentTask", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String queryCurrentTask(final HttpServletRequest request, final int flag, final int pageNum, final int pageSize) {
        logger.debug("查询待办任务");
        final Map<String, Object> param = Servlets.getParametersStartingWith(request, "");
        return exec(() -> workFlowService.webQueryCurrentTask(pageNum, pageSize, param), "查询待办任务失败", logger);
    }

    @RequestMapping(value = "/queryHistoryTask", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String queryHistoryTask(final HttpServletRequest request, final int flag, final int pageNum, final int pageSize) {
        logger.debug("查询已办任务");
        final Map<String, Object> param = Servlets.getParametersStartingWith(request, "");
        return exec(() -> workFlowService.webQueryHistoryTask(pageNum, pageSize, param), "查询已办任务失败", logger);
    }

    @RequestMapping(value = "/queryMonitorTask", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String queryMonitorTask(final HttpServletRequest request, final long custNo, final int flag, final int pageNum,
            final int pageSize) {
        logger.debug("查询监控任务");
        final Map<String, Object> param = Servlets.getParametersStartingWith(request, "");
        return exec(() -> workFlowService.webQueryMonitorTask(custNo, pageNum, pageSize, param), "查询监控任务失败", logger);
    }

    @RequestMapping(value = "/startWorkFlow", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String startWorkFlow(final HttpServletRequest request, final String workFlowName, final Long custNo) {
        logger.debug("启动流程 workFlowName=" + workFlowName + "  custNo=" + custNo);
        final Map<String, Object> param = Servlets.getParametersStartingWith(request, "");
        return exec(() -> workFlowService.webStartWorkFlow(workFlowName, custNo, param), "启动流程失败", logger);
    }

    @MetaData(acccessType={WebAccessType.ORG_MOBILE_PASS,WebAccessType.ORG_PC}, value = "passWorkFlow")
    @RequestMapping(value = "/passWorkFlow", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String passWorkFlow(final HttpServletRequest request, final String taskId) {
        logger.debug("审批通过任务");
        final Map<String, Object> param = Servlets.getParametersStartingWith(request, "");
        return exec(() -> workFlowService.webPassWorkFlow(taskId, param), "审批通过任务失败", logger);
    }

    @MetaData(acccessType={WebAccessType.ORG_MOBILE_PASS,WebAccessType.ORG_PC}, value = "rejectWorkFlow")
    @RequestMapping(value = "/rejectWorkFlow", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String rejectWorkFlow(final HttpServletRequest request, final String taskId) {
        logger.debug("审批驳回任务");
        final Map<String, Object> param = Servlets.getParametersStartingWith(request, "");
        return exec(() -> workFlowService.webRejectWorkFlow(taskId, param), "审批驳回任务失败", logger);
    }

    @MetaData(acccessType={WebAccessType.ORG_MOBILE_PASS,WebAccessType.ORG_PC}, value = "handleWorkFlow")
    @RequestMapping(value = "/handleWorkFlow", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String handleWorkFlow(final HttpServletRequest request, final String taskId) {
        logger.debug("处理经办任务");
        final Map<String, Object> param = Servlets.getParametersStartingWith(request, "");
        return exec(() -> workFlowService.webHandleWorkFlow(taskId, param), "处理经办任务失败", logger);
    }

    @RequestMapping(value = "/cancelWorkFlow", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String cancelWorkFlow(final HttpServletRequest request, final String taskId) {
        logger.debug("作废流程");
        final Map<String, Object> param = Servlets.getParametersStartingWith(request, "");
        return exec(() -> workFlowService.webCancelWorkFlow(taskId, param), "作废流程失败", logger);
    }

    @RequestMapping(value = "/saveWorkFlow", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String saveWorkFlow(final HttpServletRequest request, final String taskId) {
        logger.debug("保存经办数据");
        final Map<String, Object> param = Servlets.getParametersStartingWith(request, "");
        return exec(() -> workFlowService.webSaveWorkFlow(taskId, param), "保存经办数据", logger);
    }

    @RequestMapping(value = "/queryAuditRecord", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String queryAuditRecord(final HttpServletRequest request, final String businessId, final int flag, final int pageNum,
            final int pageSize) {
        logger.debug("查询审批记录");
        return exec(() -> workFlowService.webQueryAudit(businessId, flag, pageNum, pageSize), "查询审批记录失败", logger);
    }

    @RequestMapping(value = "/queryRejectNode", method = RequestMethod.POST, produces = "application/json")
    public @ResponseBody String queryRejectNode(final HttpServletRequest request, final String taskId) {
        logger.debug("查询驳回列表");
        return exec(() -> workFlowService.webQueryRejectNode(taskId), "查询驳回列表失败", logger);
    }
}
