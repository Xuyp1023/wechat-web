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

    @RequestMapping(value = "/queryFactorRelation", method = RequestMethod.POST)
    public @ResponseBody String queryWeChatFactorList() {
        logger.info("客户与保理机构关系查询");

        return exec(() -> custRelationService.webQueryFactorRelation(), "客户与保理机构关系查询失败", logger);
    }
}
