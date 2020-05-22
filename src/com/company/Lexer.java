package com.company;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;


public class Lexer
{
    private  String fileInput; // переменная класса String
    private List<Token> tokens = new ArrayList<>(); // создаем массив токенов


    public List<Token> getToken() // getter токенов
    {
        return tokens;
    }

    public Lexer(String fileInput)  // конструктор
    {
        this.fileInput = fileInput;
    }

    public String fileInputGetter() // getter (для тестов)
    {
        return this.fileInput;
    }

    public List<Token> checkToken() // проверка токенов в нашем файле
    {
        this.tokens = new ArrayList<>();
        this.fileInput = this.fileInput.replaceAll("\n",""); // убираем переходы переходы от строки к строке
        int startIndex = 0;
        int nowIndex = 0;
        LexemType prevLexem = null;
        LexemType nowLexem = null;
        String text = ""; // подстрока, которую будем сравнивать

        while (nowIndex < this.fileInput.length())
        {
            nowLexem = null;
            text = this.fileInput.substring(startIndex, nowIndex + 1);
            if (text.equals(" "))
            {
                startIndex++;
                nowIndex++;
                continue;
            }

            for (LexemType lexem : LexemType.values()) // итерируемся по лексемам и сравниваем их с нашей текущей подстрокой
            {
                Matcher matcher = lexem.getPattern().matcher(text);

                if (matcher.find()) // если совпадение, то останавливаем
                {
                    nowLexem = lexem;
                    break;
                }

            }

            if (nowLexem != null) // если не null, то идем дальше
            {
                text = this.fileInput.substring(startIndex, nowIndex + 1);
                prevLexem = nowLexem;
            }

            if (nowLexem == null) // добавление нового токена к списку токенов и изменение начальной позиции подсписка
            {
                Token token = new Token(prevLexem, text.substring(0, text.length() - 1));
                this.tokens.add(token);
                startIndex = nowIndex;
            }
            else
            {
                nowIndex++;
            }
            if (nowIndex == this.fileInput.length())
            {
                Token token = new Token(prevLexem, text);
                this.tokens.add(token);
            }



        }
        return tokens;
    }


}
