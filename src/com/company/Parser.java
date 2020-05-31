package com.company;
import java.util.List;

public class Parser
{
    private final List<Token> tokens; // список токенов
    private ListOfVariables lVariables;
    private int counter = 0; // счетчик
    private Token token = null;

    public Parser(List<Token> tokens)
    {
        this.tokens = tokens;
        lVariables = new ListOfVariables();
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
        variableValue();
        SEMICOLON();
    }

    private  void variableAssigment() throws LangParseException // присвоение
    {
        VAR();
        ASSIGN_OP();
        variableValue();
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
        ASSIGN_OP();
        value();
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
            }

        } catch (LangParseException e)
        {
            counter = newCounter; // в этом случае tokens.get(step) - VAR
            try
            {
                variableAssigment(); // пытаемся присвоить уже существующей переменной новое значение

                if (!lVariables.checkIfValueExist(new ListOfVariables.OneOfVariables(tokens.get(newCounter).getValue()))) // сли переменной нету в таблице переменных, тогда ошибка
                {
                    System.out.println("ERROR: VARIABLE doesn't EXISTS!");
                    throw new LangParseException("ERROR: VARIABLE doesn't EXISTS!");
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
                        print(); // проверка является ли tokens.get(step) - print
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
        value();
        if (tokens.get(counter - 1).getType().equals(LexemType.VAR)) // проверка существует ли уже переменная, которую мы хотим присвоить
        {
            if(!lVariables.checkIfValueExist(new ListOfVariables.OneOfVariables(tokens.get(counter - 1).getValue())))
            {
                System.out.println("ERROR: value doens't exist");
                throw new LangParseException("ERROR: value doens't exist");
            }
        }

        while (tokens.size() > counter)
        {
            if (tokens.get(counter).getType().equals(LexemType.SEMICOLON)) // пока не дойдем до ;, выполняем OP и затем digit, либо value
                break;

            OP();
            value();
            if (tokens.get(counter - 1).getType().equals(LexemType.VAR)) // проверка существует ли уже переменная, которую мы хотим присвоить
            {
                if(!lVariables.checkIfValueExist(new ListOfVariables.OneOfVariables(tokens.get(counter - 1).getValue())))
                {
                    System.out.println("ERROR: value doens't exist");
                    throw new LangParseException("ERROR: value doens't exist");
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
        matchToken(match(), LexemType.KEY_FOR);
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
}
