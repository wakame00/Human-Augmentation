package com.wakame.humanaugmentation.body;

public enum BodySlot {
    HEART(2, true), LUNGS(2, true), DIGESTIVE(2, true), BLOOD(2, true), EYES(2, true), BRAIN(2, true),
    SPECIAL(4, false), MUSCLES(4, false), SKELETON(4, false), SKIN(3, false), SPINE(3, false);

    private final int capacity;
    private final boolean organ;

    BodySlot(int capacity, boolean organ) {
        this.capacity = capacity;
        this.organ = organ;
    }

    public int capacity() {
        return capacity;
    }

    public boolean isOrgan() {
        return organ;
    }
}
