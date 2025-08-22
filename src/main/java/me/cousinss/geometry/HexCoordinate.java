package me.cousinss.geometry;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class HexCoordinate<T extends HexDirection<T>> {
    private final TileCoordinate tile;
    private final T direction;

    public HexCoordinate(TileCoordinate tile, T direction) {
        this.tile = tile;
        this.direction = direction;
    }

    public TileCoordinate getTile() {
        return tile;
    }

    public T getDirection() {
        return direction;
    }

    public HexCoordinate<T> asDominant() {
        HexCoordinate<T> dominantAdjust = this.direction.getDominantAdjust();
        return new HexCoordinate<T>(this.tile.add(dominantAdjust.tile), dominantAdjust.direction);
    }

    public HexCoordinate<T> add(TileCoordinate coordinate) {
        return new HexCoordinate<T>(this.getTile().add(coordinate), this.getDirection());
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof HexCoordinate<?> other)) return false;
        HexCoordinate<T> v1 = this.asDominant();
        HexCoordinate<?> v2 = other.asDominant();
        return v1.tile.equals(v2.tile) && v1.direction.equals(v2.direction);
    }

    @Override
    public String toString() {
        return "{" + tile.toString() + ", " + direction.toString() + "}";
    }

    @Override
    public int hashCode() {
        return this.asDominant().tile.hashCode() + this.asDominant().direction.hashCode();
    }
}
