package main;

import java.util.List;

public class VChoiceRule implements Rule {

    private List<Predicate> body;
    private List<Choice> heads;

    // no default constructor
    private VChoiceRule() {};

    public VChoiceRule(List<Choice> heads, List<Predicate> body){
        this.heads = heads;
        this.body = body;
    }

    public List<Choice> getHeads() {
        return heads;
    }

    public List<Predicate> getBody() {
        return body;
    }

    public Choice getHead(int i){
        return this.getHeads().get(i);
    }
    public Predicate getBody(int i) {
        return this.getBody().get(i);
    }
    public int getHeadsLength(){
        return this.getHeads().size();
    }
    public int getBodyLength(){
        return this.getBody().size();
    }
}
