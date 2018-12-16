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
    /*
    * these strings are used to return URL to view resolver, thymeleaf default prefix is /resources/templates/, default suffix is .html
    * they are returned to thymeleaf and thymeleaf will add the prefix and suffix then return the whole url to client browser
    */
    static final String DEFAULT_PAGE_URL="/";
    static final String INDEX_URL="start";
    static final String CLIENT_PAGE_URL="client";
    static final String ADMIN_PAGE_URL="admin";
    static final String RESULT_URL="result";
    static final String CHANGE_RATE_RESULT_URL="change-result";
    /*
     * these strings are used to pass form name to thymeleaf view
     * form name are used as key to map the corresponding object that carries information 
     * 
     * notice: first letter of the key should be in lower-case
     */
    private static final String CALCULATE_FORM="calculateAmountForm";
    private static final String CHANGE_RATE_FORM="changeRateForm";

    @Autowired
    
    private CurrencyConverterService service;
	
	/*
	 * map the get request from default url
	 */
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
    /*
     * model: the registry table of the result page
     */
    @GetMapping("/"+ADMIN_PAGE_URL)
    public String showAdminView(ChangeRateForm changeRateForm,Model model) {
        service.setUp();
        Counter counter=service.getCounter();
        if(counter==null) {
            System.out.println("counter is null");
            model.addAttribute(ExceptionHandlers.ERROR_PATH,ExceptionHandlers.GENERIC_ERROR );
            return ExceptionHandlers.ERROR_PAGE_URL;
        }else {
            //register the counter object to the model, the registry key is the class name of the object with first letter in lower case (Counter->"counter")
            model.addAttribute("counter",counter);
            return ADMIN_PAGE_URL;
        }
    }
    /*
     * on client page the form will call POST method to /result
     * the request will be mapped to this method
     * 
     * @Valid will check the legality of fields and return the binding result, still unclear about how it works and how the bindingResult is generated
     */
    @PostMapping("/"+RESULT_URL)
    public String getResultPage(@Valid @ModelAttribute(CALCULATE_FORM)CalculateAmountForm calculateAmountForm,
        BindingResult bindingResult,Model model) throws Exception{
        if (!bindingResult.hasErrors()) {
            CalculateAmountForm resultForm=service.calculateAmount(calculateAmountForm.getCurrFrom(), calculateAmountForm.getCurrTo(), calculateAmountForm.getAmountFrom());
            System.out.println("test:"+resultForm.toString());
            model.addAttribute(CALCULATE_FORM, resultForm);
            service.addCounterByOne();
            return RESULT_URL;
        }else {
            model.addAttribute(ExceptionHandlers.ERROR_PATH,ExceptionHandlers.GENERIC_ERROR );
            return ExceptionHandlers.ERROR_PAGE_URL;
    }
}
    
    @PostMapping("/"+CHANGE_RATE_RESULT_URL)
    public String getChangeReultPage(@Valid @ModelAttribute(CHANGE_RATE_FORM)ChangeRateForm changeRateForm,
        BindingResult bindingResult,Model model) throws Exception{
        if (!bindingResult.hasErrors()) {
            service.printAllConvertRate();
            service.printAllCurrency();
            service.changeConvertRate(changeRateForm.getCurrFrom(), changeRateForm.getCurrTo(), changeRateForm.getNewRate());
            model.addAttribute(CHANGE_RATE_FORM	, changeRateForm);
            service.printAllConvertRate();
            return CHANGE_RATE_RESULT_URL;
        } else {
            model.addAttribute(ExceptionHandlers.ERROR_PATH,ExceptionHandlers.GENERIC_ERROR );
            return ExceptionHandlers.ERROR_PAGE_URL;
        }
    }
}
