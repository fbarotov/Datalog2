package main;

import java.util.List;

public class VRule implements Rule{

    private List<Predicate> heads;
    private List<Predicate> body;

    // no default constructor
    private VRule() {};

    public VRule(List<Predicate> heads, List<Predicate> body){
        this.heads = heads;
        this.body = body;
    }

    public List<Predicate> getHeads() {
        return heads;
    }

    public Predicate getHead(int i){
        return this.getHeads().get(i);
    }

    public List<Predicate> getBody() {
        return body;
    }

    public Predicate getBody(int i){
        return this.getBody().get(i);
    }

    public int getHeadsLength(){
        return this.heads.size();
    }
    public int getBodyLength() {
        return this.body.size();
    }

    @Override
    public String toString() {
        String str = "";
        for(int i=0; i < this.getHeadsLength(); i++){
            str = str + this.getHead(i).toString() + ", ";
        }
        str = str + " :- ";
        for(int i=0; i < this.getBodyLength(); i++){
            str = str + this.getBody(i) + ", ";
        }
        return str;
    }

}
