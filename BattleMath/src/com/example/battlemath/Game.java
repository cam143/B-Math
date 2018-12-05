package com.example.battlemath;

import static java.lang.Double.NaN;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/** GENETRATES GAME QUESTION, EQUATION, ANSWER . . LINK @StartGame.java **/
public class Game {

    private final String operator[] = {"+" ,"-" , "*" , "/"};
    private final Random generator;
    private final String opr;
    
    private int num1;
    private int num2;
    
    public Game(){
        generator = new Random();
        opr = operator[randomNumber(0, 4)];
        num1 = randomNumber(-100, 100);
        num2 = randomNumber(-100, 100);
        
        while((num1 == 1 || num2 == 1 ||num1 == 0 ||num2 == 0) && (opr.equals("/")||opr.equals("*"))){
        	num1 = randomNumber(-100, 100);
        	num2 = randomNumber(-100, 100);
        }
    }
    
    /**** Get Operator ****/
    protected final String Get_Operator(){
        return opr;
    }
    
    /**** Get Expression ****/
    protected final String Get_Expression(){
    	return expression(num1, num2, opr) + " = " + answer(num1, num2, opr);
    }
    
    /**** Generate Expression ****/
    private final String expression(int num1 , int num2 , String operand){
        switch(operand){
            case "+": return num1 + "  ___  " + num2;
            case "-": return num1 + "  ___  " + num2;
            case "*": return num1 + "  ___  " + num2;
            case "/": return num1 + "  ___  " + num2;
        }
        return null;
    }
    
    /**** Generate Answer ****/
    private final double answer(double num1 , double num2 , String operand){
        double out = NaN;
        switch(operand){
            case "+": out = num1 + num2; break;
            case "-": out = num1 - num2; break;
            case "*": out = num1 * num2; break;
            case "/": out = num1 / num2; break;
        }
        BigDecimal bd = new BigDecimal(out).setScale(2, RoundingMode.HALF_EVEN);
        return bd.doubleValue();
    }
    
    /**** Generate Random Number ****/
    private final int randomNumber(int min, int max) {
        return (int) (min + (generator.nextDouble() * (max - min)));
    }
}