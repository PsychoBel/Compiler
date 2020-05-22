package com.company;
import java.util.List;

public class Parser
{
    private final List<Token> tokens;
    private ListOfVariables lVariables;
    private int counter = 0;

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

    private  void variableAssigment() throws LangParseException
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

    private void cycle_for() throws LangParseException
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

    private  void value_expr () throws LangParseException
    {
        int step = counter; // сохраняем счетчик
        try {
            variableCreation(); // пытаемся создать переменную
            boolean create = lVariables.addVariable(new ListOfVariables.OneOfVariables(tokens.get(step).getValue(), tokens.get(step + 1).getValue()));
            if (!create)
            {
                System.out.println("VARIABLE ALREADY EXISTS!");
                throw new LangParseException("VARIABLE ALREADY EXISTS!");
            }
        } catch (LangParseException e)
        {
            counter = step;
            try
            {
                variableAssigment();

                boolean assign = lVariables.checkIfValueExist(new ListOfVariables.OneOfVariables(tokens.get(step).getValue()));

                if (!assign)
                {
                    System.out.println("VARIABLE doesn't EXISTS!");
                    throw new LangParseException("VARIABLE doesn't EXISTS!");
                }

            } catch (LangParseException e2)
            {
                counter = step;
                try
                {
                    condition_if();
                } catch (LangParseException e3) {
                    counter = step;
                    try
                    {
                        cycle_for();
                    } catch (LangParseException e4){
                        counter = step;
                        print();
                    }
                }
            }
        }
    }
    private Token match() throws LangParseException // отслежваем, чтобы мы не вышли за пределы массива
    {
        Token token = null;

        if (counter < tokens.size())
        {
            token = tokens.get(counter);
            counter++;
        } else throw new LangParseException("FATAL ERROR: Закончились токены проверки...");

        return token;
    }

    private void variableValue() throws LangParseException // реализуем присвоение
    {
        value();

        while (tokens.size() > counter)
        {
            if (tokens.get(counter).getType().equals(LexemType.SEMICOLON)) // пока не дойдем до ;, выполняем OP и затем digit, либо value
                break;

            OP();
            value();
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

    private void matchToken(Token token, LexemType type) throws LangParseException
    {
        if (!token.getType().equals(type))
        {
            throw new LangParseException("ERROR: " + token.getType()
                    + " expected but "
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
