package com.company;
import java.io.*;
import java.io.IOException;


public class Main
{

    public static void main(String[] args) throws LangParseException {
        String file = "";
        try(FileInputStream fin = new FileInputStream("/home/michael/IdeaProjects/Compiler/test.txt")) // считываем файл
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
    }
}
