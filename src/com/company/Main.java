package com.company;
import java.io.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;


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


        Parser parser = new Parser(lexer.checkToken());
        parser.lang();
        if(!parser.checkBrackets())
        {
            System.out.println("BRACKETS ERROR");
        }



        ReversePolisNotation polis = new ReversePolisNotation(lexer.checkToken());
        polis.make_polis();



        StackMachine machine = new StackMachine(polis.result);
        machine.setVarTable(parser.tableForMachine);
        machine.setMarksPosiions(polis.points);
        machine.run();
    }
}
