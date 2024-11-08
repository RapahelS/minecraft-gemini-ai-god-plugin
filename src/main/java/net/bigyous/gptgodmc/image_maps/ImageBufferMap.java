package net.bigyous.gptgodmc.image_maps;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import com.loohp.imageframe.api.events.ImageMapUpdatedEvent;
import com.loohp.imageframe.objectholders.DitheringType;
import com.loohp.imageframe.objectholders.FileLazyMappedBufferedImage;
import com.loohp.imageframe.objectholders.ImageMap;
import com.loohp.imageframe.objectholders.ImageMapAccessPermissionType;
import com.loohp.imageframe.objectholders.ImageMapManager;
import com.loohp.imageframe.utils.FutureUtils;
import com.loohp.imageframe.utils.MapUtils;

import net.bigyous.gptgodmc.GPTGOD;

public class ImageBufferMap extends ImageMap {

    private BufferedImage image;

    protected final FileLazyMappedBufferedImage[] cachedImages;

    protected byte[][] cachedColors = null;

    public static Future<? extends ImageBufferMap> create(ImageMapManager manager, String name, BufferedImage imageBytes, int width, int height, DitheringType ditheringType, UUID creator) throws Exception {
        World world = MapUtils.getMainWorld();
        int mapsCount = width * height;
        List<Future<MapView>> mapViewsFuture = new ArrayList<>(mapsCount);
        List<Map<String, MapCursor>> markers = new ArrayList<>(mapsCount);
        for (int i = 0; i < mapsCount; i++) {
            mapViewsFuture.add(MapUtils.createMap(world));
            markers.add(new ConcurrentHashMap<>());
        }
        List<MapView> mapViews = new ArrayList<>(mapsCount);
        List<Integer> mapIds = new ArrayList<>(mapsCount);
        for (Future<MapView> future : mapViewsFuture) {
            try {
                MapView mapView = future.get();
                GPTGOD.SERVER.getScheduler().runTask(JavaPlugin.getPlugin(GPTGOD.class), () -> {
                    for (MapRenderer renderer : mapView.getRenderers()) {
                        mapView.removeRenderer(renderer);
                    }
                });
                mapViews.add(mapView);
                mapIds.add(mapView.getId());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        ImageBufferMap map = new ImageBufferMap(manager, -1, name, imageBytes, new FileLazyMappedBufferedImage[mapsCount], mapViews, mapIds, markers, width, height, ditheringType, creator, Collections.emptyMap(), System.currentTimeMillis());
        return FutureUtils.callAsyncMethod(() -> {
            FutureUtils.callSyncMethod(() -> {
                for (int i = 0; i < mapViews.size(); i++) {
                    mapViews.get(i).addRenderer(new ImageBufferMapRenderer(map, i));
                }
            }).get();
            map.update(false);
            return map;
        });
    }

    public ImageBufferMap(ImageMapManager manager, int imageIndex, String name, BufferedImage imageBytes, FileLazyMappedBufferedImage[] cachedImages, List<MapView> mapViews, List<Integer> mapIds, List<Map<String, MapCursor>> mapMarkers, int width, int height, DitheringType ditheringType, UUID creator, Map<UUID, ImageMapAccessPermissionType> hasAccess, long creationTime) {
        super(manager, imageIndex, name, mapViews, mapIds, mapMarkers, width, height, ditheringType, creator, hasAccess, creationTime);
        this.image = imageBytes;
        this.cachedImages = cachedImages;
    }

    @Override
    public void cacheColors() {}

    @Override
    public void clearCachedColors() {}

    @Override
    public ImageMap deepClone(String arg0, UUID arg1) throws Exception {
        return (ImageMap)this.clone();
    }

    // handle saving to disk
    @Override
    public void save() throws Exception {}

    @Override
    public void update(boolean save) throws Exception {
        image = MapUtils.resize(image, width, height);
        int i = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cachedImages[i++] = FileLazyMappedBufferedImage.fromImage(MapUtils.getSubImage(image, x, y));
            }
        }
        cacheColors();
        Bukkit.getPluginManager().callEvent(new ImageMapUpdatedEvent(this));
        send(getViewers());
        if (save) {
            save();
        }
    }
    
}
