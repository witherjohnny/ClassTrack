package com.cao.classtrack.classes;

public class RoomItem {
    public String name;
    public int id;

    public RoomItem(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String toString() {
        return name;
    }
}
