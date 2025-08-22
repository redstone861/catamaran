package me.cousinss.geometry;

import java.util.*;
import java.util.stream.Collectors;

public enum VertexDirection implements HexDirection<VertexDirection> {
    UP(0, 0, true),
    UPRIGHT(0, 0, false),
    DOWNRIGHT(0, 1, true),
    DOWN(-1, 1, false),
    DOWNLEFT(-1, 1, true),
    UPLEFT(-1, 0, false);

    static final Map<VertexDirection, VertexCoordinate> DOMINANT_ADJUST;
    private final TileCoordinate dominantVector;
    private final boolean dominantDirIsUp;
    static final Map<VertexDirection, Set<EdgeDirection>> ADJACENT_EDGES;

    VertexDirection(int q, int r, boolean dominantDirIsUp) {
        this.dominantVector = new TileCoordinate(q, r);
        this.dominantDirIsUp = dominantDirIsUp;
    }

    static {
        Map<VertexDirection, VertexCoordinate> map = new HashMap<>();
        Map<VertexDirection, Set<EdgeDirection>> edges = new HashMap<>();
        Map<VertexDirection, Set<EdgeCoordinate>> triad = new HashMap<>();
        for(VertexDirection d : VertexDirection.values()) {
            map.put(d, new VertexCoordinate(d.dominantVector, d.dominantDirIsUp ? VertexDirection.UP : VertexDirection.UPRIGHT));
            edges.put(d, EnumSet.of(EdgeDirection.values()[d.ordinal()], EdgeDirection.values()[d.ordinal() - 1 == -1 ? 5 : d.ordinal() - 1]));
        }
        DOMINANT_ADJUST = Collections.unmodifiableMap(map);
        ADJACENT_EDGES = Collections.unmodifiableMap(edges);
    }

    @Override
    public int getOrdinal() {
        return ordinal();
    }

    @Override
    public VertexDirection[] valueArray() {
        return VertexDirection.values();
    }

    public VertexCoordinate getDominantAdjust() {
        return DOMINANT_ADJUST.get(this);
    }

    public boolean isDominant() {
        return DOMINANT_ADJUST.get(this).getTile().isOrigin();
    }

    public Set<EdgeDirection> getAdjacentEdges() {
        return ADJACENT_EDGES.get(this);
    }

    public Set<EdgeCoordinate> getEdgeTriad() {
        Set<EdgeCoordinate> triad = new HashSet<>();
        triad.add(
                new EdgeCoordinate(EdgeDirection.values()[this.ordinal()].getCoordinateVector(), EdgeDirection.values()[this.ordinal()].turn(2, false))
        );
        triad.addAll(this.getAdjacentEdges().stream().map(ed -> {
            TileCoordinate vec = ed.getCoordinateVector();
            return new EdgeCoordinate(vec, ed.getOpposing());
        }).collect(Collectors.toSet()));
        return triad;
    }
}
