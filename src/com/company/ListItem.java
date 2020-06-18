package com.company;
import java.awt.event.ItemListener;

public class ListItem
{
    public int value;
    public int index = 0;
    public String key;
    public int hash;
    public ListItem nextItemLink = null;

    public ListItem (int value) {
        this.value = value;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex () {
        return this.index;
    }

    public ListItem getNext() {
        return this.nextItemLink;
    }
    public void setNext (ListItem next) {
        this.nextItemLink = next;
    }
}