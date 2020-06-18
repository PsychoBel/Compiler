package com.company;


public class HashSet {
    ListItem[] ListArray = new ListItem[16];

    public void add (String key, int value) {
        ListItem item = new ListItem(value);
        item.hash = getHash(key);
        item.key  = key;
        int itemIndex = getIndex(key);

        if (ListArray[itemIndex] == null) {
            item.setIndex(0);
            ListArray[itemIndex] = item;
        } else {
            ListItem nItem = ListArray[itemIndex];
            boolean isChanging = false;

            while (true) {
                if (nItem.key == key) {
                    isChanging = true;
                    break;
                }

                if (nItem.getNext() == null)
                    break;

                nItem = nItem.getNext();
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

        return hash;
    }

    private int getIndex(String key) {
        return getHash(key) % ListArray.length;
    }

    public int getByKey (String key) {
        int index = getIndex(key);

        ListItem nItem = ListArray[index];

        while (true) {
            if (nItem.key.compareTo(key) == 0)
                return nItem.value;

            if (nItem.getNext() == null)
                break;

            nItem = nItem.getNext();
        }
        return 0;

    }
}
