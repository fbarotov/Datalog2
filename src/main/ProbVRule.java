package main;

import java.util.List;

public class ProbVRule extends VRule implements Rule {

    private List<Double> headProbs;

    public ProbVRule(List<Predicate> heads, List<Double> headProbs, List<Predicate> body) {
        super(heads, body);
        this.headProbs = headProbs;
    }
}
