package com.cenume.rideTrackerDemo.services;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Map;

import com.cenume.rideTrackerDemo.models.Ride;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

/**
 * Ride Service
 * Core service to provide ride utilities such as getting ride status,
 * starting a ride and adding people to a queue.
 * 
 * Requires a JSON file containing ride information and working connection with redis
 * 
 */
public class RideService {
  private Map<String, Ride> rideMap;
  private RedisClient redisClient;
  private Gson gson;
  // Paths are hardcoded as of now
  private static final Path rideSourceFilePath = 
    Paths.get("/mnt/c/Users/nikol/Desktop/Misc Projects/uss-ride-tracker/API/src/resources/ridemap.json");
  private static final String redisClientURI = "redis://localhost:6379/0";

  public RideService() {
    this.gson = new Gson();
    this.redisClient = RedisClient.create(redisClientURI);
    this.loadBaseRideMap();
  }

  private void loadBaseRideMap() {
    try {
      Reader reader = Files.newBufferedReader(rideSourceFilePath);
      Type type = new TypeToken<Map<String, Ride>>(){}.getType();
      Map<String, Ride> map = this.gson.fromJson(reader, type);
      reader.close();
      this.rideMap = (Map<String, Ride>) map;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Map<String, Ride> getRideMap() {
    StatefulRedisConnection<String, String> connection = this.redisClient.connect();
    RedisCommands<String, String> syncCommands = connection.sync();
    for (Map.Entry<String, Ride> entry : this.rideMap.entrySet()) {
      Ride ride = getCachedRideSync(entry.getKey(), syncCommands);
      entry.setValue(ride);
    }
    return this.rideMap;
  }

  public boolean startRide(String rideName) {
    StatefulRedisConnection<String, String> connection = this.redisClient.connect();
    RedisCommands<String, String> syncCommands = connection.sync();
    Ride ride = getCachedRideSync(rideName, syncCommands);
    ArrayList<Integer> queue = ride.getCurrentQueue();
    queue = getReducedQueueByCapacity(queue, ride.getCapacity());
    ride.setCurrentQueue(queue);
    ride.setLastStart(Instant.now().getEpochSecond());
    String result = syncCommands.set(rideName, this.gson.toJson(ride));
    connection.close();    
    return (result == "OK");
  }

  /**
   * Function to automatically consume queue by ride's capacity
   * *In reality might actually need a manual human input 
   * @return queue after reducing
   */
  private ArrayList<Integer> getReducedQueueByCapacity(ArrayList<Integer> queue, int capacity) {
    ArrayList<Integer> newQueue = new ArrayList<Integer>(queue);
    while(newQueue.size() > 0 && capacity > newQueue.get(0)) {
      int queueSize = newQueue.remove(0);
      capacity -= queueSize;
    }
    return newQueue;
  }
  
  public boolean addQueue(String rideName, int queueSize) {
    StatefulRedisConnection<String, String> connection = this.redisClient.connect();
    RedisCommands<String, String> syncCommands = connection.sync();
    Ride ride = getCachedRideSync(rideName, syncCommands);
    ArrayList<Integer> currentQueue = ride.getCurrentQueue();
    currentQueue.add(queueSize);
    ride.setCurrentQueue(currentQueue);
    String result = syncCommands.set(rideName, this.gson.toJson(ride));
    connection.close();
    return (result == "OK");
  }

  public Ride getCachedRideSync (String rideName, RedisCommands<String, String> syncCommands ) {
    String stringifiedRideInfo = syncCommands.get(rideName);
    Ride ride;
    if (stringifiedRideInfo == null) {
      ride = new Ride(
        rideName, 
        this.rideMap.get(rideName).getDuration(), 
        this.rideMap.get(rideName).getCapacity()
      );
    } else {
      ride = this.gson.fromJson(stringifiedRideInfo, Ride.class);
    }
    return ride;
  }

  /**
   * Clear all rides in db
   * @return successful clearance of rides
   */
  public boolean flushRides() {
    StatefulRedisConnection<String, String> connection = this.redisClient.connect();
    RedisCommands<String, String> syncCommands = connection.sync();
    syncCommands.flushdb();
    return true;
  }
}
