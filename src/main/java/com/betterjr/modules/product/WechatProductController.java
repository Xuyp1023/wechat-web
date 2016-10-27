package com.betterjr.modules.product;

import static com.betterjr.common.web.ControllerExceptionHandler.exec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;

@Controller
@RequestMapping("/Wechat/Scf/Product")
public class WechatProductController {

    private static final Logger logger = LoggerFactory.getLogger(WechatProductController.class);

    @Reference(interfaceClass = IScfProductService.class)
    private IScfProductService productService;

    @RequestMapping(value = "/queryProductElement", method = RequestMethod.POST)
    public @ResponseBody String queryProductElement() {
        logger.info("微信端融资产品信息查询");

        return exec(() -> productService.webQueryProductKeyAndValue(), "融资产品信息查询失败", logger);
    }

    @RequestMapping(value = "/findProduct", method = RequestMethod.POST)
    public @ResponseBody String findProduct(Long productId) {
        logger.info("微信端融资产品信息查询");

        return exec(() -> productService.webFindProductById(productId), "融资产品信息查询失败", logger);
    }

}
