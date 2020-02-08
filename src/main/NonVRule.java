package main;

import java.util.List;

public class NonVRule implements Rule {

    private Predicate head;
    private List<Predicate> body;

    // no default constructor
    private NonVRule() {};

    public NonVRule(Predicate head, List<Predicate> body){
        this.head = head;
        this.body = body;
    }

    public List<Predicate> getBody() {
        return body;
    }
    public Predicate getBody(int i){
        return this.getBody().get(i);
    }

    public Predicate getHead() {
        return head;
    }
    public int getBodyLength(){
        return this.getBody().size();
    }

}
