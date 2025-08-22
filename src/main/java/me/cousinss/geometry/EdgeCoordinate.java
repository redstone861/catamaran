package me.cousinss.geometry;

import java.awt.event.WindowStateListener;
import java.util.Set;
import java.util.stream.Collectors;

public class EdgeCoordinate extends HexCoordinate<EdgeDirection> {
    public EdgeCoordinate(TileCoordinate tile, EdgeDirection direction) {
        super(tile, direction);
    }

    public EdgeCoordinate(HexCoordinate<EdgeDirection> edgeCoordinate) {
        this(edgeCoordinate.getTile(), edgeCoordinate.getDirection());
    }

    public Set<VertexCoordinate> getVertices() {
        return this.getDirection().getAdjacentVertices().stream().map(vd -> new VertexCoordinate(this.getTile(), vd)).collect(Collectors.toUnmodifiableSet());
    }

    public Set<TileCoordinate> getAdjacentTiles() {
        return Set.of(this.getTile(), this.getTile().add(this.getDirection().getCoordinateVector()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EdgeCoordinate ec)) return false;
        return this.asDominant().equals(ec.asDominant());
    }
}
