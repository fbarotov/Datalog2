package test;

import main.Predicate;
import main.term.Constant;
import main.term.Term;
import main.term.Variable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Generator {



    private static boolean isNumeric(String strNum) {

        try{
            Integer.parseInt(strNum);
            return true;
        }catch (Exception e){
            return false;
        }


    }


    /**
     * @param name predicate name
     * @param args predicate arguments in string form
     * @return ground atom
     */
    public static Predicate newFact(String name, String ... args){
        List<Term> terms = new ArrayList<>();
        for(String arg: args){
            if(isNumeric(arg)){
                terms.add(new Constant<Integer>(Integer.parseInt(arg)));
            }
            else{
                terms.add(new Constant(arg));
            }
        }
        //System.out.println(terms);
        Predicate p =  new Predicate(name, terms);
        return p;
    }


    public static Predicate newPredicate(String name, String ...vars){
        List<Term> terms = new ArrayList<>();
        for(String var: vars){
            if(Character.isLowerCase(var.charAt(0)) || isNumeric(var)){ // if certain argument is not a variable
                if(isNumeric(var)){
                    terms.add(new Constant(Integer.parseInt(var)));
                }
                else {
                    terms.add(new Constant(var));
                }
            }
            else{
                terms.add( new Variable(var));
            }

        }
        // terms.stream().forEach(t -> System.out.println("!@#!@#!@#!@#!@: " + t));
        return new Predicate(name, terms);
    }

    // parses from string to normal predicate representation
    public static Predicate parseStrPredicate( String strPred){

        String[] entries = strPred.split( "[(), ]" );
        List<Term> terms = new ArrayList<>();
        for(int i=1; i < entries.length; i++){
            if( entries[i].isEmpty() ){
                continue;
            }
            else if( isNumeric(entries[i]) ){
                terms.add( new Constant( Integer.parseInt(entries[i]) ) );
            }
            else{
                terms.add( new Constant( entries[i] ) );
            }
        }
        return new Predicate( entries[0], terms );
    }

    public static List<Predicate> normalize(List<Predicate> predicateList){

        List<Predicate> predicates = new ArrayList<>(predicateList.size());
        for(Predicate predicate: predicateList){
            String strPredicate = predicate.toString();
            predicates.add( parseStrPredicate(strPredicate) );
        }
        return predicates;
    }




}
