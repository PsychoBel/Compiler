package com.company;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.HashMap;
import java.util.Map;

public class ReversePolisNotation
{

    private final List<Token> tokens; // список токенов
    Token IfFlag; //
    public List<Integer> number_of_variable;
    int isWhileCounter = 0;
    int isIfCounter = 0;
    private Stack<Token>  ifWhileStack = new Stack<>(); // стек для меток в while
    Map<String, Integer> points = new HashMap<String, Integer>(); // Map для хранения наших меток
    Stack<Token> stack = new Stack<>(); // стек для операндов
    public List<Token> result = new ArrayList<>(); // список в инфиксном варианте
    boolean flag_IF = false; // флаг, когда встречаем IF
    boolean flag_WHILE = false; // аг когда встречаем WHILE
    int point_counter = 1; // счетчик меток 
    int point_position_counter = 0; // счетчик расположения меток
    public List<LexemType> operation = new ArrayList<>();
    boolean printFlag = false;

    public ReversePolisNotation (List<Token> tokens)
    {
        this.tokens = tokens;
        operation.add(LexemType.OP);
        operation.add(LexemType.ROUND_CLOSE_BRACKET);
        operation.add(LexemType.ROUND_OPEN_BRACKET);
        operation.add(LexemType.COMPARISION_OP);
        operation.add(LexemType.ASSIGN_OP);
    }


    public Map<String, Integer> getPoints ()
    {
        return points;
    }

    public List<Token> getTokens()  // getter
    {
        return tokens;
    }

    public int priority (Token token)
    {
        switch (token.getValue())
        {
            case "*":
            case "/":
                return 4;

            case ">":
            case "<":
                return 3;

            case "+":
            case "-":
                return 2;

            case "=":
                return 0;

            default:
                return 1;
        }
    }

    public void make_polis ()
    {
        for (Token token : tokens)
        {
            if (token.getType().equals(LexemType.KEY_DATA_TYPE))
                continue;

            if (token.getType().equals(LexemType.KEY_PRINT))
            {
                printFlag = true;
                continue;
            }

            if (token.getType().equals(LexemType.SEMICOLON))
            {
                while (stack.size() > 0)
                    addToken(stack.pop());

                if (printFlag)
                {
                    addToken(new Token(LexemType.KEY_PRINT, "print"));
                    printFlag = false;
                }


                continue;
            }

            if (token.getType().equals(LexemType.VAR))
            {
                addToken(token);
                continue;
            }

            if (token.getType().equals(LexemType.KEY_IF) || token.getType().equals(LexemType.KEY_WHILE))
            {
                Token temporary;

                if (token.getType().equals(LexemType.KEY_WHILE))
                {
                    temporary = new Token(LexemType.KEY_WHILE, "P" + point_counter);
                    isWhileCounter++;
                }

                else
                {
                    temporary = new Token(LexemType.KEY_IF, "P" + point_counter);
                    isIfCounter++;
                }
                //?
                points.put("P" + point_counter, point_position_counter);
                point_counter++;
                IfFlag = temporary;
                ifWhileStack.push(temporary);

                continue;
            }

            if (token.getType().equals(LexemType.FIGURE_OPEN_BRACKET))
            {

            if ((isWhileCounter > 0) || (isIfCounter > 0)) {
                addToken(IfFlag);

                Token m2 = new Token("!F");
                addToken(m2);
            }

            continue;
        }

            if (token.getType().equals(LexemType.FIGURE_CLOSE_BRACKET)) {
                if (isWhileCounter > 0) {
                    if (ifWhileStack.peek().getType().equals(LexemType.KEY_WHILE)) {
                        Token m1 = new Token(LexemType.KEY_WHILE, "P" + point_counter);
                        Token m2 = new Token(LexemType.KEY_WHILE,"!");

                        while (stack.size() > 0)
                            addToken(stack.pop());

                        addToken(m1);
                        addToken(m2);

                        points.put("P" + point_counter, points.get(ifWhileStack.peek().getValue()));
                        points.put(ifWhileStack.peek().getValue(), point_position_counter);


                        point_counter++;
                        isWhileCounter--;
                    }
                }

                if (isIfCounter > 0) {
                    if (ifWhileStack.peek().getType().equals(LexemType.KEY_IF)) {
                        while (stack.size() > 0)
                            addToken(stack.pop());
                        points.put(ifWhileStack.peek().getValue(), point_position_counter);


                        point_counter++;
                        isIfCounter--;
                    }
                }

                ifWhileStack.pop();
                continue;
            }
            if (operation.contains(token.getType())) // если токен равен операции или скобкам, то идет работа со стеком
            {
                if ((stack.size() > 0) && (token.getType() != LexemType.ROUND_OPEN_BRACKET))
                {
                    if (token.getType() == LexemType.ROUND_CLOSE_BRACKET)
                    {
                       // System.out.println();
                        Token last_element = stack.pop();
                        while (last_element.getType() != LexemType.ROUND_OPEN_BRACKET)
                        {
                            addToken(last_element);
                            last_element = stack.pop();
                        }
                    }
                    else
                    {
                        if (priority(token) > priority(stack.peek()))
                        {
                            stack.add(token);
                        }
                        else
                        {
                            while (stack.size() > 0 && priority(token) <= priority(stack.peek()))
                            {
                                addToken(stack.pop());
                            }
                            stack.add(token);
                        }
                    }
                }
                else
                {
                    stack.add(token);
                }
            }
            else
            {
                addToken(token);
            }

        }
        if (stack.size() > 0)
            for (Token token : stack)
                addToken(token);
    }


    public void getResult ()
    {
        System.out.print("YOUR POLIS: ");
        for (Token token : result)
        {
            System.out.print(token.getValue() + " ");
        }
    }

    public void addToken (Token token)
    {
        result.add(token);
        point_position_counter++;

    }
}

