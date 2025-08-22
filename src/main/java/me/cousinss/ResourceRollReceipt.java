package me.cousinss;

import me.cousinss.geometry.TileCoordinate;
import me.cousinss.geometry.VertexCoordinate;

public record ResourceRollReceipt(TileCoordinate fromTile, VertexCoordinate fromVertex, Resource resource, int count) {

    public ResourceSet asResourceSet() {
        return new ResourceSet().set(resource, count);
    }

}
