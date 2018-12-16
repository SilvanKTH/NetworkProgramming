/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.appserv.converter.application;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.kth.id1212.appserv.converter.domain.Counter;
import se.kth.id1212.appserv.converter.domain.Currency;
import se.kth.id1212.appserv.converter.domain.CurrencyDTO;
import se.kth.id1212.appserv.converter.domain.CurrencyException;
import se.kth.id1212.appserv.converter.domain.ExchangeRate;
import se.kth.id1212.appserv.converter.presentation.currency.CalculateAmountForm;
import se.kth.id1212.appserv.converter.repository.CounterRepository;
import se.kth.id1212.appserv.converter.repository.CurrencyRepository;
import se.kth.id1212.appserv.converter.repository.ExchangeRateRepository;

/**
 *
 * @author silvanzeller
 */

// Service is responsible for application functions 
// In this instance: 
// 1) creating a currency
// 2) creating exchange rates
// 3) finding a currency and returning currency plus exchange rate

@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
@Service
public class CurrencyConverterService {
    
    @Autowired
    private CounterRepository counterRepository; // object for the database integration
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private ExchangeRateRepository convertRateRepository;

    private final long COUNTER_ID=1;

    private boolean DB_SETUP_STATE=false;
    
    
    public void setUp() {
    	if(DB_SETUP_STATE) {
    		return;
    	}
    	
        Counter c = new Counter(COUNTER_ID,0);
    	counterRepository.saveAndFlush(c);
    	
    	List<Currency> currencyList=new ArrayList<>();
        Currency USD = new Currency(1,"USD");
        Currency SEK = new Currency(2,"SEK");
    	Currency EUR = new Currency(3,"EUR");
    	Currency YEN = new Currency(4,"YEN");
    	currencyList.add(SEK);
    	currencyList.add(EUR);
    	currencyList.add(USD);
    	currencyList.add(YEN);
    	currencyRepository.saveAll(currencyList);
    	
    	List<ExchangeRate> exchangeRateList=new ArrayList<>();
    	ExchangeRate er1 = new ExchangeRate(1,USD,USD,1);
    	ExchangeRate er2 = new ExchangeRate(2,SEK,USD,0.11);
    	ExchangeRate er3 = new ExchangeRate(3,EUR,USD,1.13);
    	ExchangeRate er4 = new ExchangeRate(4,YEN,USD,0.0088);
    	exchangeRateList.add(er1);
    	exchangeRateList.add(er2);
    	exchangeRateList.add(er3);
    	exchangeRateList.add(er4);
    	convertRateRepository.saveAll(exchangeRateList);
    	DB_SETUP_STATE=true;
    	
    }

    public void printAllCounter() {
        List<Counter> counters = counterRepository.findAll();
        for(Counter c:counters){
            System.out.println(c.toString());
	}
    }
	
    public void printAllConvertRate() {
	List<ExchangeRate> rates = convertRateRepository.findAll();
        for(ExchangeRate cr:rates){
            System.out.println(cr.toString());
	}
    }
	
    public void printAllCurrency() {
	List<Currency>	currencies = currencyRepository.findAll();
        for(Currency c:currencies) {
            System.out.println(c.toString());
        }
    }
	
	
    public Counter getCounter() {
	try {	
            List<Counter> counters = counterRepository.findAll();
            if(counters.size() == 0) {
                Counter counter = new Counter(COUNTER_ID,0);
		counterRepository.save(counter);
            }
            Counter counter = counterRepository.findCounterById(COUNTER_ID);
            return counter;
	} catch (Exception e) {
            e.printStackTrace();
            return null;
	}
    }
    public void addCounterByOne() {
        try {
            Counter counter = counterRepository.findCounterById(COUNTER_ID);
            counter.addCount();
            counterRepository.saveAndFlush(counter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
    public double getConvertRate(String fromCurr, String toCurr) throws Exception{
        List<ExchangeRate> convertRates = convertRateRepository.findAll();
        for(ExchangeRate cr : convertRates) {
            if(cr.getCurFrom().getName().equals(fromCurr) && cr.getCurTo().getName().equals(toCurr)) {
                return cr.getRate();
            }
        }
        throw new Exception("Could not find currency in DB");
    }
	
    public void changeConvertRate(String fromCurr, String toCurr, double newRate) throws Exception{
        List<ExchangeRate> convertRates = convertRateRepository.findAll();
	for(ExchangeRate cr : convertRates) {
            if(cr.getCurFrom().getName().equals(fromCurr) && cr.getCurTo().getName().equals(toCurr)) {
                cr.setRate(newRate);
		convertRateRepository.saveAndFlush(cr);
		System.out.println("changed exchange rates "+fromCurr+" to "+toCurr);
            }
            if(cr.getCurFrom().getName().equals(toCurr) && cr.getCurTo().getName().equals(fromCurr)) {
		cr.setRate(1.0/newRate);
		convertRateRepository.saveAndFlush(cr);
		System.out.println("changed exchange rates "+toCurr+" to "+fromCurr);
            }
	}
    }
	
	public CalculateAmountForm calculateAmount(String fromCurr, String toCurr, double amount) throws CurrencyException, Exception{
            CalculateAmountForm form=new CalculateAmountForm();
            form.setCurrFrom(fromCurr);
            form.setCurrTo(toCurr);
            form.setAmountFrom(amount);
            double convertRate=getConvertRate(fromCurr, toCurr);
            form.setAmountTo(amount*convertRate);
            return form;
	}

}
    