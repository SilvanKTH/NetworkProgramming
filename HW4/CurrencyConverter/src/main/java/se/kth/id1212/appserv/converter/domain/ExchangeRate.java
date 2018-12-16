/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.appserv.converter.domain;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.PositiveOrZero;

/**
 *
 * @author silvanzeller
 */
@Entity
@Table(name="EXCHANGE_RATE")
public class ExchangeRate implements ExchangeRateDTO{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="EXCHANGE_RATE_ID")
    private long id;
    
    @OneToOne(cascade = CascadeType.ALL) 
    @JoinColumn(name = "CURRENCY_FROM", nullable = false) 
    private Currency curFrom;
	
    @OneToOne(cascade = CascadeType.ALL) 
    @JoinColumn(name = "CURRENCY_TO", nullable = false)
    private Currency curTo;
	
    @PositiveOrZero(message = "{exchangeRate.rate.negative}")
    @Column(name = "EXCHANGE_RATE")
    private double rate;
	
	
    @Version
    @Column(name = "EXCHANGE_OPTLOCK_VERSION")
    private int optLockVersion;
	
    public ExchangeRate(long id, Currency curFrom, Currency curTo, double rate) {
        super();
        this.id = id;
        this.curFrom = curFrom;
        this.curTo = curTo;
        this.rate=rate;
    }
    
    public ExchangeRate() {
	// TODO Auto-generated constructor stub
    }
	
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Currency getCurFrom() {
        return curFrom;
    }

    public void setCurFrom(Currency curFrom) {
        this.curFrom = curFrom;
    }

    public Currency getCurTo() {
        return curTo;
    }

    public void setCurTo(Currency curTo) {
        this.curTo = curTo;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    @Override
    public String toString() {
        return "Exchange Rate [id = " + id + ", curFrom = " + curFrom
                + ", curTo = " + curTo + ", rate = " + rate
                + ", optLockVersion = " + optLockVersion + "]";
    }

}

