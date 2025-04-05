package com.cao.classtrack.classes;

public class LottoItem {
    public String name;
    public int id;

    public LottoItem(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
}

