/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.appserv.converter.repository;

import java.util.List;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.kth.id1212.appserv.converter.domain.Currency;
import se.kth.id1212.appserv.converter.domain.CurrencyDTO;

/**
 *
 * @author silvanzeller
 */

// Extending the JpaRepository grants access to functions such as save etc.. 
// It also allows for creating custom functions --> way to convert functions into JPQL calls
// See spring-data jpa doc  

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface CurrencyRepository extends JpaRepository<Currency, Long> { // <Entity Type, Id Type>
    
    Currency findCurrencyByName(String name);

}
