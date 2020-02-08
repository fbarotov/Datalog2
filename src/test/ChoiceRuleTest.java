package test;

import main.Engine;
import main.Predicate;
import main.term.Constant;
import main.term.Term;
import main.*;
import main.term.Variable;

import java.util.ArrayList;
import java.util.List;

public class ChoiceRuleTest {



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

        // time(1..n).
        final int N = 10;
        List<Predicate> timePreds = new ArrayList<>();

        for(int i=1; i < N; i++){
            List<Term> terms = new ArrayList<>();
            terms.add( new Constant(i));
            timePreds.add( new Predicate("time", terms) );
        }

        // generate edges
        /*
        List<Predicate> edges = new ArrayList<>();
        edges.add( newFact("edge", 1, 2) );
        edges.add( newFact("edge", 1, 3) );
        edges.add( newFact("edge", 2, 4) );
        edges.add( newFact("edge", 4, 3) );
        edges.add( newFact("edge", 1, 2) );
        edges.add( newFact("edge", 4, 5) );

        edges.add( newFact("edge", 2, 1) );
        edges.add( newFact("edge", 3, 1) );
        edges.add( newFact("edge", 4, 2) );
        edges.add( newFact("edge", 3, 4) );
        edges.add( newFact("edge", 2, 1) );
        edges.add( newFact("edge", 5, 4) );

        */

        List<Predicate> body = new ArrayList<>();
        body.add( newPredicate("time", "X") );
        body.add( newPredicate("succ", "X", "V") );
        Choice choice = new Choice(newPredicate("pair", "X", "V"), body);

        List<Choice> choiceList = new ArrayList<>();
        choiceList.add(choice);


        List<Predicate> execRes = Engine.ChoiceExec(choiceList, timePreds);

        System.out.println(execRes);
        // choose some random node at the beginning





    }










}
