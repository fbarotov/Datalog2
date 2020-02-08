package main;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.scenario.effect.impl.prism.PrDrawable;
import main.term.Constant;
import main.term.Term;
import main.term.Variable;
import test.Generator;
import test.ProbVChoiceRuleTest;

import javax.lang.model.type.ErrorType;
import javax.xml.crypto.Data;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Engine {

    public static List<Predicate> nonVRuleExec(List<NonVRule> rules, List<Predicate> _facts){

        List<Predicate> facts = new ArrayList<>(_facts);

        AtomicBoolean isNewFactsDerived = new AtomicBoolean(true);
        while (isNewFactsDerived.get()) {
            isNewFactsDerived.set(false);

            rules.stream().forEach(
                    rule -> {
                        Pair<Predicate, List<Predicate>> result = Datalog.join(rule.getBody(), facts);
                        List<Predicate> newFacts = extract(result, rule.getHead());
                        if(newFacts != null && !newFacts.isEmpty()){
                            facts.addAll(newFacts);
                            isNewFactsDerived.set(true);
                        }
                    }
            );
        }
        return facts;
    }

    public static List<Predicate> VRuleExec(List<VRule> rules, List<Predicate> _facts){
        List<Predicate> facts = new ArrayList<>(_facts);

        rules.stream().forEach(
                rule -> {
                    Pair<Predicate, List<Predicate>> result = Datalog.join(rule.getBody(), facts);
                    List<List<Predicate>> space = new ArrayList<>( extract(result, rule.getHeads()) );

                    List<Predicate> derivedFacts = new ArrayList<>();
                    Random rnd = new Random();
                    for(int i=0; i < space.get(0).size(); i++){
                        int choose = rnd.nextInt(space.size());
                        derivedFacts.add( space.get(choose).get(i) );
                    }
                    if(derivedFacts != null && !derivedFacts.isEmpty()){
                        facts.addAll(derivedFacts);
                    }
                }
        );
        return facts;
    }

    public static List<Predicate> ChoiceExec(List<Choice> choices, List<Predicate> _facts){
        List<Predicate> facts = new ArrayList<>(_facts);


        Random rnd = new Random();
        choices.stream().forEach(
                choice -> {
                    Pair<Predicate, List<Predicate>> result = Datalog.join(choice.getBody(), facts);
                    List<Predicate> candidates = extract(result, choice.getHead());
                    int choose = rnd.nextInt(candidates.size());
                    facts.add(candidates.get(choose));
                }
        );
        return facts;
    }

    public static List<Predicate> choiceRuleExec(List<ChoiceRule> rules, List<Predicate> _facts){
        List<Predicate> facts = new ArrayList<>(_facts);

        Random rnd = new Random();
        rules.stream().forEach(
                rule -> {
                    Pair<Predicate, List<Predicate>> pair = Datalog.join(rule.getBody(), facts);

                    List<Predicate> intermediate_body = new ArrayList<>(rule.getHead().getBody());
                    intermediate_body.add(pair.getFirstVal());

                    List<Predicate> intermediate_facts = new ArrayList<>(facts);
                    intermediate_facts.addAll(pair.getSecondVal());

                    Pair<Predicate, List<Predicate>> result =  Datalog.join(intermediate_body, intermediate_facts);

                    List<Predicate> candidates = extract(result, rule.getHead().getHead());
                    int choose = rnd.nextInt(candidates.size());
                    facts.add(candidates.get(choose));
                }
        );
        return facts;
    }
    public static List<Predicate> VChoiceRuleExec(List<VChoiceRule> rules, List<Predicate> _facts){
        List<Predicate> facts = new ArrayList<>(_facts);

        Random rnd = new Random();

        rules.stream().forEach(
                rule -> {
                    Pair<Predicate, List<Predicate>> pair = Datalog.join(rule.getBody(), facts);
                    List<Predicate> intermediate_facts = choiceRuleExecHelper(rule, facts).stream().flatMap(List::stream).collect(Collectors.toList());
                    int choose = rnd.nextInt( intermediate_facts.size() );
                    facts.add( intermediate_facts.get(choose) );
                }
        );
        return facts;
    }

    private static List<List<Predicate>> choiceRuleExecHelper(VChoiceRule rule, List<Predicate> _facts){

        List<Predicate> facts = new ArrayList<>(_facts);

        Pair<Predicate, List<Predicate>> pair = Datalog.join(rule.getBody(), facts);
        if(pair == null || pair.getSecondVal() == null || pair.getSecondVal().size() == 0){
            return new ArrayList<>();
        }

        List<List<Predicate>> intermediate_facts = new ArrayList<>();

        rule.getHeads().stream().forEach(
                head -> {
                    List<Predicate> inputFacts = new ArrayList<>(facts);
                    inputFacts.addAll(pair.getSecondVal());

                    List<Predicate> intermediate_body = new ArrayList<>(head.getBody());
                    intermediate_body.add(pair.getFirstVal());

                    Pair<Predicate, List<Predicate>> result = Datalog.join(intermediate_body, inputFacts);
                    intermediate_facts.add(extract(result, head.getHead()));
                }
        );
        return intermediate_facts;
    }

    public static List<Predicate> executeRules(List<Rule> rules, List<Predicate> _facts){

        List<Predicate> facts = new ArrayList<>(_facts);
        /*
        * execute each rule through some iteration.
        * TODO: implement rule dependency. Ex: rule executed later might affect the one executed earlier
        */
        for(Rule rule: rules){
            // choice
            if(rule.getClass() == Choice.class){
                List<Choice> choices = new ArrayList<>();
                choices.add( (Choice) rule);
                facts = Generator.normalize( Engine.ChoiceExec(choices, facts) );
            }
            else if( rule.getClass() == ProbVChoiceRule.class ){

                ProbVChoiceRule probVChoiceRule = (ProbVChoiceRule) rule;

                facts = executeSingleProbVChoiceRule(probVChoiceRule, facts);
            }
        }
        return facts;
    }

    private static List<Predicate> executeSingleProbVChoiceRule( ProbVChoiceRule rule, List<Predicate> _facts ){

        List<ProbVChoiceRule> rules = new ArrayList<>();
        rules.add( rule );
        List<Predicate> facts = new ArrayList<>( _facts );
        List<Predicate> toBeConsidered = new ArrayList<>( _facts );
        do {
            List<Predicate> tempFacts = Generator.normalize( Engine.probVChoiceRuleExec(rules, toBeConsidered) );

            // exception might occur
            List<Predicate> finalToBeConsidered = toBeConsidered;
            List<Predicate> tmp = tempFacts.stream().filter(predicate -> !finalToBeConsidered.contains(predicate)).collect(Collectors.toList());
            if(tmp == null || tmp.size() == 0){
                System.out.println( "RESULT OF JOIN IS EMPTY... RETURNING" );
                break;
            }
            Predicate derived = tmp.get(0);

            toBeConsidered = tempFacts.stream().filter( predicate -> !predicate.getName().equals( derived.getName() ) ).collect(Collectors.toList());
            toBeConsidered.add( derived );
            facts.add( derived );

            boolean doesBodyContainPredicateLikeNewlyGeneratedAtom = rule.getBody().stream().anyMatch( predicate -> predicate.getName().equals(derived.getName()) );
            if( !doesBodyContainPredicateLikeNewlyGeneratedAtom ){
                break;
            }
        } while (true);
        return facts;
    }


    public static List<Predicate> probVChoiceRuleExec(List<ProbVChoiceRule> rules, List<Predicate> _facts){

        List<Predicate> facts = new ArrayList<>(_facts);

        rules.stream().forEach(
                rule -> {
                    List<List<Predicate>> derived = choiceRuleExecHelper(rule, facts);
                    List<Double> probs = rule.getHeadProbs();

                    /*
                     * @TODO: this part is for evaluating the rule using max_probability inference
                    Map<Predicate, Double> predToProb = new HashMap<>();
                    for(int i=0; i < derived.size(); i++) {
                        for (int j = 0; j < derived.get(i).size(); j++) {
                            Predicate predicate = derived.get(i).get(j);
                            Double prob = predToProb.get(predicate);
                            if (prob == null) {
                                predToProb.put(predicate, probs.get(i) / derived.get(i).size());
                            }
                            else{
                                predToProb.put(predicate, prob + probs.get(i)/derived.get(i).size());
                            }
                        }
                    }
                    Predicate chosen = Collections.max(predToProb.entrySet(), Comparator.comparingDouble(Map.Entry::getValue)).getKey();
                    */

                    // second way:
                    if(derived != null && derived.size() > 0) {
                        int chosenIdx = RandomHelper.chooseRandomIdx(probs);
                        List<Predicate> chosenSetOfPreds = derived.get(chosenIdx);

                        // if chosen head is empty, choose some other ###
                        if(chosenSetOfPreds == null || chosenSetOfPreds.size() == 0){
                            for(int i=0; i < derived.size(); i++){
                                List<Predicate> chosenAtI = derived.get(i);
                                if(chosenAtI != null && chosenAtI.size() > 0){
                                    chosenSetOfPreds = chosenAtI;
                                    break;
                                }
                            }
                        }
                        // end of ###

                        if (chosenSetOfPreds != null && chosenSetOfPreds.size() > 0) {
                            Predicate chosen = chosenSetOfPreds.get(new Random().nextInt(chosenSetOfPreds.size()));
                            // end of second way
                            // System.out.println("chosen predicate is: " + chosen);
                            facts.add(chosen);
                        }
                    }
                }
        );
        return facts;
    }

    private static List<List<Predicate>> extract(Pair<Predicate, List<Predicate>> bodyPair, List<Predicate> heads){
        // each index corresponds to facts belonging to predicate at that index
        List<List<Predicate>> finalFacts = new ArrayList<>();
        for(Predicate head: heads){
            finalFacts.add( extractor(bodyPair, head) );
        }
        return finalFacts;
    }

    private static List<Predicate> extract(Pair<Predicate, List<Predicate>> bodyPair, Predicate head){
        return extractor(bodyPair, head);
    }


    /**
     * extracts facts that unify/bind with head atom
     */
    private static List<Predicate> extractor(Pair<Predicate, List<Predicate>> pair, Predicate head){

        int headLen = head.getArgLength();
        Set<Integer> constIdxs = new HashSet<>();

        for(int i=0; i < headLen; i++){
            if(head.getArg(i) instanceof Constant)
                constIdxs.add(i);
        }
        // unzip the pair
        Predicate pred = pair.getFirstVal();
        List<Predicate> facts = pair.getSecondVal();

        // list of indices corresponding to variables of head Predicate
        List<Integer> idxs = head.getArgs().stream().map(term -> {
            if(term instanceof Variable)
                return pred.getArgs().indexOf(term);
            else
                return -1;
        }).collect(Collectors.toList());

        List<Predicate> extractedFacts = new ArrayList<>();

        facts.stream().forEach(fact -> {

            List<Term> terms = new ArrayList<>();
            for(int i=0; i < headLen; i++){
                if(constIdxs.contains(i))
                    terms.add(new Constant(head.getArg(i)));
                else
                    terms.add(new Constant(fact.getArg(idxs.get(i))));
            }
            Predicate newFact = new Predicate(head.getName(), terms);
            extractedFacts.add(newFact);
        });
        return extractedFacts;
    }
}
