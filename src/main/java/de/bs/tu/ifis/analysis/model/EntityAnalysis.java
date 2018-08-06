package de.bs.tu.ifis.analysis.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.NONE)
public class EntityAnalysis {
    @XmlAttribute
    private Long incomingPredicates = null;
    @XmlAttribute
    private Long outgoingPredicates = null;
    @XmlAttribute
    private Long ontologyDepth = null;
    @XmlAttribute
    private Long subtreeSize = null;
    @XmlAttribute
    private Double pagerankScore = null;

    public Long getIncomingPredicates() {
        return incomingPredicates;
    }

    public void setIncomingPredicates(long incomingPredicates) {
        this.incomingPredicates = incomingPredicates;
    }

    public Long getOutgoingPredicates() {
        return outgoingPredicates;
    }

    public void setOutgoingPredicates(long outgoingPredicates) {
        this.outgoingPredicates = outgoingPredicates;
    }

    public Long getOntologyDepth() {
        return ontologyDepth;
    }

    public void setOntologyDepth(Long ontologyDepth) {
        this.ontologyDepth = ontologyDepth;
    }

    public Long getSubtreeSize() {
        return subtreeSize;
    }

    public void setSubtreeSize(Long subtreeSize) {
        this.subtreeSize = subtreeSize;
    }

    public Double getPagerankScore() {
        return pagerankScore;
    }

    public void setPagerankScore(Double pagerankScore) {
        this.pagerankScore = pagerankScore;
    }
}
