package main;

import main.term.Constant;
import main.term.Term;
import main.term.Variable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Predicate {
    
    String name;
    List<Term> args;

    // no default constructor
    private Predicate() {};

    public Predicate(String name, List<Term> args){
        this.name = name;
        this.args = args;
    }

    public Predicate(Predicate predicate){
        this.name = predicate.getName();
        this.args = predicate.getArgsCopy();
    }

    /**
     * @return new Predicate P such that P.name  = left.name + right.name and
     *         P.args = left.args + right.args;
    */
    public static Predicate joinedPred(Predicate left, Predicate right){
        List<Term> joinedTerms = left.getArgsCopy();
        joinedTerms.addAll(right.getArgsCopy());
        return new Predicate(left.getName() + right.getName(), joinedTerms);
    }

    /**
     * @return a list of predicates formed through cartesian product of the input
     *         predicates
    */
    public static List<Predicate> cartProduct(List<Predicate> left, List<Predicate> right){
        // list of preicates for cartesian product
        List<Predicate> cprod = new LinkedList<>();
        // join each left predicate with each right predicate
        for(Predicate lp: left){
            for(Predicate rp: right){
                cprod.add(Predicate.joinedPred(lp, rp));
            }
        }
        return cprod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Predicate predicate = (Predicate) o;
        return Objects.equals(name, predicate.name) &&
                Objects.equals(args, predicate.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, args);
    }

    public List<Term> getArgsCopy(){
        return this.getArgs().stream().map(term -> {
            if(term instanceof Constant)
                return new Constant<>(term);
            else
                return new Variable(term);
        }).collect(Collectors.toList());
    }


    public String getName() {
        return name;
    }

    public List<Term> getArgs() {
        return args;
    }

    public Term getArg(int i){
        return this.getArgs().get(i);
    }

    public Term getArgCopy(int i){
        Term term = this.getArg(i);
        if(term instanceof Variable)
            return new Variable(term);
        else
            return new Constant(term);
    }


    public boolean containsArg(Term term){
        return this.getArgs().contains(term);
    }


    public Integer getArgLength(){
        return this.args.size();
    }

    @Override
    public String toString(){
        String str = name + "(";
        for(int i=0; i < this.getArgs().size(); i++){
            if(i > 0)
                str = str + ", " + this.getArgs().get(i).toString();
            else
                str = str + this.getArgs().get(i).toString();
        }
        str = str + ")";
        return str;
    }
}
