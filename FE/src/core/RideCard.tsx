import React from 'react'
import { Ride } from './api'

type Props = {
  ride: Ride
}

export const RideCard: React.FC<Props> = ({ ride })=>{
  
  const estimatedWait = getEstimatedWait(ride);

  return <div className='card'>
    <h3>{ride.name}</h3>
    <p>Ride Capacity: {ride.capacity}</p>
    <p>Ride Duration: {secondsToMinuteString(ride.duration)}</p>
    <p>Estimated Wait: {secondsToMinuteString(estimatedWait)} </p>
  </div>
}

const secondsToMinuteString = (seconds: number)=>{
  return `${Math.floor(seconds / 60) || 'Less than one '} minutes`
}

const getEstimatedWait = (ride: Ride) => {
  let remainingLaps = 0;
  let sum = 0;
  for (let group of ride.currentQueue) {
    sum += group;
    if (sum >= ride.capacity) {
      sum = 0;
      remainingLaps++;
    }
  }
  const currentTime = Math.floor(Date.now() / 1000);
  let currentLapRemainingSeconds = ride.duration - (currentTime - ride.lastStart);
  if (currentLapRemainingSeconds < 0) currentLapRemainingSeconds = 0; 
  return (remainingLaps * ride.duration) + currentLapRemainingSeconds
}