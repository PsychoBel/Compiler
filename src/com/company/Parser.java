package com.company;
import java.util.*;


public class Parser
{
    private final List<Token> tokens; // список токенов
    private ListOfVariables lVariables;
    private int counter = 0; // счетчик
    private Token token = null;
    HashMap<String, HashMap<String, String>> tableForMachine = new HashMap<String, HashMap<String, String>>();
    List<String> list_table = new ArrayList<>();

    public Parser(List<Token> tokens)
    {
        this.tokens = tokens;
        lVariables = new ListOfVariables();

    }

    public HashMap<String, HashMap<String, String>> getVarTable ()
    {
        return tableForMachine;
    }

    public boolean lang() throws LangParseException
    {
        try
        {
            while (tokens.size() > counter)
            {
                expr();
            }
            System.out.println("###### КОД ВЕРЕН ######");
            return true;
        } catch (LangParseException e)
        {
            System.out.println("###### В КОДЕ ЕСТЬ ОШИБКА ######");
            System.out.println(e);
            System.out.println(tokens.size());
            System.out.println(counter);
            return false;
        }
    }

    private void expr() throws LangParseException
    {
        value_expr ();
    }

    private  void variableCreation() throws LangParseException
    {
        KEY_DATA_TYPE();
        VAR();
        ASSIGN_OP();

        int step = counter;
        try {
            variableValue();
            SEMICOLON();
        } catch (LangParseException e) {
            counter = step;
            try
            {
                listGet();
            } catch (LangParseException e1)
            {
                counter = step;
                MapGet();
            }
        }
    }

    private  void variableAssigment() throws LangParseException // присвоение
    {
        VAR();
        ASSIGN_OP();
        int step = counter;
        try {
            variableValue();
            SEMICOLON();
        } catch (LangParseException e) {
            counter = step;
            try
            {
                listGet();
            } catch (LangParseException e1)
            {
                counter = step;
                MapGet();
            }
        }
    }


    private void MapCreation () throws  LangParseException
    {
        KEY_HASHMAP();
        VAR();
        SEMICOLON();
    }

    private  void MapAdd () throws  LangParseException
    {
        VAR();
        KEY_HASH_ADD();
        VAR();
        value();
        SEMICOLON();
    }

    private  void MapGet () throws  LangParseException
    {
        VAR();
        KEY_HASH_GET();
        value();
        SEMICOLON();
    }


    private void listCreation () throws  LangParseException
    {
        KEY_LIST();
        int step = counter;
        VAR();
        SEMICOLON();
        list_table.add(tokens.get(step).getValue());
    }

    private  void listAdd () throws  LangParseException
    {
        VAR();
        KEY_LIST_ADD();
        value();
        SEMICOLON();
    }

    private  void listGet () throws  LangParseException
    {
        VAR();
        KEY_LIST_GET();
        value();
        SEMICOLON();
    }

    private void print() throws LangParseException
    {
        KEY_PRINT();
        ROUND_OPEN_BRACKET();
        value();
        ROUND_CLOSE_BRACKET();
        SEMICOLON();
    }


    private void loop_while () throws  LangParseException
    {
        KEY_WHILE();
        ROUND_OPEN_BRACKET();
        value();
        COMPARISION_OP();
        value();
        ROUND_CLOSE_BRACKET();
        FIGURE_OPEN_BRACKET();
        int step2 = counter;

        try {
            while (!(tokens.get(counter).getType().equals(LexemType.FIGURE_CLOSE_BRACKET)))
                value_expr();

            FIGURE_CLOSE_BRACKET();
        } catch (LangParseException e) {
            counter = step2;
            FIGURE_CLOSE_BRACKET();
        }
    }



    private void loop_for() throws LangParseException
    {
        KEY_FOR();
        ROUND_OPEN_BRACKET();

        variableCreation();

        VAR();
        COMPARISION_OP();
        value();
        SEMICOLON();
        // надо сделать проверку на +- 1
        VAR();
        OP();
        OP();
        ROUND_CLOSE_BRACKET();
        FIGURE_OPEN_BRACKET();
        int step2 = counter;
        try
        {
            while (!(tokens.get(counter).getType().equals(LexemType.FIGURE_CLOSE_BRACKET)))
                value_expr();

            FIGURE_CLOSE_BRACKET();
        } catch (LangParseException e)
        {
            counter = step2;
            FIGURE_CLOSE_BRACKET();
        }
    }

    private void condition_if() throws LangParseException {
        KEY_IF();
        ROUND_OPEN_BRACKET();
        value();
        int step1 = counter;
        try
        {
            COMPARISION_OP();
        } catch (LangParseException e)
        {
            counter = step1;
            ASSIGN_OP();
            ASSIGN_OP();
        }
        value();
        ROUND_CLOSE_BRACKET();
        FIGURE_OPEN_BRACKET();
        int step2 = counter;

        try {
            while (!(tokens.get(counter).getType().equals(LexemType.FIGURE_CLOSE_BRACKET)))
                value_expr();

            FIGURE_CLOSE_BRACKET();
        } catch (LangParseException e) {
            counter = step2;
            FIGURE_CLOSE_BRACKET();
        }
    }

