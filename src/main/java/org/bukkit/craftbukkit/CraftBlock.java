
package org.bukkit.craftbukkit;

import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.block.CraftSign;

public class CraftBlock implements Block {
    private final CraftWorld world;
    private final CraftChunk chunk;
    private final int x;
    private final int y;
    private final int z;
    protected int type;
    protected byte data;
    protected byte light;

    protected CraftBlock(final CraftWorld world, final int x, final int y, final int z, final int type, final byte data) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.data = data;
        this.light = (byte)world.getHandle().i(x, y, z);
        this.chunk = (CraftChunk)world.getChunkAt(x << 4, z << 4);
    }

    protected CraftBlock(final CraftWorld world, final int x, final int y,
            final int z, final int type, final byte data, final byte light) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.data = data;
        this.light = light;
        this.chunk = (CraftChunk)world.getChunkAt(x << 4, z << 4);
    }

    /**
     * Gets the world which contains this Block
     *
     * @return World containing this block
     */
    public World getWorld() {
        return world;
    }

    /**
     * Gets the x-coordinate of this block
     *
     * @return x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y-coordinate of this block
     *
     * @return y-coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Gets the z-coordinate of this block
     *
     * @return z-coordinate
     */
    public int getZ() {
        return z;
    }

    /**
     * Gets the chunk which contains this block
     *
     * @return Containing Chunk
     */
    public Chunk getChunk() {
        return chunk;
    }

    /**
     * Sets the metadata for this block
     *
     * @param data New block specific metadata
     */
    public void setData(final byte data) {
        this.data = data;
        world.getHandle().c(x, y, z, data);
    }

    /**
     * Gets the metadata for this block
     *
     * @return block specific metadata
     */
    public byte getData() {
        return data;
    }

    /**
     * Sets the type of this block
     *
     * @param type Material to change this block to
     */
    public void setType(final Material type) {
        setTypeID(type.getID());
    }

    /**
     * Sets the type-ID of this block
     *
     * @param type Type-ID to change this block to
     */
    public void setTypeID(final int type) {
        this.type = type;
        world.getHandle().d(x, y, z, type);
    }

    /**
     * Gets the type of this block
     *
     * @return block type
     */
    public Material getType() {
        return Material.getMaterial(getTypeID());
    }

    /**
     * Gets the type-ID of this block
     *
     * @return block type-ID
     */
    public int getTypeID() {
        return type;
    }
    
    /**
     * Gets the light level between 0-15
     * 
     * @return light level
     */
    public byte getLightLevel() {
        return light;
    }

    /**
     * Gets the block at the given face
     *
     * @param face Face of this block to return
     * @return Block at the given face
     */
    public Block getFace(final BlockFace face) {
        return getFace(face, 1);
    }

    /**
     * Gets the block at the given distance of the given face<br />
     * <br />
     * For example, the following method places water at 100,102,100; two blocks
     * above 100,100,100.
     * <pre>
     * Block block = world.getBlockAt(100,100,100);
     * Block shower = block.getFace(BlockFace.Up, 2);
     * shower.setType(Material.WATER);
     * </pre>
     *
     * @param face Face of this block to return
     * @param distance Distance to get the block at
     * @return Block at the given face
     */
    public Block getFace(final BlockFace face, final int distance) {
        return getRelative(face.getModX() * distance, face.getModY() * distance,
                face.getModZ() * distance);
    }

    /**
     * Gets the block at the given offsets
     *
     * @param modX X-coordinate offset
     * @param modY Y-coordinate offset
     * @param modZ Z-coordinate offset
     * @return Block at the given offsets
     */
    public Block getRelative(final int modX, final int modY, final int modZ) {
        return getWorld().getBlockAt(getX() + modX, getY() + modY, getZ() + modZ);
    }

    /**
     * Gets the face relation of this block compared to the given block<br />
     * <br />
     * For example:
     * <pre>
     * Block current = world.getBlockAt(100, 100, 100);
     * Block target = world.getBlockAt(100, 101, 100);
     *
     * current.getFace(target) == BlockFace.Up;
     * </pre>
     * <br />
     * If the given block is not connected to this block, null may be returned
     *
     * @param block Block to compare against this block
     * @return BlockFace of this block which has the requested block, or null
     */
    public BlockFace getFace(final Block block) {
        BlockFace[] values = BlockFace.values();

        for (BlockFace face : values) {
            if (
                    (this.getX() + face.getModX() == block.getX()) &&
                    (this.getY() + face.getModY() == block.getY()) &&
                    (this.getZ() + face.getModZ() == block.getZ())
                ) {
                return face;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return "CraftBlock{" + "world=" + world + "x=" + x + "y=" + y + "z=" + z + "type=" + type + "data=" + data + '}';
    }
    
    /**
     * Notch uses a 0-5 to mean Down, Up, East, West, North, South
     * in that order all over. This method is convenience to convert for us.
     * 
     * @return BlockFace the BlockFace represented by this number
     */
    public static BlockFace notchToBlockFace(int notch) {
        switch (notch) {
        case 0:
            return BlockFace.Down;
        case 1:
            return BlockFace.Up;
        case 2:
            return BlockFace.East;
        case 3:
            return BlockFace.West;
        case 4:
            return BlockFace.North;
        case 5:
            return BlockFace.South;
        default:
            return BlockFace.Self;
        }
    }

    public BlockState getState() {
        Material material = getType();

        switch (material) {
            case Sign:
            case SignPost:
            case WallSign:
                return new CraftSign(this);
            default:
                return new CraftBlockState(this);
        }
    }
}
