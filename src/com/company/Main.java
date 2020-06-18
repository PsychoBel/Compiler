package com.company;
import java.io.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;


public class Main
{

    public static void main(String[] args) throws LangParseException {
        if (false) {
            HashSet hashSet = new HashSet();
            hashSet.add("test_1", 17);
            hashSet.add("test_2", 99);
            hashSet.add("test_3", -5);

            System.out.println("['test_1']: " + hashSet.getByKey("test_1"));
            System.out.println("['test_2']: " + hashSet.getByKey("test_2"));
            System.out.println("['test_3']: " + hashSet.getByKey("test_3"));
            return;
        }


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
        //System.out.println(lexer.checkToken());
        /*for (int i = 0; i < lexer.checkToken().size(); i++)
        {
            System.out.println(lexer.checkToken().get(i).getType() + " " + lexer.checkToken().get(i).getValue());
        }
        */

        Parser parser = new Parser(lexer.checkToken());
        parser.lang();
        if(!parser.checkBrackets())
        {
            System.out.println("ERROR: ERROR WITH BRACKETS");
        }
       // System.out.println(parser.getVarTable().keySet());
        //System.out.println(parser.getVarTable().values());


        ReversePolisNotation polis = new ReversePolisNotation(lexer.checkToken());
        polis.make_polis();
        //polis.getResult();
        //System.out.println();
        //System.out.println(polis.points.size());5
        //System.out.println(polis.points.keySet());
        //System.out.println(polis.points.values());


        StackMachine machine = new StackMachine(polis.result);
        machine.setVarTable(parser.tableForMachine);
        machine.setMarksPosiions(polis.points);
        machine.run();
    }
}
