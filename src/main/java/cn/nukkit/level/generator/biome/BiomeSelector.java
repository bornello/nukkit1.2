package cn.nukkit.level.generator.biome;

import cn.nukkit.level.generator.noise.Simplex;
import cn.nukkit.math.NukkitRandom;

import java.util.HashMap;
import java.util.Map;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BiomeSelector {
    private final Biome fallback;
    private final Simplex temperature;
    private final Simplex rainfall;

    private final Map<Integer, Biome> biomes = new HashMap<>();

    private int[] map = new int[64 * 64];

    public BiomeSelector(NukkitRandom nukkitRandom, Biome fallback) {
        this.fallback = fallback;
        this.temperature = new Simplex(nukkitRandom, 2F, 1F / 8F, 1F / 1024F);
        this.rainfall = new Simplex(nukkitRandom, 2F, 1F / 8F, 1F / 1024F);
    }

    public int lookup(double temperature, double rainfall) {
        if (rainfall < 0.25f) {
            return Biome.SWAMPLAND;
        } else if (rainfall < 0.60f) {
            if (temperature < 0.25f) {
                return Biome.ICE_FLATS;
            } else if (temperature < 0.75f) {
                return Biome.DESERT;
            } else {
                return Biome.SAVANNA;
            }
        } else if (rainfall < 0.80f) {
            if (temperature < 0.25f) {
                return Biome.TAIGA;
            } else {
                return Biome.FOREST;
            }
        } else {
            if (rainfall < 1.0f) {
                return Biome.JUNGLE;
            }
        }
        return Biome.PLAINS;
    }

    public void recalculate() {
        this.map = new int[64 * 64];
        for (int i = 0; i < 64; ++i) {
            for (int j = 0; j < 64; ++j) {
                this.map[i + (j << 6)] = this.lookup(i / 63d, j / 63d);
            }
        }
    }

    public void addBiome(Biome biome) {
        this.biomes.put(biome.getId(), biome);
    }

    public double getTemperature(double x, double z) {
        return (this.temperature.noise2D(x, z, true) + 1) / 2;
    }

    public double getRainfall(double x, double z) {
        return (this.rainfall.noise2D(x, z, true) + 1) / 2;
    }

    public Biome pickBiome(double x, double z) {
        int temperature = (int) (this.getTemperature(x, z) * 63);
        int rainfall = (int) (this.getRainfall(x, z) * 63);

        int biomeId = this.map[temperature + (rainfall << 6)];
        return this.biomes.containsKey(biomeId) ? this.biomes.get(biomeId) : this.fallback;
    }

}