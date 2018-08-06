package de.bs.tu.ifis.EYRE2018;

import de.bs.tu.ifis.model.Entity;

public class EyreEntity extends Entity {
    private final long eid;
    private final String entityClass;
    private final String elabel;
    private int tripleNum;
    private final String graph;

    public EyreEntity(final long eid, final String entityClass, final String elabel, final int tripleNum){
        super(elabel);
        //name is the end of the label
       // super(elabel.split("/resource/")[1]);

        this.eid = eid;
        this.entityClass = entityClass;
        this.elabel = elabel;
        this.tripleNum = tripleNum;
        this.graph = elabel.split("/resource/")[0] + "/resource/";
    }

    public long getEid() {
        return eid;
    }

    public String getEntityClass() {
        return entityClass;
    }

    public String getElabel() {
        return elabel;
    }

    public int getTripleNum() {
        return tripleNum;
    }

    public String getGraph() {
        return graph;
    }

    @Override
    public String toString() {
        return "EyreEntity{" +
                "eid=" + eid +
                ", entityClass='" + entityClass + '\'' +
                ", elabel='" + elabel + '\'' +
                ", tripleNum=" + tripleNum + '\'' +
                ", graph=" + graph + '\'' +
                ", name=" + getName() +
                '}';
    }
}
