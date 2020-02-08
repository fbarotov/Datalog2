package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomHelper {

    static Random rnd = new Random();

    public static int getRandom(int max){
        return rnd.nextInt(max);
    }
    /**
     * @INPUT probs probability for indices
     */
    public static int getRandom(List<Double> probs){

        Collections.sort(probs);
        double sum = probs.stream().reduce((k,v) -> k+v).get();
        int random = rnd.nextInt((int)(sum * 1000000));

        for(int i=0; i < probs.size(); i++){
            if(probs.get(i)*1000000 <= random)
                return i;
        }
        return probs.size()-1;
    }
    public static int chooseRandomIdx(List<Double> _probs){

        List<Double> probs = new ArrayList<>(_probs);
        List<Integer> idxs = new ArrayList<>();

        Collections.sort(probs);
        for(Double prob: probs){
            idxs.add(_probs.indexOf(prob));
        }
        Double chosen = rnd.nextDouble();
        for(int i=0; i < probs.size(); i++){
            if(chosen < probs.get(i)){
                return idxs.get(i);
            }
        }
        return idxs.get(probs.size()-1);
    }




}
