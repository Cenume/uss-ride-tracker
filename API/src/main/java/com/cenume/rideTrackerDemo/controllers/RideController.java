package com.cenume.rideTrackerDemo.controllers;

import java.util.Map;

import com.cenume.rideTrackerDemo.models.api.AddQueueRequest;
import com.cenume.rideTrackerDemo.models.api.StartRideRequest;
import com.cenume.rideTrackerDemo.models.Ride;
import com.cenume.rideTrackerDemo.services.RideService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class RideController {
	
	private RideService rideService;
	public RideController() {
		this.rideService = new RideService();
	}

	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/rideMap")
	public Map<String, Ride> rideMap() {
		return rideService.getRideMap();
	}

	@PostMapping("/addQueue")
	public boolean addQueue(@RequestBody AddQueueRequest addQueueRequest ) {
		boolean isSuccessfulAddQueue = rideService.addQueue(addQueueRequest.getRideName(), addQueueRequest.getSize());
		return isSuccessfulAddQueue;
	}

	@PostMapping("/startRide")
	public boolean startRide(@RequestBody StartRideRequest startRideRequest ) {
		boolean isSuccessfulRideStart = rideService.startRide(startRideRequest.getRideName());
		return isSuccessfulRideStart;
	}

	@PostMapping("/flushRides")
	public boolean flushRides() {
		boolean isSuccessfulFlushRides = rideService.flushRides();
		return isSuccessfulFlushRides;
	}
}