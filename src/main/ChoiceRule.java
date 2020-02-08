package main;

import java.util.List;

public class ChoiceRule implements Rule{

    private Choice head;
    private List<Predicate> body;

    // no default constructor
    private ChoiceRule() {};

    public ChoiceRule(Choice head, List<Predicate> body){
        this.head = head;
        this.body = body;
    }

    public List<Predicate> getBody() {
        return body;
    }

    public Choice getHead() {
        return head;
    }
}
