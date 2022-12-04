package com.exchange.config;

import com.exchange.dto.ResponseInfo;
import com.exchange.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;

import java.util.List;
import java.util.StringJoiner;

/**
 * 全局异常处理，处理可预见的异常
 *
 * @author gyh
 */
@Slf4j
@RestControllerAdvice
public class RestExceptionTranslator {

    @ExceptionHandler(BusinessException.class)
    public ResponseInfo<Void> handleError(BusinessException e) {
        log.error("业务异常:{}", e.getMessage());
        return ResponseInfo.failed(e.getMessage());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseInfo<Void> handleError(WebExchangeBindException ex) {
        List<ObjectError> list = ex.getAllErrors();
        StringJoiner errorMsg = new StringJoiner(",");
        for (ObjectError objectError : list) {
            String defaultMessage = objectError.getDefaultMessage();
            if (objectError instanceof FieldError violationFieldError) {
                String filed = violationFieldError.getField();
                if (defaultMessage.contains("%s")) {
                    defaultMessage = String.format(defaultMessage, filed);
                } else {
                    defaultMessage = filed + defaultMessage;
                }
            }
            errorMsg.add(defaultMessage);
        }
        log.error("表单绑定或校验失败：{} ", errorMsg);
        return ResponseInfo.failed(errorMsg.toString());
    }

}
