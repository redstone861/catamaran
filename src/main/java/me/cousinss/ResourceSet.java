package me.cousinss;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

public class ResourceSet {
    private final Map<Resource, Integer> resources;

    public ResourceSet() {
        resources = new EnumMap<>(Resource.class);
        for(Resource r : Resource.values()) {
            resources.put(r, 0);
        }
    }

    /**
     * Sets the number of resources to the value and returns the Set.
     * @param resource the resource
     * @param n the new count value
     * @return the updated {@code ResourceSet}.
     */
    public ResourceSet set(Resource resource, int n) {
        resources.put(resource, n);
        return this;
    }

    public int count(Resource resource) {
        return resources.get(resource);
    }

    public int count(Collection<Resource> resources) {
        if(resources.isEmpty()) {
            return 0;
        }
        int count = 0;
        for(Resource r : EnumSet.copyOf(resources)) {
            count += this.count(r);
        }
        return count;
    }

    /**
     * Returns whether this Set contains the specified resource.
     * Equivalent to {@link #contains}(resource, 1).
     * @param resource the resource
     * @return {@code true} if this Set contains at least 1 of the specified resource, {@code false} otherwise.
     */
    public boolean contains(Resource resource) {
        return this.contains(resource, 1);
    }

    /**
     * Returns whether this Set contains at least {@code n} of the specified resource.
     * @param resource the resource.
     * @param n the count value.
     * @return {@code true} if this Set contains at least {@code n} of the specified resource, {@code false} otherwise.
     */
    public boolean contains(Resource resource, int n) {
        return this.count(resource) >= n;
    }

    /**
     * Whether the specified Set is a subset (inclusive) of this Set.
     * @param resourceSet the resource set.
     * @return {@code true} if this Set completely contains the contents of the specified Set, {@code false} otherwise.
     */
    public boolean contains(ResourceSet resourceSet) {
        for(Resource r : resources.keySet()) {
            if(!resourceSet.contains(r, this.count(r))) {
                return false;
            }
        }
        return true;
    }

    public int size() {
        return this.count(resources.keySet());
    }

    public int increment(Resource resource) {
        this.set(resource, this.count(resource) + 1);
        return this.count(resource);
    }

    public int decrement(Resource resource) {
        if(this.contains(resource)) {
            this.set(resource, this.count(resource) - 1);
            return this.count(resource);
        }
        return 0;
    }

    public int add(Resource resource, int count) {
        this.set(resource, this.count(resource) + count);
        return this.count(resource);
    }

    public int remove(Resource resource, int count) {
        if(this.contains(resource, count)) {
            this.set(resource, this.count(resource) - count);
            return this.count(resource);
        }
        this.set(resource, 0);
        return 0;
    }

    public boolean removeAll(Resource resource) {
        if(this.contains(resource)) {
            this.set(resource, 0);
            return true;
        }
        return false;
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }
}
