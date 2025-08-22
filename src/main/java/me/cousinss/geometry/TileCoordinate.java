package me.cousinss.geometry;

import me.cousinss.Tile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record TileCoordinate(int q, int r) {

    public Set<VertexCoordinate> vertices() {
        return Arrays.stream(VertexDirection.values()).map(d -> new VertexCoordinate(this, d)).collect(Collectors.toSet());
    }

    public Set<EdgeCoordinate> edges() {
        return Arrays.stream(EdgeDirection.values()).map(d -> new EdgeCoordinate(this, d)).collect(Collectors.toSet());
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

    public TileCoordinate scaleVector(int factor) {
        return new TileCoordinate(this.q * factor, this.r * factor);
    }

    public static List<TileCoordinate> ring(int radius) {
        List<TileCoordinate> ring = new ArrayList<>();
        if (radius == 0) {
            ring.add(TileCoordinate.origin());
            return ring;
        }
        TileCoordinate runner = EdgeDirection.UPLEFT.getCoordinateVector().scaleVector(radius);
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < radius; j++) {
                ring.add(runner);
                runner = runner.add(EdgeDirection.RIGHT.turn(i, true).getCoordinateVector());
            }
        }
        return ring;
    }

    private int calculateS() {
        return -this.q - this.r;
    }

    //one-sixth turns (60 degrees)
    public TileCoordinate rotateAboutOrigin(int turns, boolean clockwise) {
        TileCoordinate coordinate = this;
        for (int i = 0; i < turns; i++) {
            coordinate = clockwise ? new TileCoordinate(-coordinate.r, -coordinate.calculateS()) :
                    new TileCoordinate(-coordinate.calculateS(), -coordinate.q);
        }
        return coordinate;
    }
}


