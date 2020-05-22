package com.company;
import java.util.ArrayList;
import java.util.List;


public class ListOfVariables // Тут будут хранится переменнеые, ктороые инцилизировались
{
    public static class OneOfVariables
    {
        String type = ""; // тип переменной
        String title = ""; // имя переменной
        String value = ""; // значение переменной
        // конструторы разных вариантов
        public OneOfVariables (String type, String title, String value)
        {
            this.title = title;
            this.type = type;
            this.value = value;
        }
        public OneOfVariables (String type, String title)
        {
            this.title = title;
            this.type = type;
        }
        public OneOfVariables(String title)
        {
            this.title = title;
        }
    }

    List<OneOfVariables> LOfVariables = new ArrayList<>(); // список, где будут хранится переменные

    public boolean addVariable(OneOfVariables variable) // добавление переменной в таблицу переменных
    {
        for (int i = 0; i < this.LOfVariables.size(); i++) // проверка есть ли уже переменная с таким именем и значением
        {
            OneOfVariables v = this.LOfVariables.get(i);
            if(v.title.equals(variable.title) && v.value.equals(variable.value))
            {
                return false;
            }
        }
        this.LOfVariables.add(variable);
        return true;
    }

    public boolean checkIfValueExist(OneOfVariables variable) // проверка на то, существует ли уже наша переменная в таблице переменных
    {

        for (int i = 0; i < this.LOfVariables.size(); i++)
        {
            OneOfVariables v = this.LOfVariables.get(i);

            System.out.println("v.title: " + v.title);
            System.out.println("variable.title: " + variable.title);

            if (v.title.equals(variable.title))
            {
                return true;
            }
        }
        return false;
    }

}
