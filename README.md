# World of Transport

A simple Java command-line application that fetches nearby transport hubs from an IBM Cloudant database and displays them sorted by distance.

---

## Features

- Fetch transport hubs from the publicly accessible [airportdb Cloudant database](https://mikerhodes.cloudant.com/airportdb).
- List hubs within a user-specified radius (km) of a given latitude and longitude.
- Sort hubs by ascending distance.
- Simple command-line interface

---

## Requirements

- Java JDK 17 or higher
- Maven 3.8 or higher
- Internet access (to query the Cloudant database)

---

## Setup & Run

1. Make sure you have **Java 8 or higher** installed.

2. Clone the repository

3. Navigate to project folder. For example:
   ```bash
   cd path/to/WorldOfTransport
4. Run the application with Maven
    ```bash
   mvn clean compile exec:java
5. Follow the prompts in the console to enter your location and search radius.