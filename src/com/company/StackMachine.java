package com.company;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class StackMachine {
    private final List<Token> tokens;
    private Stack<String> buffer = new Stack<>();
    private int counter = 0;

    HashMap<String, HashMap<String, String>> tableForMachine = new HashMap<String, HashMap<String, String>>();
    Map<String, Integer> points = new HashMap<String, Integer>(); // Map для хранения наших меток

    int a, b, c;

    public StackMachine(List<Token> tokens) {
        this.tokens = tokens;
    }


    public void setVarTable(HashMap<String, HashMap<String, String>> table) {
        this.tableForMachine = table;
    }

    public int run () {
        System.out.println("\nStarting stack machine...");
        Token token;

        debugTable();
        debugMark();

        while (counter < tokens.size()) {
            token = tokens.get(counter);

            if (token.getType() == LexemType.VAR) {
                buffer.push(token.getValue());
            } else if (token.getType() == LexemType.DIGIT) {
                buffer.push(token.getValue());
            } else if (token.getType() == LexemType.OP) {
                OPERATION(token.getValue());
            } else if (token.getType() == LexemType.ASSIGN_OP) {
                ASSIGN_OP();
            } else if (token.getType() == LexemType.COMPARISION_OP) {
                LOGIC_OPERATION(token.getValue());
            } else if (token.getValue() == "!F") {
                int pointValue = points.get(tokens.get(counter-1).getValue());
                boolean fl = buffer.pop().equals("true");
                counter = fl ? counter : pointValue - 1;
            } else if (token.getValue() == "!") {
                int pointValue = points.get(tokens.get(counter-1).getValue());
                counter = pointValue;
                counter--; //костыль (почему-то прыгает на один элемент вперед - выяснить!
            } else if (token.getType() == LexemType.KEY_PRINT) {
                System.out.println("F++ >  " + getVarFromTable(buffer.pop()));
            }
            counter++;
        }

        debugTable();
        return 0;
    }


    private void ASSIGN_OP(){
        a = getVarFromTable(buffer.pop());
        HashMap<String, String> innerMap = new HashMap<String, String>(); // внутренний hashmap, в котором будет храниться тип и значение переменной
        innerMap.put("value", Integer.toString(a));
        tableForMachine.put(buffer.pop(), innerMap);
    }

    private void OPERATION(String op) {
        b = getVarFromTable(buffer.pop());
        a = getVarFromTable(buffer.pop());

        switch (op) {
            case "+":
                c = a + b;
                break;
            case "-":
                c = a - b;
                break;
            case "/":
                c = a / b;
                break;
            case "*":
                c = a * b;
                break;
        }
//        System.out.println("OPERATION: " + String.valueOf(c));
        buffer.push(String.valueOf(c));
    }



    private void LOGIC_OPERATION(String op) {
        boolean flag = false;
        b = getVarFromTable(buffer.pop());
        a = getVarFromTable(buffer.pop());

        switch (op) {
            case "<":
                flag = a < b;
                break;
            case ">":
                flag = a > b;
                break;
            case "==":
                flag = a == b;
                break;
            case "!=":
                flag = a != b;
                break;
            case "<=":
                flag = a <= b;
                break;
            case ">=":
                flag = a >= b;
                break;
        }

//        System.out.println("LOGIC: " + flag);
        buffer.push(String.valueOf(flag));
    }

    public void setMarksPosiions(Map<String, Integer> marks) {
        this.points = marks;
    }

    private int getVarFromTable(String value) // возвращает число или значение переменной
    {
        if (isDigit(value)) {
            return Integer.valueOf(value);
        } else {
            return Integer.parseInt(tableForMachine.get(value).get("value"));
        }
    }


    private static boolean isDigit(String s) throws NumberFormatException {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;

        }
    }

    private void debugTable() {
        System.out.println("");
        System.out.printf("%-15s%-10s%n", "переменная", "значение");
        for (Map.Entry entry : tableForMachine.entrySet()) {
            // Выводим имя поля
            System.out.printf("%-15s", entry.getKey());
            // Выводим значение поля
            System.out.printf("%5s%n", entry.getValue());
        }
        System.out.println();
    }
    private void debugMark() {
        System.out.println("");
        System.out.printf("%-10s%-10s%n", "метка", "значение");
        for (Map.Entry entry : points.entrySet()) {
            // Выводим имя поля
            System.out.printf("%-7s", entry.getKey());
            // Выводим значение поля
            System.out.printf("%5s%n", entry.getValue());
        }
        System.out.println();
    }
}