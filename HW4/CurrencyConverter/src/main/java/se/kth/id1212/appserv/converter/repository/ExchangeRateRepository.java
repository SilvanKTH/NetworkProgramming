/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.appserv.converter.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.kth.id1212.appserv.converter.domain.ExchangeRate;

/**
 *
 * @author silvanzeller
 */

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Integer> {
    
    List<ExchangeRate> findAll();

    @SuppressWarnings("unchecked")
    ExchangeRate saveAndFlush(ExchangeRate convertRate);
}


//@Repository
//@Transactional(propagation = Propagation.MANDATORY)
//public interface ConvertRateRepo extends JpaRepository<ConvertRate, Integer> {
//	List<ConvertRate> findAll();
//	
//	@SuppressWarnings("unchecked")
//	ConvertRate saveAndFlush(ConvertRate convertRate);
//}
