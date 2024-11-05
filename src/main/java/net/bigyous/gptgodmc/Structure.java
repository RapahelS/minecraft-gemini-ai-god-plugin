package net.bigyous.gptgodmc;

import java.util.HashSet;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class Structure {
    private HashSet<Vector> blocks;
    private Player builder;
    private Location location;
    private Location[] bounds;
    private World world;

    // for tracking when to recalculate the centroid
    private static final int minimumBlocksForRecalculate = 4;
    private int lastCenterCalculated = 0;
    private int lastBoundsCalcuated = 0;

    public Structure(Location block, Player builder) {
        this.blocks = new HashSet<Vector>();
        this.addBlock(block);
        this.builder = builder;
        this.location = null;
        this.world = block.getWorld();
    }

    public Player getBuilder() {
        return builder;
    }

    public boolean containsBlock(Location block) {
        return blocks.contains(block.toVector());
    }

    public void removeBlock(Location block) {
        blocks.remove(block.toVector());
    }

    public void addBlock(Location block) {
        blocks.add(block.toVector());
        if (location != null)
            location = null;
        if (world == null)
            world = block.getWorld();
    }

    private Location calculateCentroid() {
        double x = 0;
        double y = 0;
        double z = 0;
        int size = blocks.size();
        for (Vector location : blocks) {
            x += location.getX();
            y += location.getY();
            z += location.getZ();
        }
        return new Location(world, x / size, y / size, z / size);
    }

    // returns the upper and lower bound of the structure
    private Location[] calculateBounds() {
        // walk up to top xyz
        double tx = 0, ty = 0, tz = 0;
        // walk down to bottom xyz
        double bx = Double.MAX_VALUE, by = Double.MAX_VALUE, bz = Double.MAX_VALUE;
        for (Vector location : blocks) {
            bx = Double.min(bx, location.getX());
            tx = Double.max(tx, location.getX());
            by = Double.min(by, location.getY());
            ty = Double.max(ty, location.getY());
            bz = Double.min(bz, location.getZ());
            tz = Double.max(tz, location.getZ());
        }
        return new Location[] { new Location(world, bx, by, bz), new Location(world, tx, ty, tz), };
    }

    // get center of building and calculate it if the building is new or changed in
    // size
    public Location getLocation() {
        int blockCount = blocks.size();
        if (location == null || (blockCount - minimumBlocksForRecalculate) > lastCenterCalculated) {
            location = calculateCentroid();
            lastCenterCalculated = blockCount;
        }
        return location;
    }

    public Location[] getBounds() {
        int blockCount = blocks.size();
        if( bounds == null || (blockCount - minimumBlocksForRecalculate) > lastBoundsCalcuated) {
            bounds = calculateBounds();
            lastBoundsCalcuated = blockCount;
        }
        return bounds;
    }

    public boolean isBlockConnected(Location block) {
        if (world == null)
            return false;
        int[] directions = { -1, 0, 1 };
        for (int xDirection : directions) {
            for (int yDirection : directions) {
                for (int zDirection : directions) {
                    Vector location = new Vector(block.getX() + xDirection, block.getY() + yDirection,
                            block.getZ() + zDirection);
                    if (blocks.contains(location)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public List<Block> getBlocks() {
        return blocks.stream().map((Vector vector) -> {
            return WorldManager.getCurrentWorld().getBlockAt(vector.getBlockX(), vector.getBlockY(),
                    vector.getBlockZ());
        }).toList();
    }

    public HashSet<Vector> getVectors() {
        return this.blocks;
    }

    public void merge(Structure other) {
        blocks.addAll(other.getVectors());
    }

    public int getSize() {
        return blocks.size();
    }

    @Override
    public String toString() {
        return String.format("Structure: Location: %s Size: %d Builder: %s", this.getLocation().toVector().toString(),
                this.getSize(), this.getBuilder().getName());
    }
}
