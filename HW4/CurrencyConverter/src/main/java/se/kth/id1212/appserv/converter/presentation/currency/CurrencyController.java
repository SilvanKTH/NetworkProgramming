/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.appserv.converter.presentation.currency;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import se.kth.id1212.appserv.converter.application.CurrencyConverterService;
import se.kth.id1212.appserv.converter.domain.Currency;
import se.kth.id1212.appserv.converter.domain.CurrencyDTO;
import se.kth.id1212.appserv.converter.domain.CurrencyException;

/**
 *
 * @author silvanzeller
 */

// Resposible for redirection to appropriate URL 
// Includes the appropriate form objects

// 1) parses HTTP request 
// 1a) validates data
// 1b) exception handling
// 2) invokes handling bean in Spring, e.g. model handling or database access
// 3) invokes view generator -> which is responsible for HTML  

@Controller
@Scope("session")
public class CurrencyController {
    static final String DEFAULT_URL = "/";
    static final String CURRENCY_URL = "currency-converter"; // start page, currently only page displayed
    
    private static final String OWN_CURRENCY_OBJ_NAME = "ownCurrency"; // use in html form for Currency Converter fragment
    private static final String DEST_CURRENCY_OBJ_NAME = "destCurrency"; 
    private static final String CURRENCY_CONVERTER_FORM_OBJ_NAME = "currencyConverterForm";
    
    private static final String NEW_CURRENCY_OBJ_NAME = "newCurrency"; // use in html form for Create Currency fragment
    private static final String NEW_EXCHANGE_RATE_OBJ_NAME = "exchangeRate";
    private static final String CREATE_CURRENCY_FORM_OBJ_NAME = "createCurrencyForm";
    
    @Autowired
    private CurrencyConverterService currencyConverterService; // Spring instantiates an object of CCS --> can find and create currencies
    
    private CurrencyDTO currency;
    private CurrencyDTO ownCurrency;
    private CurrencyDTO destCurrency;
    
    @GetMapping(DEFAULT_URL) // Responds to a HTTP get request
    public String showDefaultPage(){ // redirects to the default page 
        return "redirect:"+CURRENCY_URL;
    }
    
    @GetMapping(DEFAULT_URL+CURRENCY_URL)
    public String showCurrencyPage(CurrencyConverterForm currencyConverterForm, CreateCurrencyForm createCurrencyForm){ // renders the currency page
        return CURRENCY_URL;
    }
    
    // @PostMapping Responds to a HTTP post request; 
    // @Valid validates according to class constraints
    // BindingResult holds results of validation
    // Model holds map of key-value pairs
    @PostMapping(DEFAULT_URL+CURRENCY_URL+"-create")
    public String createNewCurrency(@Valid CreateCurrencyForm createCurrencyForm, BindingResult bindingResult, Model model) throws CurrencyException{
        if(bindingResult.hasErrors()){ // if wrong data was entered
            model.addAttribute(CURRENCY_CONVERTER_FORM_OBJ_NAME, new CurrencyConverterForm()); // page is returned with new instance of FindAcctForm
            return CURRENCY_URL;
        }
        currency = currencyConverterService.createCurrency(createCurrencyForm.getCurrency(), createCurrencyForm.getExchangeRate());
        return CURRENCY_URL;
    }
    
    @PostMapping(DEFAULT_URL+CURRENCY_URL+"-convert")
    public String convertCurrency(@Valid CurrencyConverterForm currencyConverterForm, BindingResult bindingResult, Model model) throws CurrencyException{
        if(bindingResult.hasErrors()){
            model.addAttribute(CREATE_CURRENCY_FORM_OBJ_NAME, new CreateCurrencyForm());
            return CURRENCY_URL;
        }
        ownCurrency = currencyConverterService.findCurrency(currencyConverterForm.getOwnCurrency());
        destCurrency = currencyConverterService.findCurrency(currencyConverterForm.getDestCurrency());
        return CURRENCY_URL;
    }
}


