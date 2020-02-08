package test;

import main.Choice;
import main.Engine;
import main.Predicate;
import main.ProbVChoiceRule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PrTest {


    public static List<Predicate> timeFacts(int num){
        List<Predicate> times = new ArrayList<>();
        for(int i=1; i <= num; i++){
            times.add( Generator.newFact("time", Integer.toString(i)) );
        }
        return times;
    }
    public static Predicate timeFact(int num){
        return Generator.newFact("time", Integer.toString(num));
    }
    public static Choice rule1(){
        // 1 :: { visited(X,0): vertex(X) }.
        return new Choice(Generator.newPredicate("visited","X", "0"),
                          Arrays.asList( Generator.newPredicate("vertex", "X") ));
    }
    public static ProbVChoiceRule rule2(){
        // 0.8 :: { visited(Y,T2): edge(X,Y) }  |  0.2 :: { visited(X,T2) } :- visited(X,T), time(T2), #succ(T,T2).
        List<Choice> heads = Arrays.asList(
                new Choice(Generator.newPredicate("visited", "Y", "T2"), Arrays.asList( Generator.newPredicate("edge", "X", "Y") )),
                new Choice(Generator.newPredicate("visited", "X", "T2"), new ArrayList<>())
        );
        List<Predicate> body = Arrays.asList(Generator.newPredicate("visited", "X", "T"), Generator.newPredicate("time", "T2"),
                                             Generator.newPredicate("succ", "T", "T2"));

        List<Double> headProbs = Arrays.asList(0.8, 0.2);
        return new ProbVChoiceRule(heads, headProbs, body);
    }

    public static ProbVChoiceRule rule3() {
        List<Predicate> body = Arrays.asList(
                Generator.newPredicate("visited", "X", "T"),
                Generator.newPredicate("visited", "X", "T2"),
                Generator.newPredicate("time", "T2"),
                Generator.newPredicate("time", "T3"),
                Generator.newPredicate("succ", "T", "T2"),
                Generator.newPredicate("succ", "T2", "T3")
        );
        // 1 :: { visited(Y,T3): vertex(Y) , X!=Y,} :-
        List<Choice> heads = Arrays.asList( new Choice(Generator.newPredicate("visited", "Y", "T3"),
                                                       Arrays.asList( Generator.newPredicate("vertex", "Y"),
                                                                      Generator.newPredicate("noteq", "X", "Y") )
                                                      )
        );
        return new ProbVChoiceRule(heads, Arrays.asList(1.0), body);
    }
    public static List<Predicate> getVertices(int num){
        List<Predicate> vertices = new ArrayList<>();
        for(int i=0; i < num; i++){
            vertices.add( Generator.newFact("vertex", Integer.toString(i)) );
        }
        return vertices;
    }
    public static List<Predicate> getEdges(int num){
        List<Predicate> edges = new ArrayList<>();
        for(int i=0; i < num; i++){
            edges.add( Generator.newFact("edge", Integer.toString(i), Integer.toString((i+1)%num)) );
            edges.add( Generator.newFact("edge", Integer.toString((i+1)%num), Integer.toString(i)) );
        }
        return edges;
    }

    /**
     1 :: { visited(X,0): vertex(X) }. % choose initial node randomly
     0.8 :: { visited(Y,T2): edge(X,Y) }  |  0.2 :: { visited(X,T2) } :- visited(X,T), time(T2), #succ(T,T2).
     % @@  % randomly pick one of the adjacent vertices or wait
     1 :: { visited(Y,T3): vertex(Y) , X!=Y,} :-  visited(X,T),  visited(X,T2),  time(T2), time(T3),  #succ(T,T2), #succ(T2,T3).
     % jump to another node randomly after waiting too long
     */
    public static void main(String[] args) {

        List<Predicate> vertices = getVertices(3);
        List<Predicate> edges = getEdges(3);
        // List<Predicate> rule1ExecRes = Engine.ChoiceExec(Arrays.asList( rule1() ), vertices);
        List<Predicate> rule1ExecRes = Arrays.asList( Generator.newFact("visited", "0", "0") );
        System.out.println(rule1ExecRes.stream().filter(p -> p.getName()=="visited").collect(Collectors.toList()).get(0).getArg(1).getValue().getClass());

        // System.out.println(rule1ExecRes);
        final int NUM_ITERATION = 10;
        for(int i=0; i < NUM_ITERATION; i++){

            List<Predicate> _fs = new ArrayList<>(rule1ExecRes);
            _fs.add( timeFact(i) );
            rule1ExecRes = Engine.probVChoiceRuleExec(Arrays.asList(rule2()), _fs ).stream().filter(p -> p.getName() != "time").collect(Collectors.toList());

            _fs = new ArrayList<>(rule1ExecRes);
            _fs.add(timeFact(i-1));
            _fs.add(timeFact(i));

            rule1ExecRes = Engine.probVChoiceRuleExec(Arrays.asList(rule3()), _fs).stream().filter(p -> p.getName() != "time").collect(Collectors.toList());
            System.out.println("End of one iteration: " + rule1ExecRes);
        }

        System.out.println(rule1ExecRes);
    }

}
