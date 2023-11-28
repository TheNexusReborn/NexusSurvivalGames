package com.thenexusreborn.survivalgames.util;

public record Range<V> (int min, int max, V value) {
    public boolean contains(int number) {
        return number >= min && number <= max;
    }
}
