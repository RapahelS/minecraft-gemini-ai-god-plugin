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
        byte[] cached = this.parent.getCachedColorsAt(this.index);
        if (cached != null) {
            colors = cached;
        } else if (this.parent.getCachedImageAt(this.index) != null) {
            colors = MapUtils.toMapPaletteBytes(this.parent.getCachedImageAt(this.index).get(), this.parent.getDitheringType());
        } else {
            colors = null;
        }

        Collection<MapCursor> cursors = ((Map) this.parent.getMapMarkers().get(this.index)).values();
        return new MutablePair(colors, cursors);
    }
}
