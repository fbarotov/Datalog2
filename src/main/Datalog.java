package main;


import main.term.Constant;
import main.term.Term;
import main.term.Variable;

import java.util.*;
import java.util.stream.Collectors;

public class Datalog {


    public static Pair<Predicate, List<Predicate>> join(List<Predicate> _preds, List<Predicate> _facts){

        // List<Predicate> preds = new ArrayList<>(_preds);
        // List<List<Predicate>> facts = unify(preds, _facts);

        // check for special predicates
        // imagine there will be only one succ(X,Y) at max.
        List<Predicate> succs = new ArrayList<>();
        List<Integer> succIdxs = new ArrayList<>();
        for(int i=0; i < _preds.size(); i++){
            if( _preds.get(i).getName() == "succ" ){
                succIdxs.add(i);
                succs.add( _preds.get(i) );
            }
        }
        // nobarobar predicata hal kun
        Predicate noteq = null;
        int noteqIdx = -1;
        for(int i=0; i < _preds.size(); i++){
            if(_preds.get(i).getName() == "noteq"){
                noteq = _preds.get(i);
                noteqIdx = i;
            }
        }
        //
        List<Predicate> preds = new ArrayList<>();
        for(int i=0; i < _preds.size(); i++){
            if(!succIdxs.contains(i) && i!= noteqIdx){
                preds.add(_preds.get(i));
            }
        }
        List<List<Predicate>> facts = unify(preds, _facts);

        // noteq-da tughri namegiriftagi factora khorij k
        if(noteq != null){
            // intra_predicate
            for(int i=0; i < preds.size(); i++){
                if(preds.get(i).getArgs().contains(noteq.getArg(0)) &&
                   preds.get(i).getArgs().contains(noteq.getArg(1))){
                    int firstIdx = preds.get(i).getArgs().indexOf(noteq.getArg(0));
                    int secondIdx = preds.get(i).getArgs().indexOf(noteq.getArg(1));
                    List<Predicate> fs = facts.get(i).stream().filter(p -> p.getArg(firstIdx) != p.getArg(secondIdx)).collect(Collectors.toList());
                    facts.remove(i);
                    facts.add(i, fs);
                }
            }
        }
        if(succIdxs.size() != 0){
            for(int j=0; j < succs.size(); j++){
                Predicate succ = succs.get(j);
                Variable x = (Variable) succ.getArg(0); // assuming there wont be any case such as Succ(1, Y)
                Set<Integer> xSet = new HashSet<>();
                for(int i=0; i < preds.size(); i++){ // scan the predicates to get possible values for x
                    Predicate predicate = preds.get(i);
                    if(predicate.getArgs().contains(x)){
                        int index = predicate.getArgs().indexOf(x);

                        facts.get(i).stream().map(p -> xSet.add( (int)( p.getArg(index).getValue() ))).collect(Collectors.toList());

                    }
                }
                List<Predicate> succFacts = new ArrayList<>();
                Arrays.stream(xSet.toArray()).map(val -> {
                    List<Term> terms = new ArrayList<>();
                    terms.add(new Constant( val ));
                    terms.add(new Constant( (int)val + 1 ));
                    succFacts.add( new Predicate("succ", terms));
                    return null;
                }).collect(Collectors.toList());
                preds.add(succ);
                facts.add(succFacts);
            }
        }
        if(preds.size() == 1)
            return new Pair<>(preds.get(0), facts.get(0));
/////////////////////////////////////////////////////////////////////////////////////////////////
        // check that all predicates should contain some facts matching
        if(facts.size() < preds.size())
            return null;
        for(int i =0 ; i < preds.size(); i++){
            if(facts.get(i).isEmpty())
                return null;
        }

        while (preds.size() >= 2){ // goes to infinity loop

            // System.out.println(facts.subList(0, 2))
            Map<Predicate, List<Predicate>> joinResult = joinTwoPreds(preds.subList(0,2), facts.subList(0,2));

            if(joinResult == null)
                System.out.println("There is something bad happening");
            Predicate newPred = joinResult.keySet().iterator().next();
            List<Predicate> newFacts = joinResult.get(newPred);

            // remove first two elements
            preds = preds.subList(2, preds.size());
            facts = facts.subList(2, facts.size());

            preds.add(0, newPred);
            facts.add(0, newFacts);
        }

        return new Pair<>(preds.get(0), facts.get(0));
    }

    // here we do not allow same variable to occur twice
    // for example wassup(red, X,Y,X) is not allowed since X is occurring twice
    private static List<List<Predicate>> unify(List<Predicate> preds, List<Predicate> facts){


        List<List<Predicate>> predicateListList = new ArrayList<>();
        for(Predicate predicate: preds){
            List<Predicate> predicateList = facts.stream().filter(fact -> {
                if(!fact.getName().equals(predicate.getName()) || fact.getArgLength() != predicate.getArgLength())
                    return false;
                for(int i=0; i < predicate.getArgLength(); i++){
                    if(predicate.getArg(i) instanceof Constant && !predicate.getArg(i).equals(fact.getArg(i)) ){
                        return false;
                    }
                }
                return true;
            }).collect(Collectors.toList());

            predicateListList.add(predicateList);
        }
        return predicateListList;
    }