    private  void value_expr () throws LangParseException // проверяем 1 элемент строки и от этого оттакливаемся, что проверять далее
    {
        int newCounter = counter; // сохранение состояния счетчика, чтобы проверять тот же эелемент (в первом случае token.get(step) - KEY_DATA_TYPE)
        try
        {
            variableCreation(); // пытаемся создать переменную
            if (lVariables.checkIfValueExist(new ListOfVariables.OneOfVariables(tokens.get(newCounter + 1).getValue()))) // проверка на то, существует ли уже наша переменная в таблице переменных
            {
                System.out.println("ERROR: VARIABLE ALREADY EXISTS!");
                throw new LangParseException("ERROR: VARIABLE ALREADY EXISTS!");
            }
            else
            {
                lVariables.addVariable(new ListOfVariables.OneOfVariables(tokens.get(newCounter).getValue(), tokens.get(newCounter + 1).getValue())); // если не сузествует, то добавляем в таблицу переменных, нашу переменную с ее типом
                HashMap<String, String> innerMap = new HashMap<String, String>(); // внутренний hashmap, в котором будет храниться тип и значение переменной
                innerMap.put("type", tokens.get(newCounter).getValue());
                innerMap.put("value", "0");
                tableForMachine.put(tokens.get(newCounter + 1).getValue(), innerMap);
            }

        } catch (LangParseException e)
        {
            counter = newCounter; // в этом случае tokens.get(step) - VAR
            try
            {
                variableAssigment(); // пытаемся присвоить уже существующей переменной новое значение

                if (!lVariables.checkIfValueExist(new ListOfVariables.OneOfVariables(tokens.get(newCounter).getValue()))) // если переменной нету в таблице переменных, тогда ошибка
                {
                    System.out.println("ERROR: THE VARIABLE DOESN'T EXIST!");
                    throw new LangParseException("ERROR: THE VARIABLE DOESN'T EXIST!");
                }

            } catch (LangParseException e2)
            {
                counter = newCounter;
                try
                {
                    condition_if(); // проверка является ли tokens.get(step) - If
                } catch (LangParseException e3)
                {
                    counter = newCounter;
                    try
                    {
                        loop_for(); // проверка является ли tokens.get(step) - for
                    } catch (LangParseException e4)
                    {
                        counter = newCounter;
                        try
                        {
                            print(); // проверка является ли tokens.get(step) - print
                        } catch (LangParseException e5)
                        {
                            counter = newCounter;
                            try
                            {
                                loop_while();
                            } catch (LangParseException e6)
                            {
                                counter = newCounter;
                                try
                                {
                                    listCreation();
                                } catch (LangParseException e7)
                                {
                                    counter = newCounter;
                                    try
                                    {
                                        listAdd();
                                    } catch (LangParseException e8)
                                    {
                                        counter = newCounter;
                                        try
                                        {
                                            MapAdd();
                                        } catch (LangParseException e9)
                                        {
                                            counter = newCounter;
                                            MapCreation();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private Token match() throws LangParseException // возвращает новый токен и инкриминтирует счетчик
    {

        if (counter < tokens.size())
        {
            token = tokens.get(counter);
            counter++;
        } else throw new LangParseException("ERROR: Закончились токены проверки...");

        return token;
    }

    private void variableValue() throws LangParseException // реализуем присвоение
    {
        skipBracket();
        value();
        if(list_table.contains(tokens.get(counter - 1).getValue()))
        {
            throw new LangParseException("ERROR");
        }
        if (tokens.get(counter - 1).getType().equals(LexemType.VAR)) // проверка существует ли уже переменная, которую мы хотим присвоить
        {
            if(!lVariables.checkIfValueExist(new ListOfVariables.OneOfVariables(tokens.get(counter - 1).getValue())))
            {
                System.out.println("ERROR: THE VARIABLE IS NOT INCILIZED!");
                throw new LangParseException("ERROR: THE VARIABLE IS NOT INCILIZED");
            }
        }

        while (tokens.size() > counter)
        {
            skipBracket();
            if (tokens.get(counter).getType().equals(LexemType.SEMICOLON)) // пока не дойдем до ;, выполняем OP и затем digit, либо value
                break;
            skipBracket();
            OP();
            skipBracket();
            value();
            if (tokens.get(counter - 1).getType().equals(LexemType.VAR)) // проверка существует ли уже переменная, которую мы хотим присвоить
            {
                if(!lVariables.checkIfValueExist(new ListOfVariables.OneOfVariables(tokens.get(counter - 1).getValue())))
                {
                    System.out.println("ERROR: THE VARIABLE IS NOT INCILIZED!");
                    throw new LangParseException("ERROR: THE VARIABLE IS NOT INCILIZED!");
                }
            }
        }
    }

    private void value() throws LangParseException // проверяем, чтобы присваивалось, либо Var, либо digit
    {
        int newCounter = counter; // сохранение состояния счетчика, чтобы проверять тот же эелемент

        try
        {
            VAR();
        } catch (LangParseException e)
        {
            counter = newCounter;
            DIGIT();
        }
    }


    private void matchToken(Token token, LexemType type) throws LangParseException // проверяет тип текущего токена с ожидаемым типом
    {
        if (!token.getType().equals(type))
        {
            throw new LangParseException("ERROR: " + type
                    + " expected, but came "
                    + token.getType().name() + ": '" + token.getValue()
                    + "' found");
        }
    }

    public boolean checkBrackets () // функция проверки правильности скобочной последовательности
    {
        Stack<Token> stack = new Stack<>();
        Token prevToken = tokens.get(0);
        for (Token token : tokens)
        {
            LexemType type = token.getType();

            if (type.equals(LexemType.ROUND_OPEN_BRACKET))
            {
                stack.push(token);
                if ((prevToken.getType().equals(LexemType.DIGIT)) || (prevToken.getType().equals(LexemType.VAR)))
                {
                    return false;
                }
            }
            if (type.equals(LexemType.ROUND_CLOSE_BRACKET))
            {
                if (stack.size() <= 0)
                    return false;
                if (stack.peek().getType().equals(LexemType.ROUND_OPEN_BRACKET))
                {
                    stack.pop();
                }
            }
            if (type.equals(LexemType.ROUND_CLOSE_BRACKET))
            {
                if (prevToken.getType().equals(LexemType.OP))
                {
                    return false;
                }
            }
            prevToken = token;
        }

        if (stack.size() > 0)
        {
            return false;
        } else {
            return true;
        }
        
    }

    private void skipBracket () // ф-ия для инцилизации переменной, пропуск скобок
    {
        while (tokens.get(counter).getType().equals(LexemType.ROUND_OPEN_BRACKET) || tokens.get(counter).getType().equals(LexemType.ROUND_CLOSE_BRACKET))
            counter++;
    }



    private void VAR() throws LangParseException
    {
        matchToken(match(), LexemType.VAR);
    }
    private void KEY_DATA_TYPE() throws LangParseException
    {
        matchToken(match(), LexemType.KEY_DATA_TYPE);
    }
    private void KEY_IF() throws LangParseException
    {
        matchToken(match(), LexemType.KEY_IF);
    }
    private void ROUND_OPEN_BRACKET() throws LangParseException
    {
        matchToken(match(), LexemType.ROUND_OPEN_BRACKET);
    }
    private void ROUND_CLOSE_BRACKET() throws LangParseException
    {
        matchToken(match(), LexemType.ROUND_CLOSE_BRACKET);
    }
    private void FIGURE_OPEN_BRACKET() throws LangParseException
    {
        matchToken(match(), LexemType.FIGURE_OPEN_BRACKET);
    }
    private void FIGURE_CLOSE_BRACKET() throws LangParseException
    {
        matchToken(match(), LexemType.FIGURE_CLOSE_BRACKET);
    }
    private void COMPARISION_OP() throws LangParseException
    {
        matchToken(match(), LexemType.COMPARISION_OP);
    }
    private void SEMICOLON() throws LangParseException
    {
        matchToken(match(), LexemType.SEMICOLON);
    }
    private void KEY_FOR() throws LangParseException
    {
        matchToken(match(), LexemType.KEY_FOR);
    }
    private void KEY_WHILE() throws LangParseException
    {
        matchToken(match(), LexemType.KEY_WHILE);
    }
    private void KEY_ELSE() throws LangParseException
    {
        matchToken(match(), LexemType.KEY_FOR);
    }
    private void DIGIT() throws LangParseException
    {
        matchToken(match(), LexemType.DIGIT);
    }
    private void OP() throws LangParseException
    {
        matchToken(match(), LexemType.OP);
    }
    private void ASSIGN_OP() throws LangParseException
    {
        matchToken(match(), LexemType.ASSIGN_OP);
    }
    private void KEY_PRINT() throws LangParseException
    {
        matchToken(match(), LexemType.KEY_PRINT);
    }
    private void DOUBLE_QUOTES() throws LangParseException
    {
        matchToken(match(), LexemType.DOUBLE_QUOTES);
    }
    private  void KEY_LIST () throws  LangParseException
    {
        matchToken(match(), LexemType.KEY_LIST);
    }
    private  void KEY_LIST_ADD () throws  LangParseException
    {
        matchToken(match(), LexemType.KEY_LIST_ADD);
    }

    private  void KEY_LIST_GET () throws  LangParseException
    {
        matchToken(match(), LexemType.KEY_LIST_GET);
    }
    private  void KEY_HASH_ADD () throws  LangParseException
    {
        matchToken(match(), LexemType.KEY_HASH_ADD);
    }

    private  void KEY_HASH_GET () throws  LangParseException
    {
        matchToken(match(), LexemType.KEY_HASH_GET);
    }

    private  void KEY_HASHMAP () throws  LangParseException
    {
        matchToken(match(), LexemType.KEY_HASHMAP);
    }


}
