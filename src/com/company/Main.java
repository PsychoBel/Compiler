package com.company;
import java.io.*;
import java.io.IOException;


public class Main
{

    public static void main(String[] args) throws LangParseException {
        String file = "";
        try(FileInputStream fin = new FileInputStream("/home/michael/programming/IdeaProjects/Compiler/test_of_code.txt")) // считываем файл
        {
            int i = -1;
            while((i = fin.read()) != -1)
            {
                file += (char)i; // посимвольно записываем в файл
            }
        }
        catch(IOException ex)
        {
            System.out.println(ex.getMessage());
        }

        Lexer lexer = new Lexer(file);

        System.out.println(lexer.fileInputGetter());
        System.out.println(lexer.checkToken());
        for (int i = 0; i < lexer.checkToken().size(); i++)
        {
            System.out.println(lexer.checkToken().get(i).getType() + " " + lexer.checkToken().get(i).getValue());
        }

        Parser parser = new Parser(lexer.checkToken());
        parser.lang();
        System.out.println(parser.getVarTable().size());
        System.out.println(parser.getVarTable().keySet());
        System.out.println(parser.getVarTable().values());


        ReversePolisNotation polis = new ReversePolisNotation(lexer.checkToken());
        polis.make_polis();
        polis.getResult();
        System.out.println();
        System.out.print("NEEDED POLIS: a 1 = a 3 < p1 !F a a 1 + = b a = p2 ! c b 2 + =");
    }
}