    private static Map<Predicate, List<Predicate>> joinTwoPreds(List<Predicate> preds, List<List<Predicate>> facts){

        Predicate left = preds.get(0),
                  right = preds.get(1);
        List<Predicate> leftFacts = facts.get(0),
                        rightFacts = facts.get(1);

        // indices for terms to keep from predicates
        List<Integer> leftIdx = new ArrayList<>(),
                      rightIdx = new ArrayList<>();



        // terms for the new predicate
        List<Term> terms = new ArrayList<>();

        for(int i=0; i < left.getArgLength(); i++){
            Term term = left.getArg(i);
            if(term instanceof Variable && !terms.contains(term)){
                terms.add(new Variable(term));
                leftIdx.add(i);
            }
        }
        for(int i=0; i < right.getArgLength(); i++){
            Term term = right.getArg(i);
            if(term instanceof Variable && !terms.contains(term)){
                terms.add(term);
                rightIdx.add(i);
            }
        }
        Predicate joinPred = new Predicate(left.getName() + right.getName(), terms);
        List<List<Integer>> idxs = new ArrayList<>();
        idxs.add(leftIdx);
        idxs.add(rightIdx);

        if(Global.isDebugMode) {
            System.out.println("Indices to keep, leftIdx and then rightIdx: ");
            System.out.println(leftIdx);
            System.out.println(rightIdx);
        }
        List<Predicate> joined = joinTwoPredsHelper(preds, facts, idxs);
        if(Global.isDebugMode) {
            System.out.println("result of joinTwoPredsHelper: ");
            System.out.println(joined);
        }
        Map<Predicate, List<Predicate>> map = new HashMap<>();
        map.put(joinPred, joined);
        return map;


    }
    private static List<Predicate> joinTwoPredsHelper(List<Predicate> preds, List<List<Predicate>> facts, List<List<Integer>> idxsToKeep){
        List<Predicate> leftFacts = facts.get(0),
                        rightFacts = facts.get(1);
        Predicate left = preds.get(0),
                  right = preds.get(1);

        // indices to keep while joining
        List<Integer> leftIdxCommon = new ArrayList<>(),
                      rightIdxCommon = new ArrayList<>();

        // terms to keep track of
        // for example, pred(X,X,Y), we do not want for X to be added twice: NOTE THAT WE DO NOT ALLOW THIS !!!
       // List<Term> leftTermsCommon = new ArrayList<>(),
        //           rightTermsCommon = new ArrayList<>();

        for(int i=0; i < left.getArgLength(); i++){
            Term term = left.getArg(i);
            if(term instanceof Variable && right.getArgs().contains(term)){
                // leftTerms.add(term);
                leftIdxCommon.add(i);
                rightIdxCommon.add(right.getArgs().indexOf(term));
            }
        }

        // create hash-map on the right predicate facts
        Map<String, List<Predicate>> map = new HashMap<>();

        if(Global.isDebugMode) {
            System.out.println("rightIdxCommon and then leftIdxCommon");
            System.out.println(rightIdxCommon);
            System.out.println(leftIdxCommon);
        }
        // CREATE A HASH TABLE ON THE RIGHT PREDICATE GROUND ATOMS
        for(Predicate fact: rightFacts){
            List<Term> ts = rightIdxCommon.stream().map(i -> fact.getArg(i)).collect(Collectors.toList());
            String key = "";
            for(Term term: ts) { key = key + term.toString(); }

            if(map.containsKey(key)){
                List<Predicate> list = map.get(key);
                list.add(fact);
                map.put(key, list);
            }
            else{
                List<Predicate> list = new ArrayList<>();
                list.add(fact);
                map.put(key, list);
            }
        }
        if(Global.isDebugMode) {
            System.out.println("map after creating the hashmap");
            System.out.println(map);
        }
        List<Integer> leftIdxsToKeep = idxsToKeep.get(0),
                      rightIdxsToKeep = idxsToKeep.get(1);

        List<Predicate> joined = new ArrayList<>();
        for(Predicate fact: leftFacts){
            List<Term> ts = leftIdxCommon.stream().map(i -> fact.getArg(i)).collect(Collectors.toList());

            String key = "";
            for(Term term: ts) { key = key + term.toString(); }

            if(Global.isDebugMode) {
                System.out.println("key of the left");
                System.out.println(key);
                System.out.println(map);
            }
            if(map.containsKey(key)){
                if(Global.isDebugMode) {
                    System.out.println("it contains");
                }
                List<Predicate> rightFactsToJoin = map.get(key);
                List<Term> leftTermsToJoin = leftIdxsToKeep.stream().map(i -> fact.getArgCopy(i)).collect(Collectors.toList());
                for(Predicate rightFact: rightFactsToJoin){
                    List<Term> rightTermsToJoin = rightIdxsToKeep.stream().map(i -> rightFact.getArg(i)).collect(Collectors.toList()),
                               joinedTerms = new ArrayList<>(leftTermsToJoin);
                    if(Global.isDebugMode) {
                        System.out.println("lerftTermsToJoin");
                        System.out.println(leftTermsToJoin);
                        System.out.println("rightTermsToJoin");
                        System.out.println(rightTermsToJoin);
                    }
                    joinedTerms.addAll(rightTermsToJoin);
                    joined.add(new Predicate(left.getName() + right.getName(), joinedTerms));
                }
            }
        }
        return joined;
    }

    /*
        returns true if there is a variable that both input predicates contain
        otherwise returns false;
    */
    private static boolean isAnyCommonVars(Predicate left, Predicate right){
        for(Term leftArg: left.getArgs()) {
            if( leftArg instanceof Variable && right.containsArg(leftArg) )
                return true;
        }
        return false;
    }

    /*
        return true if the given input predicate contains variable
    */
    private static boolean isAnyVars(Predicate predicate){
        for( Term term: predicate.getArgs() ){
            if(term instanceof Variable)
                return true;
        }
        return false;
    }

    private static boolean isAnyVars(Predicate left, Predicate right){
        return isAnyVars(left) || isAnyVars(right);
    }

}
