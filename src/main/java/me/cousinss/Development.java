package me.cousinss;

public enum Development {

    ROAD(0, new ResourceSet().set(Resource.WOOD, 1).set(Resource.BRICK, 1)),
    VILLAGE(1, new ResourceSet().set(Resource.WOOD, 1).set(Resource.BRICK, 1).set(Resource.WHEAT, 1).set(Resource.SHEEP, 1)),
    CITY(2, new ResourceSet().set(Resource.WHEAT, 2).set(Resource.ORE, 3));

    final int score;
    final ResourceSet cost;

    Development(int score, ResourceSet cost) {
        this.score = score;
        this.cost = cost;
    }

    public int getScore() {
        return score;
    }

    public ResourceSet getCost() {
        return cost;
    }

    public boolean canBuild(ResourceSet with) {
        return with.contains(this.cost);
    }
}
