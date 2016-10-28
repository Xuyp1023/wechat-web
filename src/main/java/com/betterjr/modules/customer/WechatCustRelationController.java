package com.betterjr.modules.customer;

import static com.betterjr.common.web.ControllerExceptionHandler.exec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;

@Controller
@RequestMapping("/Wechat/CustRelation")
public class WechatCustRelationController {

    private static final Logger logger = LoggerFactory.getLogger(WechatCustRelationController.class);

    @Reference(interfaceClass = ICustRelationService.class)
    private ICustRelationService custRelationService;

    @RequestMapping(value = "/findCustInfo", method = RequestMethod.POST)
    public @ResponseBody String findCustInfo() {
        logger.info("微信客户信息查询");

        return exec(() -> custRelationService.webFindWechatCurrentCustInfo(), "微信客户信息查询失败", logger);
    }

    @RequestMapping(value = "/saveRelation", method = RequestMethod.POST)
    public @ResponseBody String saveRelation(Long custNo, String factorList) {
        logger.info("微信端开通保理融资业务申请,入参: " + custNo + " and " + factorList);

        return exec(() -> custRelationService.webSaveCustRelation(custNo, factorList), "微信端开通保理融资业务申请失败", logger);
    }

    @RequestMapping(value = "/queryFactorRelation", method = RequestMethod.POST)
    public @ResponseBody String queryWeChatFactorList() {
        logger.info("客户与保理机构关系查询");

        return exec(() -> custRelationService.webQueryFactorRelation(), "客户与保理机构关系查询失败", logger);
    }

}
