/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.appserv.converter.presentation.currency;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author silvanzeller
 */
class CreateCurrencyForm {
    
    @NotBlank(message = "Please enter the new Currency as a String between 1-10 characters")
    @Size(min = 1, max = 10, message = "Currency must have between 1-10 characters")
    private String currency;
    
    @NotBlank(message = "Please enter the new Currency with decimal point")
    @NotNull(message = "Please specify exchange rate") // negative amounts will be handled with an exception
    private float exchangeRate;
    
    public String getCurrency(){
        return currency;
    }
    
    public float getExchangeRate(){
        return exchangeRate;
    }
    
    public void setCurrency(String currency){
        this.currency = currency;
    }
    
    public void setExchangeRate(float exchangeRate){
        this.exchangeRate = exchangeRate;
    }
}
