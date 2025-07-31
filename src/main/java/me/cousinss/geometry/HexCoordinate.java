package me.cousinss.geometry;

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

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof HexCoordinate<?> other)) return false;
        HexCoordinate<T> v1 = this.asDominant();
        HexCoordinate<?> v2 = other.asDominant();
        return v1.tile.equals(v2.tile) && v1.direction.equals(v2.direction);
    }
}
