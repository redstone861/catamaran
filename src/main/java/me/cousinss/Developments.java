package me.cousinss;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import me.cousinss.geometry.*;

public class Developments {

    private final Map<Player, Set<EdgeCoordinate>> roads;
    private final Map<Player, Set<VertexCoordinate>> villages;
    private final Map<Player, Set<VertexCoordinate>> cities;
    private final Map<Player, List<EdgeCoordinate>> longestRoads;
    private final Map<Player, Integer> lastLongestRoadChecks;

    public Developments() {
        roads = new HashMap<>();
        villages = new HashMap<>();
        cities = new HashMap<>();
        longestRoads = new HashMap<>();
        lastLongestRoadChecks = new HashMap<>();
    }

    public List<EdgeCoordinate> getLongestRoad(Player player, Collection<Player> blockingOpponents) {
        if(lastLongestRoadChecks.containsKey(player) && lastLongestRoadChecks.get(player) == getRoads(player).size()) {
            return longestRoads.get(player);
        }

        List<EdgeCoordinate> longestRoad = new ArrayList<>();
        for (EdgeCoordinate startEdge : this.getRoads(player)) {
            Set<EdgeCoordinate> visited = new HashSet<>();
            List<EdgeCoordinate> currentPath = new ArrayList<>();
            dfs(startEdge, null, visited, currentPath, longestRoad, player, blockingOpponents); //wrong
        }
        lastLongestRoadChecks.put(player, getRoads(player).size());
        longestRoads.put(player, longestRoad);
        return longestRoad;
    }

    private void dfs(
            EdgeCoordinate current,
            VertexCoordinate cameFromVertex,
            Set<EdgeCoordinate> visited,
            List<EdgeCoordinate> currentPath,
            List<EdgeCoordinate> longestPath,
            Player player,
            Collection<Player> blockingOpponents
    ) {
        visited.add(current);
        currentPath.add(current);
        if (currentPath.size() > longestPath.size()) {
            longestPath.clear();
            longestPath.addAll(currentPath);
        }

        // Explore neighbors
        for (VertexCoordinate vertex : current.getVertices()) {
            if (vertex.equals(cameFromVertex) || this.hasOpponentBuilding(vertex, blockingOpponents)) continue;
            for (EdgeCoordinate neighbor : vertex.getEdgeTriad()) {
                if (this.getRoads(player).contains(neighbor) && !visited.contains(neighbor)) {
                    dfs(neighbor, vertex, visited, currentPath, longestPath, player, blockingOpponents);
                }
            }
        }
        visited.remove(current);
        currentPath.removeLast();
    }

    private boolean hasOpponentBuilding(VertexCoordinate vertex, Collection<Player> blockingOpponents) {
        return blockingOpponents.stream().flatMap(p -> this.getBuildings(p).stream()).anyMatch(vertex::equals);
    }

    public Set<EdgeCoordinate> getRoads(Player player) {
        return Collections.unmodifiableSet(roads.getOrDefault(player, new HashSet<>()));
    }

    public Set<VertexCoordinate> getVillages(Player player) {
        return Collections.unmodifiableSet(villages.getOrDefault(player, new HashSet<>()));
    }

    public Set<VertexCoordinate> getCities(Player player) {
        return Collections.unmodifiableSet(cities.getOrDefault(player, new HashSet<>()));
    }

    public Set<VertexCoordinate> getBuildings(Player player) {
        HashSet<VertexCoordinate> buildings = new HashSet<>();
        buildings.addAll(getVillages(player));
        buildings.addAll(getCities(player));
        return Collections.unmodifiableSet(buildings);
    }

    public boolean addRoad(Player player, EdgeCoordinate road) {
        if(!roads.containsKey(player)) roads.put(player, new HashSet<>());
        return roads.get(player).add(road);
    }

    public boolean addVillage(Player player, VertexCoordinate village) {
        if(!villages.containsKey(player)) villages.put(player, new HashSet<>());
        return villages.get(player).add(village);
    }

    public boolean upgradeToCity(Player player, VertexCoordinate city) {
        if(!cities.containsKey(player)) cities.put(player, new HashSet<>());
        if(!villages.get(player).contains(city)) return false;
        villages.get(player).remove(city);
        return cities.get(player).add(city);
    }

    public Set<VertexCoordinate> getAdjacentVillages(TileCoordinate tile, Player player) {
        return this.getVillages(player).stream().filter(c -> tile.vertices().contains(c)).collect(Collectors.toSet());
    }

    public Set<VertexCoordinate> getAdjacentCities(TileCoordinate tile, Player player) {
        return this.getCities(player).stream().filter(c -> tile.vertices().contains(c)).collect(Collectors.toSet());
    }
}
