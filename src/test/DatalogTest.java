package test;


import main.Datalog;
import main.Pair;
import main.Predicate;
import main.term.Constant;
import main.term.Term;
import main.term.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DatalogTest {


    /**
     * for stream maps, unless you collect side effects are ignored, know that !
     */



    public static Predicate newFact(String name, int ... ints){
        List<Term> terms = new ArrayList<>();
        for(int num: ints){
            terms.add( new Constant(num) );
        }
        return new Predicate(name, terms);
    }
    public static Predicate newPredicate(String name, String ...vars){
        List<Term> terms = new ArrayList<>();
        for(String var: vars){
            if(Character.isLowerCase(var.charAt(0))){
                terms.add( new Constant(var) );
            }
            else{
                terms.add( new Variable(var));
            }
        }
        return new Predicate(name, terms);
    }

    public static void main(String[] args) {

        Predicate edge1 = newFact("edge", 1, 2),
                  edge2 = newFact("walk", 2, 3),
                  edge3 = newFact("walk", 3, 5),
                  edge4 = newFact("edge", 0, 3);
        ArrayList<Predicate> facts = new ArrayList<>();

        facts.add(edge1);
        facts.add(edge2);
        facts.add(edge3);
        facts.add(edge4);

        List<Predicate> body = new ArrayList<>();
        body.add( newPredicate("edge", "X", "Y") );
        body.add( newPredicate("walk", "Y", "Z") );
        body.add( newPredicate("succ", "X", "T") );

        System.out.println(body);
        System.out.println(facts);

        Pair<Predicate, List<Predicate>> result = Datalog.join(body, facts);
        Predicate returnedPredicate = result.getFirstVal();
        List<Predicate> returnedFacts = result.getSecondVal();
        if(result == null)
            System.out.println("null returned from the join");
        else {
            System.out.println("returned predicate is: " + returnedPredicate);
            System.out.println("and returned facts are: ");
            returnedFacts.stream().forEach(predicate -> {
                System.out.println("\t\t\t\t\t\t" + predicate);
            });
            System.out.println("initial facts seem to be"+ facts);
        }
    }

}
