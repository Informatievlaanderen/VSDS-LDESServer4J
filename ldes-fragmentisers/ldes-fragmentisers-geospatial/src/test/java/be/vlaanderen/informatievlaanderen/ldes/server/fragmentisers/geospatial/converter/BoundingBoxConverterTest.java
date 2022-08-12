package be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.converter;

import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.Tile;
import be.vlaanderen.informatievlaanderen.ldes.server.fragmentisers.geospatial.connected.BoundingBox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoundingBoxConverterTest {

    @Test
    @DisplayName("Verify correct conversion of boundingBox to WKT")
    void when_ABoundingBoxIsConverted_WKTIsReturned(){
        String expectedWKT = "POLYGON ((4.32861328125 50.78510168548185, 4.32861328125 50.778155274659234, 4.317626953125 50.778155274659234, 4.317626953125 50.78510168548185, 4.32861328125 50.78510168548185))";
        BoundingBox boundingBox = new BoundingBox(new Tile(15, 16777, 11001));

        String actualWKT = BoundingBoxConverter.toWKT(boundingBox);

        assertEquals(expectedWKT, actualWKT);
    }

}