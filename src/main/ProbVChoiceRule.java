package main;

import java.util.List;

public class ProbVChoiceRule extends VChoiceRule implements Rule {

    private List<Double> headProbs;


    public ProbVChoiceRule(List<Choice> heads, List<Double> headProbs, List<Predicate> body) {
        super(heads, body);
        this.headProbs = headProbs;
    }

    public List<Double> getHeadProbs() {
        return headProbs;
    }
}
