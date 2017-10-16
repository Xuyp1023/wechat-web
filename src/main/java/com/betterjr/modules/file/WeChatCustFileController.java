package com.betterjr.modules.file;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.document.ICustFileService;
import com.betterjr.modules.document.entity.CustFileItem;

@Controller
@RequestMapping(value = "/WeChat/Cust/File")
public class WeChatCustFileController {

    private static final Logger logger = LoggerFactory.getLogger(WeChatCustFileController.class);

    @Reference(interfaceClass = ICustFileService.class)
    private ICustFileService fileItemService;

    /**
     * 文件列表
     * 
     * @param id
     *            ；文件编号
     * @param response
     * @return
     */
    @RequestMapping(value = "/fileListByBatchNo")
    public @ResponseBody String fileListByBatchNo(Long batchNo) {
        try {
            List<CustFileItem> fileItems = fileItemService.findCustFiles(batchNo);
            return AjaxObject.newOk("文件列表查询成功!", fileItems).toJson();
        }
        catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return AjaxObject.newError("文件列表查询失败!").toJson();
        }
    }
}
