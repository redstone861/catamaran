package me.cousinss;

import me.cousinss.geometry.TileCoordinate;
import me.cousinss.geometry.VertexCoordinate;

import java.util.Set;

public record Tile(Resource resource, int rollValue) {

    public boolean isDesert() {
        return null == this.resource;
    }

}
