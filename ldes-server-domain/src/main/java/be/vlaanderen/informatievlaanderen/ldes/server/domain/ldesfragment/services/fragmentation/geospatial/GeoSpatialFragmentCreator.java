package be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.fragmentation.geospatial;

import be.vlaanderen.informatievlaanderen.ldes.server.domain.config.LdesConfig;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.FragmentInfo;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.entities.LdesFragment;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragment.services.FragmentCreator;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesfragmentrequest.entities.FragmentPair;
import be.vlaanderen.informatievlaanderen.ldes.server.domain.ldesmember.entities.LdesMember;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
@Qualifier("geospatial")
@Primary
public class GeoSpatialFragmentCreator implements FragmentCreator {

    private final LdesConfig ldesConfig;
    private final GeospatialConfig geospatialConfig;

    public GeoSpatialFragmentCreator(LdesConfig ldesConfig, GeospatialConfig geospatialConfig) {
        this.ldesConfig = ldesConfig;
        this.geospatialConfig = geospatialConfig;
    }

    private static final String AS_WKT = "http://www.opengis.net/ont/geosparql#asWKT";

    @Override
    public LdesFragment createNewFragment(Optional<LdesFragment> optionalExistingLdesFragment, LdesMember firstMember) {
        try {
            Geometry geometry = new WKTReader().read(firstMember.getFragmentationValueGeo(AS_WKT));
            String tile = Arrays.stream(geometry.getCoordinates()).map(coordinate -> getTileNumber(coordinate.y, coordinate.x, geospatialConfig.getZoomLevel().intValue())).findFirst().get();
            System.out.println(tile);
            return createNewFragment(tile);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean needsToCreateNewFragment(LdesFragment fragment) {
        return false;
    }

    public static String getTileNumber(final double lat, final double lon, final int zoom) {
        int xtile = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
        int ytile = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 << zoom));
        if (xtile < 0)
            xtile = 0;
        if (xtile >= (1 << zoom))
            xtile = ((1 << zoom) - 1);
        if (ytile < 0)
            ytile = 0;
        if (ytile >= (1 << zoom))
            ytile = ((1 << zoom) - 1);
        return ("" + zoom + "/" + xtile + "/" + ytile);
    }

    private LdesFragment createNewFragment(String first) {
        return LdesFragment.newFragment(ldesConfig.getHostName(),
                new FragmentInfo(String.format("%s/%s", ldesConfig.getHostName(), ldesConfig.getCollectionName()), ldesConfig.getShape(), ldesConfig.getCollectionName(), List.of(new FragmentPair("tile", first))));
    }
}
