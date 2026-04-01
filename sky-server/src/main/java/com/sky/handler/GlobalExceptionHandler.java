package com.sky.handler;

import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 * 项目里任何地方报错了，都由这个类统一捕获、统一返回给前端友好提示，不会让系统崩掉。
 */
@RestControllerAdvice
//@RestControllerAdvice = 全局异常管家
//只要项目抛异常，自动跑到这里处理
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /**
     * 处理sql异常
     */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        String msg=ex.getMessage();
        if(msg.contains("Duplicate entry")){
            String[] split =msg.split(" ");
            String username =split[2];
            String message=username+ MessageConstant.ALREADY_EXIST;
            return Result.error(message);
        }else{
            return Result.error(MessageConstant.UNKNOWN_ERROR);
        }
    }

}
