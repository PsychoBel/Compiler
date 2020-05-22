package com.company;


public class Token
{
    private LexemType type;
    private String value;


    public Token(LexemType type, String value)
    {
        this.type = type;
        this.value = value;
    }


    public LexemType getType()
    {
        return type;
    }

    public String getValue()
    {
        return value;
    }


}
