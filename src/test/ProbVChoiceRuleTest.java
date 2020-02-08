package test;

import main.*;

import java.util.*;
import java.util.stream.Collectors;


public class ProbVChoiceRuleTest {

    /**
     1 :: { visited(X,0): vertex(X) }. % choose initial node randomly
     0.8 :: { visited(Y,T2): edge(X,Y) }  |  0.2 :: { visited(X,T2) } :- visited(X,T), time(T2), #succ(T,T2).
     % @@  % randomly pick one of the adjacent vertices or wait
     1 :: { visited(Y,T3): vertex(Y) , X!=Y,} :-  visited(X,T),  visited(X,T2),  time(T2), time(T3),  #succ(T,T2), #succ(T2,T3).
     % jump to another node randomly after waiting too long
     */

    /**
     1 :: { visited(X,0): vertex(X)) }.
     0.8 :: { visited(Y, T2): edge(X,Y) } | 0.2 :: { visited(Z, T2): vertex(Z) } :- visited(X, T), time(T2), #succ(T, T2).

     */

    public static List<Predicate> timeFacts(int n){
        Predicate[] predArray = new Predicate[n];
        for(int i=0; i < n; i++){
            predArray[i] = Generator.newFact("time", Integer.toString(i));
        }
        return Arrays.asList(predArray);
    }


    public static void main(String[] args) {


        final int NUMBER_OF_VERTICES = 8;
        final int NUMBER_OF_TIME_STEPS = 1000;

        // input facts: facts
        Predicate[] _facts = { Generator.newFact("vertex", "1"),
                               Generator.newFact("vertex", "2"),
                               Generator.newFact("vertex", "3"),
                               Generator.newFact("vertex", "4"),
                               Generator.newFact("vertex", "5"),
                               Generator.newFact("vertex", "6"),
                               Generator.newFact("vertex", "7"),
                               Generator.newFact("vertex", "8"),
                               //Generator.newFact("vertex", "3"),

                               Generator.newFact("edge", "1", "2"),
                               Generator.newFact("edge", "2", "1"),
                               Generator.newFact("edge", "2", "4"),
                               Generator.newFact("edge", "4", "5"),
                               Generator.newFact("edge", "3", "5"),
                Generator.newFact("edge", "1", "3"),
                Generator.newFact("edge", "1", "6"),
                Generator.newFact("edge", "6", "7"),
                Generator.newFact("edge", "6", "8"),
                Generator.newFact("edge", "7", "8"),
                Generator.newFact("edge", "8", "7"),
                            };


        // Rule1
        Predicate vertex = Generator.newPredicate("vertex", "X");
        List<Predicate> body1 = new ArrayList<>();
        body1.add(vertex);

        Predicate head1 = Generator.newPredicate("visited", "X", "0");
        Choice rule1 = new Choice(head1, body1);

        //


        List<Choice> heads21 = new ArrayList<>();

        List<Predicate> body21 = new ArrayList<>();
        body21.add( Generator.newPredicate("edge", "X", "Y") );
        Choice choice = new Choice(Generator.newPredicate("visited", "Y", "T2"), body21);
        heads21.add(choice);

        List<Predicate> head21Body = new ArrayList<>();
        head21Body.add( Generator.newPredicate("vertex", "Z") );
        heads21.add(new Choice(Generator.newPredicate("visited", "Z", "T2"), head21Body));

        Predicate[] bodyRule2Arr = { Generator.newPredicate("visited", "X", "T"), Generator.newPredicate("time", "T2"),
                                  Generator.newPredicate("succ", "T", "T2")
                                };
        List<Predicate> bodyRule2 = Arrays.asList(bodyRule2Arr);

        List<Double> headProbs = new ArrayList<>();
        headProbs.add(0.8); headProbs.add(0.2);

        ProbVChoiceRule rule2 = new ProbVChoiceRule(heads21, headProbs, bodyRule2);

        // List<Predicate> rule2Result = Engine.probVChoiceRuleExec(rules2, Arrays.asList(_facts));
        // System.out.println(rule2Result);

        List<Predicate> facts = new ArrayList<>( Arrays.asList(_facts) );
        List<Predicate> timeFcts = timeFacts(NUMBER_OF_TIME_STEPS);


        facts.addAll( timeFcts );


        List<Choice> rules = new ArrayList<>();
        rules.add(rule1);

        List<Predicate> result =  Engine.ChoiceExec(rules, facts);

        //result.stream().forEach( predicate -> System.out.println( predicate ) );
        ///////////////////////////printPredicates( result );
        result =  Generator.normalize( result );
        // executing rule 2
        List<ProbVChoiceRule> rules2 = new ArrayList<>();
        rules2.add( rule2 );

        List<Predicate> result2 = Engine.probVChoiceRuleExec( rules2, result );
        ////////////////// printPredicates( result2 );

        // lets execute rule1 and rule2 combined with the function Engine.executeRules(), which is untested
        List<Rule> combinedRules = new ArrayList<>();
        combinedRules.add(rule1);
        combinedRules.add(rule2);

        Map<Integer, Long> vertexNumToCount = new HashMap<>();
        long totalNumberOfAtoms = 0;

        final int NUMBER_OF_ITERATIONS = 10;
        for(int k=0; k < NUMBER_OF_ITERATIONS; k++) {
            List<Predicate> combinedRulesExecResult = Engine.executeRules(combinedRules, facts);
            totalNumberOfAtoms += combinedRulesExecResult.size();
            /////////////////printPredicates(combinedRulesExecResult);
            for (int i = 1; i <= NUMBER_OF_VERTICES; i++) {
                final int j = i;
                long count = combinedRulesExecResult.stream().filter(predicate -> (predicate.getName().equals("visited") &&
                        predicate.getArg(0).getValue().equals(j))).count();

                if(vertexNumToCount.containsKey(i)){
                    long countI = vertexNumToCount.get(i);
                    countI += count;
                    vertexNumToCount.put(i, countI);
                }
                else{
                    vertexNumToCount.put(i, count);
                }
            }
        }
        for(int i=1; i <= NUMBER_OF_VERTICES; i++){
            Long count = vertexNumToCount.get(i);
            if(count != null){
                System.out.println( "vertex(" + i + ") : "  + (count + 0.0)/(NUMBER_OF_TIME_STEPS * NUMBER_OF_ITERATIONS));
            }
        }
    }

    public static void printPredicates( Iterable<Predicate> predicates ){
        System.out.println("############# printPredicatesBegin ##############3");
        predicates.forEach( predicate -> System.out.println(predicate) );
        System.out.println("############# printPredicatesEnd ##############3");
    }



}
