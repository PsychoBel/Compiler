package com.company;


public class HashSet {
    ListItem[] LListArray = new ListItem[16];

    public void add (String key, int value) {
        ListItem item = new ListItem(value);
        item.hash = getHash(key);
        item.key  = key;
        int itemIndex = getIndex(key);

        if (LListArray[itemIndex] == null) {
            item.setIndex(0);
            LListArray[itemIndex] = item;
        } else {
            ListItem nItem = LListArray[itemIndex]; // используется для итерации и поиска последнего
            boolean isChanging = false;

            while (true) {
                if (nItem.key == key) {
                    isChanging = true;
                    break;
                }

                if (nItem.getNext() == null)
                    break;

                nItem = nItem.getNext(); // итерируемся. На выходе будет последний элемент списка
            }

            if (isChanging) {
                nItem.value = value;
            } else {
                nItem.setNext(item);
                item.setIndex(nItem.getIndex() + 1);
            }

        }
    }

    private int getHash (String key) {
        int hash = (int)key.charAt(0);
        //System.out.println("HashCode for key: " + key + " = " + hash);

        return hash;
    }

    private int getIndex(String key) {
        return getHash(key) % LListArray.length;
    }

    public int getByKey (String key) {
        int index = getIndex(key);

        ListItem nItem = LListArray[index]; // используется для итерации и поиска последнего

        while (true) {
            if (nItem.key.compareTo(key) == 0)
                return nItem.value;

            if (nItem.getNext() == null)
                break;

            nItem = nItem.getNext(); // итерируемся. На выходе будет последний элемент списка
        }
        return -1; //костыль бл

    }
}


class LListNode {
    public int test = 12;
}