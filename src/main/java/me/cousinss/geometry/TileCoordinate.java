package me.cousinss.geometry;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public record TileCoordinate(int q, int r) {

    public Set<VertexCoordinate> vertices() {
        return Arrays.stream(VertexDirection.values()).map(d -> new VertexCoordinate(this, d)).collect(Collectors.toSet());
    }

    public boolean isOrigin() {
        return q == 0 && r == 0;
    }

    public static TileCoordinate origin() {
        return new TileCoordinate(0, 0);
    }

    public TileCoordinate add(TileCoordinate coordinate) {
        return new TileCoordinate(this.q + coordinate.q, this.r + coordinate.r);
    }
}
