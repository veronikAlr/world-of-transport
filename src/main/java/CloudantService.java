import com.ibm.cloud.cloudant.v1.Cloudant;
import com.ibm.cloud.cloudant.v1.model.PostSearchOptions;
import com.ibm.cloud.cloudant.v1.model.SearchResult;
import com.ibm.cloud.cloudant.v1.model.SearchResultRow;
import com.ibm.cloud.sdk.core.security.NoAuthAuthenticator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CloudantService {

    private final Cloudant service;

    public CloudantService() {
        NoAuthAuthenticator authenticator = new NoAuthAuthenticator();
        service = new Cloudant("no-auth", authenticator);
        service.setServiceUrl("https://mikerhodes.cloudant.com");
    }

    @NotNull
    private String buildQuery(double userLat, double userLon, double radiusKm) {
        double latDelta = radiusKm / 111.0; //Convert km to degree (1 deg is approx 111km)
        double lonDelta = radiusKm / (111.0 * Math.cos(Math.toRadians(userLat))); //Longitude lines converge at the poles

        double minLat = userLat - latDelta;
        double maxLat = userLat + latDelta;
        double minLon = userLon - lonDelta;
        double maxLon = userLon + lonDelta;

        return String.format(
                "lat:[%.6f TO %.6f] AND lon:[%.6f TO %.6f]",
                minLat, maxLat, minLon, maxLon
        );
    }

    public List<Hub> getHubsNear(double userLat, double userLon, double radiusKm) {
        String query = buildQuery(userLat, userLon, radiusKm);
        List<String> sort = List.of(String.format(
                "<distance,lon,lat,%.6f,%.6f,km>", userLon, userLat
        ));
        PostSearchOptions options = new PostSearchOptions.Builder()
                .db("airportdb")
                .ddoc("view1")
                .index("geo")
                .query(query)
                .sort(sort)
                .build();

        List<Hub> hubs = new ArrayList<>();

        try {
            SearchResult result = service.postSearch(options).execute().getResult();

            for (SearchResultRow row : result.getRows()) {
                Object latObj = row.getFields().get("lat");
                Object lonObj = row.getFields().get("lon");
                Object nameObj = row.getFields().get("name");

                if (latObj instanceof Number && lonObj instanceof Number && nameObj instanceof String name) {
                    double lat = ((Number) latObj).doubleValue();
                    double lon = ((Number) lonObj).doubleValue();

                    // use haversine formula for a more precise distance as it will take in account the curvature of the Earth
                    double distance = haversine(userLat, userLon, lat, lon);

                    if (distance <= radiusKm) {
                        hubs.add(new Hub(name, distance));
                    }
                }
            }
            // resort after haversine
            hubs.sort(Comparator.comparingDouble(Hub::getDistanceKm));

        } catch (Exception e) {
            System.err.println("Error fetching data: " + e.getMessage());
        }

        return hubs;
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }
}
