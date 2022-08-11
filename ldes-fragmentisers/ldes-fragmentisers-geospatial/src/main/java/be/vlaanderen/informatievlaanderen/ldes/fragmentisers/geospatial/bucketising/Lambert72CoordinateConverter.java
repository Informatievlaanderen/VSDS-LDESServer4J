package be.vlaanderen.informatievlaanderen.ldes.fragmentisers.geospatial.bucketising;

import org.locationtech.jts.geom.Coordinate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(
        value = "geospatial.projection",
        havingValue = "lambert72",
        matchIfMissing = true)
public class Lambert72CoordinateConverter implements CoordinateConverter {

    private static final double N = 0.77164219;
    private static final double F = 1.81329763;
    private static final double THETA_FUDGE = 0.00014204;
    private static final double E = 0.08199189;
    private static final double A = 6378388;
    private static final double X_DIFF = 149910;
    private static final double Y_DIFF = 5400150;

    private static final double THETA_0 = 0.07604294;

    public Coordinate convertCoordinate(final Coordinate coordinate){
        return lambert72toWGS84(coordinate.x, coordinate.y);
    }

    private Coordinate lambert72toWGS84(double x, double y) {
        double xReal = X_DIFF - x;
        double yReal = Y_DIFF - y;

        double rho = Math.sqrt(xReal * xReal + yReal * yReal);
        double theta = Math.atan(xReal / -yReal);

        double newLongitude = (THETA_0 + (theta + THETA_FUDGE) / N) * 180 / Math.PI;
        double newLatitude = 0;

        for (int i = 0; i < 5 ; ++i) {
            newLatitude = (2 * Math.atan(Math.pow(F * A / rho, 1 / N) * Math.pow((1 + E * Math.sin(newLatitude)) / (1 - E * Math.sin(newLatitude)), E / 2))) - Math.PI / 2;
        }
        newLatitude *= 180 / Math.PI;
        return new Coordinate(newLongitude, newLatitude);
    }
}
