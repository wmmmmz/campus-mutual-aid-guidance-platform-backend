package com.wmz.campusplatform.handler;

import cn.dev33.satoken.exception.NotLoginException;
import com.wmz.campusplatform.pojo.ResultTool;
import com.wmz.campusplatform.pojo.ReturnMessage;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Log4j2
public class ExceptionHandle {

    @ExceptionHandler(value = NotLoginException.class)
    @ResponseBody
    public ResultTool notLoginHandler(HttpServletRequest req, NotLoginException e) {
        log.error(e.getMessage());
        return new ResultTool(ReturnMessage.NOT_LOGIN.getCodeNum(), ReturnMessage.NOT_LOGIN.getCodeMessage(), e.getMessage());
    }


    /**
     * no permission
     * @param e
     * @return
     */
    @ExceptionHandler
    @ResponseBody
    public ResultTool handlerException(Exception e) {
        log.error(e.getMessage());
        return new ResultTool(ReturnMessage.NO_PERMISSION.getCodeNum(), ReturnMessage.NO_PERMISSION.getCodeMessage(), e.getMessage());
    }
}
