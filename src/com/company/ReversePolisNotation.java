package com.company;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ReversePolisNotation
{
    private final List<Token> tokens; // список токенов
    public List<Integer> number_of_variable;
    Stack<Token> stack = new Stack<>(); // стек для операндов
    public List<Token> result = new ArrayList<>(); // список в инфиксном варианте
    boolean flag_IF = false; // флаг, когда встречаем IF
    boolean flag_WHILE = false; // аг когда встречаем WHILE
    int point_counter = 1; // счетчик меток
    public List<LexemType> operation = new ArrayList<>();

    public ReversePolisNotation (List<Token> tokens) // конструктор
    {
        this.tokens = tokens;
        // this.number_of_variable = number_of_variable;
        operation.add(LexemType.OP);
        operation.add(LexemType.ROUND_CLOSE_BRACKET);
        operation.add(LexemType.ROUND_OPEN_BRACKET);
        operation.add(LexemType.COMPARISION_OP);
        operation.add(LexemType.ASSIGN_OP);
    }


    public List<Token> getTokens()  // getter
    {
        return tokens;
    }

    public int priority (Token token) // расставляем приоритет операциям
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

            if (token.getType().equals(LexemType.SEMICOLON))
            {
                while (stack.size() > 0)
                    addToken(stack.pop());
                continue;
            }

            if (token.getType().equals(LexemType.VAR)) // если операнд, то сразу записываем в итог
            {
                addToken(token);
                continue;
            }

            if (token.getType().equals(LexemType.KEY_IF) || token.getType().equals(LexemType.KEY_WHILE)) // если встречаем IF или WHILE, то срабатывает флаг
            {

                if (token.getType().equals(LexemType.KEY_WHILE))
                    flag_WHILE = true;

                if (token.getType().equals(LexemType.KEY_IF))
                    flag_IF = true;

                continue;
            }

            if (token.getType().equals(LexemType.FIGURE_OPEN_BRACKET)) // если был IF или WHILE и начинается их тело, то ставаим метку
            {

                if (flag_IF || flag_WHILE)
                {
                    Token p1 = new Token("P" + point_counter);
                    addToken(p1);
                    Token m2 = new Token("!F");
                    addToken(m2);
                    point_counter++;
                }

                continue;
            }
            if (token.getType().equals(LexemType.FIGURE_CLOSE_BRACKET)) // если закрывающаяся фигурная скобка, то добавляем все из стека, ставим метку (если WHILE), инкриминтируем счетчик
            {
                if (flag_IF)
                {
                    while (stack.size() > 0)
                        addToken(stack.pop());
                    // обязательно идет после addtoken (счетчик сдвигатся)
                    //marksPosiions.put("P" + (markCunter - 1), markPositioinCounter);

                    point_counter++; // сдвиг счетчика меток
                    flag_IF = false;
                }
                if (flag_WHILE)
                {

                    while (stack.size() > 0)
                        addToken(stack.pop());

                    addToken(new Token("P" + point_counter));
                    addToken(new Token("!"));

                    // обязательно идет после addtoken (счетчик сдвигатся)
                    //marksPosiions.put("P" + markCunter, marksPosiions.get("P" + (markCunter-1)));
                    //marksPosiions.put("P" + (markCunter - 1), markPositioinCounter);

                    point_counter++; // сдвиг счетчика меток
                    flag_WHILE = false;
                }
                continue;
            }
            if (operation.contains(token.getType())) // если токен равен операции или скобкам, то идет работа со стеком
            {
                if ((stack.size() > 0) && (token.getType() != LexemType.ROUND_OPEN_BRACKET))
                // если скобка закрывающася,
                // то добавляем элементы стека в result, до тех пор полка не встретим открывающиеся скобки
                {
                    if (token.getType() == LexemType.ROUND_CLOSE_BRACKET)
                    {
                        System.out.println();
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
                        // если приоритет нашего токена выше, чем приоритет последнего элемента стека, то кладем его в стек,
                        // если нет, то вынимаем элементы из стека, до тех пор пока, элемент в стеке не будет меньше нашего токена
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
    }



    public void getResult () // ф-ия печати result
    {
        System.out.print("YOUR POLIS: ");
        for (Token token : result)
        {
            System.out.print(token.getValue() + " ");
        }
    }

    public void addToken (Token token) // добавление токена в финальный список
    {
        result.add(token);
      //  System.out.print(token.getValue() + " ");
    }
}

