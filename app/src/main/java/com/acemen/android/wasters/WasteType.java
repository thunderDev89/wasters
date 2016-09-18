package com.acemen.android.wasters;

/**
 * Created by Audrik ! on 26/08/2016.
 */
public enum WasteType {
    BUTT("butt", "Mégot"),
    EXCREMENT("excrement", "Excrément"),
    TAG("tag", "Tag"),
    ANY_WASTE("any-waste", "Déchet quelconque"),
    FULL_TRASH("full-trash", "Poubelle pleine");

    private String type;
    private String name;

    WasteType(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
