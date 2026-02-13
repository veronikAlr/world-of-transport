import com.ibm.cloud.cloudant.v1.Cloudant;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static double readDouble(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            if (scanner.hasNextDouble()) {
                return scanner.nextDouble();
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next();
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("----------World of Transport----------");
        System.out.println("Please enter your location and distance:");

        double latitude = readDouble(scanner, "Latitude: ");
        double longitude = readDouble(scanner, "Longitude: ");
        double distanceKm = readDouble(scanner, "Distance (km): ");

        System.out.println("\nFetching nearby transport hubs...\n");
        CloudantService cs = new CloudantService();

        List<Hub> nearbyHubs = cs.getHubsNear(latitude, longitude, distanceKm);

        if (nearbyHubs.isEmpty()) {
            System.out.println("No hubs found within " + distanceKm + " km.");
        } else {
            System.out.println("Hubs within " + distanceKm + " km, sorted by distance:\n");
            for (Hub hub : nearbyHubs) {
                System.out.println(hub);
            }
        }
    }
}