package de.bs.tu.ifis.analysis.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

public class PredicateAnalysis {
    @XmlAttribute
    private Long amountInDatabase = null;


    public Long getAmountInDatabase() {
        return amountInDatabase;
    }

    public void setAmountInDatabase(long amountInDatabase) {
        this.amountInDatabase = amountInDatabase;
    }
}
