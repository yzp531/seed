package com.jadyer.seed.open.annotation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.exception.SeedException;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.ValidatorUtil;
import com.jadyer.seed.open.model.LoanSubmit;
import com.jadyer.seed.open.model.LoanSubmit1101;
import com.jadyer.seed.open.model.ReqData;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@OpenService
class RouterService670 {
    /**
     * 文件上传接口
     */
    @OpenMethod(Constants.OPEN_METHOD_boot_file_upload)
    CommonResult fileupload(ReqData reqData, HttpServletRequest request) {
        Map<String, String> reqMap = JSON.parseObject(reqData.getData(), new TypeReference<Map<String, String>>(){});
        String partnerApplyNo = reqMap.get("partnerApplyNo");
        if(StringUtils.isBlank(partnerApplyNo)){
            return new CommonResult(CodeEnum.OPEN_FORM_ILLEGAL.getCode(), "partnerApplyNo is blank");
        }
        //接收并处理上传过来的文件
        MultipartFile fileData = null;
        CommonsMultipartResolver mutilpartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
        if(mutilpartResolver.isMultipart(request)){
            MultipartHttpServletRequest multipartFile = (MultipartHttpServletRequest) request;
            fileData = multipartFile.getFile("fileData");
        }
        if(null==fileData || fileData.getSize()==0){
            return new CommonResult(CodeEnum.OPEN_FORM_ILLEGAL.getCode(), "未传输文件流");
        }
        InputStream is;
        try {
            is = fileData.getInputStream();
        } catch (IOException e) {
            return new CommonResult(CodeEnum.SYSTEM_BUSY.getCode(), "文件流获取失败-->"+e.getMessage());
        }
        LogUtil.getLogger().info("文档类型：" + fileData.getContentType());
        LogUtil.getLogger().info("文件大小：" + fileData.getSize()); // 2667993=2.54MB=2,667,993字节
        LogUtil.getLogger().info("文件原名：" + fileData.getOriginalFilename());
        try {
            String desktop = FileSystemView.getFileSystemView().getHomeDirectory().getPath() + System.getProperty("file.separator");
            String separator = System.getProperty("file.separator");
            FileUtils.copyInputStreamToFile(is, new File(desktop + separator +fileData.getOriginalFilename()));
        } catch (IOException e) {
            throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "文件流保存失败-->"+e.getMessage(), e);
        }
        return new CommonResult(new HashMap<String, Integer>(){
            private static final long serialVersionUID = 8673982917114045418L;
            {
                put("fileId", 33);
            }
        });
    }


    /**
     * 申请单提交接口
     */
    @OpenMethod(Constants.OPEN_METHOD_boot_loan_submit)
    CommonResult loanSubmit(ReqData reqData) {
        Map<String, String> validatorMap;
        LoanSubmit loanSubmit = JSON.parseObject(reqData.getData(), LoanSubmit.class);
        if("1101".equals(loanSubmit.getProductCode())){
            LoanSubmit1101 loanSubmit1101 = JSON.parseObject(reqData.getData(), LoanSubmit1101.class);
            validatorMap = ValidatorUtil.validateToMap(loanSubmit1101);
        }else{
            validatorMap = ValidatorUtil.validateToMap(loanSubmit);
        }
        if(null!=validatorMap && !validatorMap.isEmpty()){
            return new CommonResult(CodeEnum.OPEN_FORM_ILLEGAL, validatorMap);
        }
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("applyNo", "2016022316140001");
        return new CommonResult(resultMap);
    }
}