package com.betterjr.modules.customer;

import static com.betterjr.common.web.ControllerExceptionHandler.exec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.dubbo.config.annotation.Reference;
import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.modules.account.dubboclient.CustOperatorDubboClientService;

@Controller
@RequestMapping("/Wechat/Platform/CustRelationConfig")
public class WechatCustRelationConfigController {
    
    private static final Logger logger = LoggerFactory.getLogger(WechatCustRelationConfigController.class);
    
    @Reference(interfaceClass=ICustRelationConfigService.class)
    private ICustRelationConfigService custRelationConfigService;
    @Autowired
    private CustOperatorDubboClientService custOperatorDubboClientService;
    
    @RequestMapping(value = "/findCustType", method = RequestMethod.POST)
    public @ResponseBody String findCustType() {
        return exec(() -> custRelationConfigService.webFindCustType(), "查询需要选择的客户类型", logger);
    }
    
    @RequestMapping(value = "/findCustInfo", method = RequestMethod.POST)
    public @ResponseBody String findCustInfo(String custType,String custName) {
        logger.info("查询客户信息，入参：custType="+custType+"，custName："+custName);
        return exec(() -> custRelationConfigService.webFindCustInfo(custType,findCurrentLongCustNo(),custName), "查询客户信息", logger);
    }
    
    @RequestMapping(value = "/addCustRelation", method = RequestMethod.POST)
    public @ResponseBody String addCustRelation(String custType,String relationCustStr) {
        logger.info("添加客户关系，入参：custType="+custType+"，relationCustStr="+relationCustStr);
        return exec(() -> custRelationConfigService.webAddCustRelation(custType, findCurrentLongCustNo(), relationCustStr), "添加客户关系", logger);
    }
    
    @RequestMapping(value = "/queryCustRelation", method = RequestMethod.POST)
    public @ResponseBody String queryCustRelation(String flag,int pageNum,int pageSize,String relationType) {
        return exec(() -> custRelationConfigService.webQueryCustRelation(findCurrentLongCustNo(),flag,pageNum,pageSize,relationType), "分页查询客户关系信息", logger);
    }
    
    /***
     * 查询电子合同服务商客户
     * @return
     */
    @RequestMapping(value = "/findElecAgreementServiceCust", method = RequestMethod.POST)
    public @ResponseBody String findElecAgreementServiceCust() {
        return exec(() -> custRelationConfigService.webFindElecAgreementServiceCust(), "查询电子合同服务商客户", logger);
    }

    // 查询临时文件
    @RequestMapping(value = "/findCustAduitTemp", method = RequestMethod.POST)
    public @ResponseBody String findCustAduitTemp(Long relateCustNo) {
        return exec(() -> custRelationConfigService.webFindCustAduitTempFile(relateCustNo,null), "查询临时文件", logger);
    }
    
    /**
     * 保存临时文件
     * @param relateCustNo
     * @param custNo
     * @param fileTypeName
     * @param fileMediaId
     * @param anCustType
     * @return
     */
    @RequestMapping(value = "/addCustAduitTempFile", method = RequestMethod.POST)
    public @ResponseBody String addCustAduitTempFile(Long relateCustNo,String fileTypeName,String fileMediaId,String custType){
        return exec(() -> custRelationConfigService.webAddCustAduitTempFile(relateCustNo, fileTypeName, fileMediaId,custType), "保存临时文件", logger);
    }
    
    /***
     * 删除附件
     */
    @RequestMapping(value = "/saveDeleteCustAduitTempFile", method = RequestMethod.POST)
    public @ResponseBody String saveDeleteCustAduitTempFile(Long id){
        return exec(() -> custRelationConfigService.webSaveDeleteCustAduitTempFile(id), "删除附件", logger);
    }
    
    /***
     * 添加保理方客户关系
     * @param custType
     * @param relationCustNo
     * @return
     */
    @RequestMapping(value = "/addFactorCustRelation", method = RequestMethod.POST)
    public @ResponseBody String addFactorCustRelation(String factorCustType,String wosCustType,String factorCustNoList,String wosCustNoList) {
        logger.info("添加保理方客户关系，入参：custType="+factorCustType+"，wosCustType="+wosCustType+"，factorCustNoList="+factorCustNoList+"，wosCustStr="+wosCustNoList);
        return exec(() -> custRelationConfigService.webAddFactorCustRelation(factorCustType, wosCustType, factorCustNoList, wosCustNoList,null), "添加保理方客户关系", logger);
    }
    
    /***
     * 查询审批记录
     * @param request
     * @return
     */
    @RequestMapping(value = "/findCustRelateAduitRecord", method = RequestMethod.POST)
    public @ResponseBody String findCustRelateAduitRecord(Long custNo){
        logger.info("入参：custNo:" + custNo);
        return exec(() -> custRelationConfigService.webFindCustRelateAduitRecord(custNo,null,null), "查询审批记录", logger);
    }
    
    public Long findCurrentLongCustNo(){
        Long custNo=custOperatorDubboClientService.findCustNo();
        logger.info("当前登录：custNo="+custNo);
        if(custNo==null){
            throw new BytterTradeException("当前登录客户号获取失败");
        }
        return custNo;
    }
    
    // 查询当前客户的类型
    @RequestMapping(value = "/findCustTypeByLogin", method = RequestMethod.POST)
    public @ResponseBody String findCustTypeByLogin() {
        return exec(() -> custRelationConfigService.webFindCustTypeByCustNo(), "查询当前客户的类型", logger);
    }
}
