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
    HashMap<String, LinkedList> listTable = new HashMap<String, LinkedList>();
    Map<String, Integer> points = new HashMap<String, Integer>(); // Map для хранения наших меток
    HashMap<String, HashSet> MapTable = new HashMap<String, HashSet>();

    int a, b, c;

    public StackMachine(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void setMarksPosiions(Map<String, Integer> marks) {
        this.points = marks;
    }

    public void setVarTable(HashMap<String, HashMap<String, String>> table) {
        this.tableForMachine = table;
    }

    public int run () {
       // System.out.println("\nStarting stack machine...");
        Token token;

      //  debugTable();
        // debugMark();

        while (counter < tokens.size()) {
            token = tokens.get(counter);

            if (token.getType() == LexemType.VAR)
            {
                buffer.push(token.getValue());
            } else if (token.getType() == LexemType.DIGIT)
            {
                buffer.push(token.getValue());
            } else if (token.getType() == LexemType.OP)
            {
                OPERATION(token.getValue());
            } else if (token.getType() == LexemType.ASSIGN_OP)
            {
                ASSIGN_OP();
            } else if (token.getType() == LexemType.COMPARISION_OP)
            {
                LOGIC_OPERATION(token.getValue());
            } else if (token.getValue() == "!F")
            {
                int pointValue = points.get(tokens.get(counter-1).getValue());
                boolean fl = buffer.pop().equals("true");
                counter = fl ? counter : pointValue - 1;
            } else if (token.getValue() == "!")
            {
                int pointValue = points.get(tokens.get(counter-1).getValue());
                counter = pointValue;
                counter--; //костыль (почему-то прыгает на один элемент вперед - выяснить!
            } else if (token.getType() == LexemType.KEY_PRINT)
            {
                System.out.println("PRINT -->  " + getVarFromTable(buffer.pop()));
            } else if (token.getType() == LexemType.KEY_LIST)
            {
                LinkedList list = new LinkedList();
                counter++;
                listTable.put(tokens.get(counter).getValue(), list);
            } else if (token.getType() == LexemType.KEY_LIST_ADD)
            {
                String variable = buffer.pop();             // название списка
                LinkedList list = listTable.get(variable);  // этот список из таблиц
                counter++;
                int value = getVarFromTable(tokens.get(counter).getValue());    // значение переменной для записи в список
                list.add(value);    // помещаю в список
                listTable.put(variable, list); //возвращаю список обратно в таблицу
            } else if (token.getType() == LexemType.KEY_LIST_GET)
            {
                counter++;
                int id = getVarFromTable(tokens.get(counter).getValue());

                String variable = buffer.pop();
                LinkedList list = listTable.get(variable);  // этот список из таблиц
                int value = list.getByIndex(id);
                buffer.push(String.valueOf(value));
            } else if (token.getType() == LexemType.KEY_HASHMAP) {
                HashSet set = new HashSet();
                counter++;
                MapTable.put(tokens.get(counter).getValue(), set);
            } else if (token.getType() == LexemType.KEY_HASH_ADD) {
                String variable = buffer.pop();             // название переменной
                counter++;
                String key = tokens.get(counter).getValue();
                counter++;
                int value = getVarFromTable(tokens.get(counter).getValue());    // значение переменной для записи в список

                System.out.println("1KEY   :-> " +  key);
                System.out.println("1name  :-> " + variable);
                System.out.println("1value :-> " + value);

                HashSet set = MapTable.get(variable);       // этот список из таблиц
                set.add(key, value);                   // помещаю в список
                MapTable.put(variable, set); //возвращаю список обратно в таблицу

            } else if (token.getType() == LexemType.KEY_HASH_GET) {
                counter++;
                String key = tokens.get(counter).getValue();
                String variable = buffer.pop();
                HashSet set = MapTable.get(variable);  // этот список из таблиц
                int value = set.getByKey(key);

                System.out.println("2KEY   :-> " + key);
                System.out.println("2name  :-> " + variable);
                System.out.println("2value :-> " + value);

                buffer.push(String.valueOf(value));
            }
            counter++;
        }

        debugHashTable();
        debugTable();
        return 0;
    }


    private void ASSIGN_OP(){
        a = getVarFromTable(buffer.pop());
        HashMap<String, String> innerMap = new HashMap<String, String>(); // внутренний hashmap, в котором будет храниться тип и значение переменной
        innerMap.put("value", Integer.toString(a));
        tableForMachine.put(buffer.pop(), innerMap);
    }

    private void OPERATION(String op)
    {
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

    private void debugHashTable() {
        System.out.println("");
        System.out.printf("%-15s%-10s%n", "переменная", "значение");
        for (Map.Entry entry : MapTable.entrySet()) {
            // Выводим имя поля
            System.out.printf("%-15s", entry.getKey());
            // Выводим значение поля
            System.out.printf("%5s%n", entry.getValue());
        }
        System.out.println();
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