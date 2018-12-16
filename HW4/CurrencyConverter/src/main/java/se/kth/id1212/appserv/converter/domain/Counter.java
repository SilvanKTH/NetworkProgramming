/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package se.kth.id1212.appserv.converter.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 *
 * @author silvanzeller
 */
@Entity
@Table(name = "COUNTER")
public class Counter implements CounterDTO {
	
    @Id
    @Column(name = "COUNTER_ID")
    private long id;
    
    @Column(name = "count")
    private int count;

    @Version
    @Column(name = "COUNTER_OPTLOCK_VERSION")
    private int optLockVersion;
    
    public Counter() {
        // TODO Auto-generated constructor stub
    }
	
    public Counter(long id,int count) {
        this.id = id;
        this.count = count;
    }
	
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void addCount() {
        this.count++;
    }
	
    public int getCounter() {
        return this.count;
    }

    public void setCounter(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Counter [id = " + id + ", count = " + count + "]";
    }
}

