public class Hub {
    private final String name;
    private final double distanceKm;

    public Hub(String name, double distanceKm) {
        this.name = name;
        this.distanceKm = distanceKm;
    }

    public double getDistanceKm() { return distanceKm; }

    @Override
    public String toString() {
        return String.format("%s - %.2f km", name, distanceKm);
    }
}
