package com.cenume.rideTrackerDemo.models;

import java.util.ArrayList;

/**
 * Ride Class representing Ride information
 */
public class Ride {
  private final String name;
  private final int duration;
  private final int capacity;
  private long lastStart;
  private ArrayList<Integer> currentQueue;

  public Ride(String name, int duration, int capacity) {
    this.name = name;
    this.duration = duration;
    this.capacity = capacity;
    this.setCurrentQueue(new ArrayList<Integer>(0));
  }
  
  public ArrayList<Integer> getCurrentQueue() {
    return currentQueue;
  }

  public void setCurrentQueue(ArrayList<Integer> currentQueue) {
    this.currentQueue = currentQueue;
  }

  public long getLastStart() {
    return lastStart;
  }

  public void setLastStart(long lastStart) {
    this.lastStart = lastStart;
  }

  public String getName() {
    return name;
  }

  public int getCapacity() {
    return capacity;
  }
  public int getDuration() {
    return duration;
  }
}
