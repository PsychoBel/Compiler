package com.company;
import java.util.ArrayList;
import java.util.List;

public class LinkedList
{
    private ListItem firstItem = null;

    public void add (int value)
    {
        ListItem item = new ListItem(value);

        if (firstItem == null)
        {
            item.setIndex(0);
            firstItem = item;
        } else
            {
            ListItem nItem = firstItem; // используется для итерации и поиска последнего

            while (true) {
                if (nItem.getNext() == null)
                    break;

                nItem = nItem.getNext(); // итерируемся. На выходе будет последний элемент списка
            }

            nItem.setNext(item);
            item.setIndex(nItem.getIndex() + 1);
        }
    }

    public int getByIndex (int index)
    {
        if (index == 0)
            return firstItem.value;

        int counter = 0;
        ListItem nItem = firstItem;

        while (counter < index)
        {
            if (nItem.getNext() == null) // я пока не знаю как правильно
                break;                   // если запрошу n+1 элемент - выход за рамки - вернет последний (пока что)

            nItem = nItem.getNext();
            counter++;
        }

        return  nItem.value;
    }
}