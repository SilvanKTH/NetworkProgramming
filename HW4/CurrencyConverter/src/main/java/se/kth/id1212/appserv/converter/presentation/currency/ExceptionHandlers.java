/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.appserv.converter.presentation.currency;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 *
 * @author silvanzeller
 */
@Controller
@ControllerAdvice
public class ExceptionHandlers implements ErrorController {
    public static final String ERROR_PAGE_URL = "error";
    public static final String ERROR_TYPE_KEY = "errorType";
    public static final String ERROR_INFO_KEY = "errorInfo";
    public static final String GENERIC_ERROR = "Operation Failed";
    public static final String GENERIC_ERROR_INFO = "Sorry, it didn't work. Please try again.";
    public static final String HTTP_404 = "The page could not be found";
    public static final String HTTP_404_INFO = "Sorry, but there is no such page. We would like to fix this error, please tell us what you where trying to do.";
    static final String ERROR_PATH = "failure";

   
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception exception, Model model) {
        model.addAttribute(ERROR_TYPE_KEY, GENERIC_ERROR);
        return ERROR_PAGE_URL;
    }

    @GetMapping("/" + ERROR_PATH)
    public String handleHttpError(HttpServletRequest request, HttpServletResponse response, Model model) {
        int statusCode = Integer.parseInt(extractHttpStatusCode(request));
        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            model.addAttribute(ERROR_TYPE_KEY, HTTP_404);
            model.addAttribute(ERROR_INFO_KEY, HTTP_404_INFO);
            response.setStatus(statusCode);
        } else {
            model.addAttribute(ERROR_TYPE_KEY, GENERIC_ERROR);
            model.addAttribute(ERROR_INFO_KEY, GENERIC_ERROR_INFO);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return ERROR_PAGE_URL;
    }

    private String extractHttpStatusCode(HttpServletRequest request) {
        return request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE).toString();
    }

    @Override
    public String getErrorPath() {
        return "/" + ERROR_PATH;
    }
}

