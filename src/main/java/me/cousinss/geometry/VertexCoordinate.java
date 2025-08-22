package me.cousinss.geometry;

import java.util.Set;
import java.util.stream.Collectors;

public class VertexCoordinate extends HexCoordinate<VertexDirection> {
    public VertexCoordinate(TileCoordinate tile, VertexDirection direction) {
        super(tile, direction);
    }

    public VertexCoordinate(HexCoordinate<VertexDirection> coordinate) {
        super(coordinate.getTile(), coordinate.getDirection());
    }

    public Set<EdgeCoordinate> getEdgeTriad() {
        return this.getDirection().getEdgeTriad().stream().map(ed -> new EdgeCoordinate(ed.add(this.getTile()))).collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VertexCoordinate vc)) return false;
        return this.asDominant().equals(vc.asDominant());
    }
}
