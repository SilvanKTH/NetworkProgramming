/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.appserv.converter.application;

import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.kth.id1212.appserv.converter.domain.Currency;
import se.kth.id1212.appserv.converter.domain.CurrencyDTO;
import se.kth.id1212.appserv.converter.domain.CurrencyException;
import se.kth.id1212.appserv.converter.repository.CurrencyRepository;

/**
 *
 * @author silvanzeller
 */

// Service is responsible for application functions 
// In this instance: 
// 1) creating a currency (with exchange rate) 
// 2) finding a currency and returning currency plus exchange rate

@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
@Service
public class CurrencyConverterService {
    
    public Set<Currency> currencies = new HashSet<>();
    
    @Autowired
    private CurrencyRepository currencyRepository; // object for the database integration
    
    public CurrencyDTO createCurrency(String currency, float exchangeRate) throws CurrencyException { // check for exchange rate >0
        if (exchangeRate < 0){
            throw new CurrencyException("Exchange rate cannot be negative");
        }
        //currencyRepository.save(currency, exchangeRate);
        Currency C = new Currency(currency, exchangeRate);
        currencies.add(C);
        
        currencyRepository.save(C); // saves new Currency object 
        CurrencyDTO cdto = C;
        return cdto;
        //return currencyRepository.findCurrencyByName(currency);
    }
    
    public CurrencyDTO findCurrency(String currency) throws CurrencyException{ //check for non-existing currency
        if (currency == null){
            throw new CurrencyException("Currency "+currency+" has no entry in the database");
        }
        
        CurrencyDTO C = new Currency(currency, 0);
        //CurrencyDTO cdto = C;
        return C;
        //return currencyRepository.findCurrencyByName(currency);
    }
    
    public Set<Currency> getAllCurrencies() {
        return currencies;
    }
    
    

}
