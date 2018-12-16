/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.appserv.converter.presentation.currency;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import se.kth.id1212.appserv.converter.application.CurrencyConverterService;
import se.kth.id1212.appserv.converter.domain.Counter;

/**
 *
 * @author silvanzeller
 */
@Controller
class ViewController {

    //used for url handling 
    static final String DEFAULT_PAGE_URL = "/";
    static final String INDEX_URL = "start";
    static final String CLIENT_PAGE_URL = "client";
    static final String ADMIN_PAGE_URL = "admin";
    static final String RESULT_URL = "result";
    static final String CHANGE_RATE_RESULT_URL = "change-result";

    
    //used to pass name to html form and link to java object 
    private static final String CALCULATE_FORM = "calculateAmountForm";
    private static final String CHANGE_RATE_FORM = "changeRateForm";

    @Autowired
    
    private CurrencyConverterService service;

    //HTTP get request + hardcoded URL
    @GetMapping(DEFAULT_PAGE_URL)
    public String showDefaultView() {
        service.setUp();
        return  INDEX_URL;
    }
	
    @GetMapping("/" + CLIENT_PAGE_URL)
    public String showClientView(CalculateAmountForm calculateAmountForm) {
        service.setUp();
        return CLIENT_PAGE_URL;
    }

    @GetMapping("/"+ADMIN_PAGE_URL)
    public String showAdminView(ChangeRateForm changeRateForm,Model model) {
        service.setUp();
        Counter counter=service.getCounter();
        if(counter==null) {
            System.out.println("counter is null");
            model.addAttribute(ExceptionHandlers.ERROR_PATH,ExceptionHandlers.GENERIC_ERROR );
            return ExceptionHandlers.ERROR_PAGE_URL;
        }else {
            model.addAttribute("counter",counter);
            return ADMIN_PAGE_URL;
        }
    }

    //HTTP post request + hardcoded URL
    //@Valid checks the validity of an attribute
    @PostMapping("/"+RESULT_URL)
    public String getResultPage(@Valid @ModelAttribute(CALCULATE_FORM)CalculateAmountForm calculateAmountForm,
        BindingResult bindingResult,Model model) throws Exception{
        if (!bindingResult.hasErrors()) {
            CalculateAmountForm resultForm = service.calculateAmount(calculateAmountForm.getCurrFrom(), calculateAmountForm.getCurrTo(), calculateAmountForm.getAmountFrom());
            model.addAttribute(CALCULATE_FORM, resultForm);
            service.increaseCounter();
            return RESULT_URL;
        }else {
            model.addAttribute(ExceptionHandlers.ERROR_PATH,ExceptionHandlers.GENERIC_ERROR );
            return ExceptionHandlers.ERROR_PAGE_URL;
    }
}
    
    @PostMapping("/"+CHANGE_RATE_RESULT_URL)
    public String getChangeResultPage(@Valid @ModelAttribute(CHANGE_RATE_FORM)ChangeRateForm changeRateForm,
        BindingResult bindingResult,Model model) throws Exception{
        if (!bindingResult.hasErrors()) {
            service.printAllExchangeRates();
            service.printAllCurrencies();
            service.changeConvertRate(changeRateForm.getCurrFrom(), changeRateForm.getCurrTo(), changeRateForm.getNewRate());
            model.addAttribute(CHANGE_RATE_FORM	, changeRateForm);
            service.printAllExchangeRates();
            return CHANGE_RATE_RESULT_URL;
        } else {
            model.addAttribute(ExceptionHandlers.ERROR_PATH,ExceptionHandlers.GENERIC_ERROR );
            return ExceptionHandlers.ERROR_PAGE_URL;
        }
    }
}
