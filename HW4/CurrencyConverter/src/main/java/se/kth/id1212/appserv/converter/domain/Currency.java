/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.appserv.converter.domain;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

/**
 *
 * @author silvanzeller
 */

@Entity
@Table (name = "CURRENCY") //creates table named currency
public class Currency implements CurrencyDTO {
    private static final String SEQUENCE_NAME_KEY = "SEQ_NAME";
    
//    @OneToOne
//    private Set<Currency> currencies = new HashSet<>(); //holds all currencies

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQUENCE_NAME_KEY)
    @SequenceGenerator(name = SEQUENCE_NAME_KEY, sequenceName = "CURRENCY_SEQUENCE") 
    @Column(name = "CURRENCY_ID") //same as column in hibernate
    private Long id;   
    
    @NotNull()
    //@Size(min = 2, max = 10)
    @Column(name = "CURRENCY") // column name in hibernate
    private String currency;
    
    @Pattern(regexp = "\\d+") //regex for positive digits only
    @PositiveOrZero() // only positive values (unfortunately 0 is also possible)
    @Column(name = "EXCHANGE_RATE") // column name in hibernate
    private float exchangeRate;
    
    @Pattern(regexp = "\\d+") //regex for positive digits only
    @PositiveOrZero() // only positive values
    private float amount;
    
    public Currency (String currency, float exchangeRate){
        this.currency = currency;
        this.exchangeRate = exchangeRate;
    }
    
    protected Currency(){ // required by JPA, will not be utilized
    }
    
     @Override
    public boolean getOwnCurrency(String currency) {
        boolean exists = getCurrency(currency);
        return exists;
    }

    @Override
    public boolean getDestCurrency(String currency) {
        boolean exists = getCurrency(currency);
        return exists;
    }
    
    private boolean getCurrency(String currency){
        return false;
//        return currencies.contains(currency);
    }

    @Override
    public float getExchangeRate(String currency) {
        if(getCurrency(currency)){
            return exchangeRate;
        }
        // error handling here
        return -1;
    }
    
    public float calcExchange(String ownCurrency, String destCurrency, float amount){
        if(!getOwnCurrency(ownCurrency)){
            // error handling here
            return -1;
        } 
        else if(!getDestCurrency(destCurrency)){
            // error handling here
            return -1;
        }
        else{
            return amount * (getExchangeRate(destCurrency)/getExchangeRate(ownCurrency));
        }       
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//    @Override
//    public void setCurrencyAndExchangeRate(String currency, float exchangeRate) {
//        
//    }
//    
  
    public void setCurrency(String currency){
        this.currency = currency;
    }
    
    public void setExchangeRate(float exchangeRate){
        this.exchangeRate = exchangeRate;
    }
    
    public void setAmount(float amount){
        this.amount = amount;
    }
    
    public float getAmount(){
        return amount;
    }
}
