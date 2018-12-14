/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.appserv.converter.presentation.currency;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

/**
 *
 * @author silvanzeller
 */
class CurrencyConverterForm {
    
    @NotBlank(message = "Please enter own currency")
    @Size(min = 1, max = 10, message = "Currency must have between 1-10 characters")
    private String ownCurrency;
    
    @NotBlank(message = "Please enter own currency")
    @Size(min = 1, max = 10, message = "Currency must have between 1-10 characters")
    private String destCurrency;
    
    @NotBlank(message = "Please enter the new Currency with decimal point, if needed")
    @PositiveOrZero
    private float amount;
    
    public String getOwnCurrency(){
        return ownCurrency;
    }
    
    public String getDestCurrency(){
        return destCurrency;
    }
    
    public float getAmount(){
        return amount;
    }
    
    public void setOwnCurrency(String ownCurrency){
        this.ownCurrency = ownCurrency;
    }
    
    public void setDestCurrency(String destCurrency){
        this.destCurrency = destCurrency;
    }
    
    public void setAmount(){
        this.amount = amount;
    }
}
