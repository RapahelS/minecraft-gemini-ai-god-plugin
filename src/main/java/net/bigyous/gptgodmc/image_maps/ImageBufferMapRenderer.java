package net.bigyous.gptgodmc.image_maps;

import com.loohp.imageframe.objectholders.ImageMap;
import com.loohp.imageframe.objectholders.MutablePair;
import com.loohp.imageframe.utils.MapUtils;
import java.util.Collection;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapView;

public class ImageBufferMapRenderer extends ImageMap.ImageMapRenderer {
    private final ImageBufferMap parent;

    public ImageBufferMapRenderer(ImageBufferMap parent, int index) {
        super(parent.getManager(), parent, index);
        this.parent = parent;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public MutablePair<byte[], Collection<MapCursor>> renderMap(MapView mapView, Player player) {
        byte[] colors;
        if (this.parent.cachedColors != null && this.parent.cachedColors[this.index] != null) {
            colors = this.parent.cachedColors[this.index];
        } else if (this.parent.cachedImages[this.index] != null) {
            colors = MapUtils.toMapPaletteBytes(this.parent.cachedImages[this.index].get(), this.parent.getDitheringType());
        } else {
            colors = null;
        }

        Collection<MapCursor> cursors = ((Map) this.parent.getMapMarkers().get(this.index)).values();
        return new MutablePair(colors, cursors);
    }
}
