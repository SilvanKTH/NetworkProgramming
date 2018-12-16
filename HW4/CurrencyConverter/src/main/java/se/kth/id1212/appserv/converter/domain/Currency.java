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
import javax.persistence.EntityManager;
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
public class Currency implements CurrencyDTO{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="CURRENCY_ID")
    private int id;
	
	
    @NotNull(message = "{currency.name.missing}")
    @Pattern(regexp = "^[a-zA-Z]+$", message = "{currency.name.invalid-char}")
    @Size(min = 2, max = 30, message = "{currency.name.length}")
    @Column(name = "CURRENCY_NAME")
    private String name;
	
    @Version
    @Column(name = "CURRENCY_OPTLOCK_VERSION")
    private int optLockVersion;

    public Currency(int id, String name) {
        this.id = id;
        this.name = name;
    }
    public Currency() {
        // TODO Auto-generated constructor stub
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Currency [id=" + id + ", name=" + name + "]";
    }
}
