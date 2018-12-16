/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.appserv.converter.presentation.currency;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;

/**
 *
 * @author silvanzeller
 */
public class ChangeRateForm {
    @NotBlank(message = "Please specify currency name you want change to convert from")
       // The regex below should permit only characters, but asterisk is
       // unfortunately also valid.
    @Pattern(regexp = "^[a-zA-Z]+$")
    private String currFrom;
	 
    @NotBlank(message = "Please specify currency name you want change to convert to")
       // The regex below should permit only characters, but asterisk is
       // unfortunately also valid.
    @Pattern(regexp = "^[a-zA-Z]+$")
    private String currTo;

    @NotNull(message = "Please specify the new rate")
    @PositiveOrZero(message = "the amount must be zero or greater")
    
    private Double newRate;
	
    public String getCurrFrom() {
        return currFrom;
    }

    public void setCurrFrom(String currFrom) {
        this.currFrom = currFrom;
    }

    public String getCurrTo() {
        return currTo;
    }

    public void setCurrTo(String currTo) {
        this.currTo = currTo;
    }

    public Double getNewRate() {
        return newRate;
    }

    public void setNewRate(Double newRate) {
        this.newRate = newRate;
    }

    @Override
    public String toString() {
        return "ChangeRateForm [fromCurr=" + currFrom + ", toCurr=" + currTo + ", newRate=" + newRate + "]";
    }

}
