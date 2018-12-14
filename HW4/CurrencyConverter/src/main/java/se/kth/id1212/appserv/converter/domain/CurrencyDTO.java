/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.appserv.converter.domain;

import java.util.List;
import java.util.Set;

/**
 *
 * @author silvanzeller
 */
public interface CurrencyDTO {
    boolean getOwnCurrency(String currencyName);
    boolean getDestCurrency(String currencyName);
    float getExchangeRate(String currencyName);

}
