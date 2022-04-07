import React from 'react'
import { apiGetRide, Ride } from './api'
import { RideCard } from './RideCard'

export const Homepage: React.FC = ()=>{
  const [isLoadingRide, setIsLoadingRide] = React.useState(false);
  const [rideStatus, setRideStatus] = React.useState<Record<string, Ride>>({});

  React.useEffect(()=>{
    updateRideStatus();
  }, []);

  const updateRideStatus = async ()=>{
    if (isLoadingRide) return;
    setIsLoadingRide(true);
    try {
      const rides = await apiGetRide();
      setRideStatus(rides);
    } catch (e) {
      console.log(e.message);
    }
    setIsLoadingRide(false);
  }

  const rideNames = Object.keys(rideStatus) || [];
  
  return (<>
    <h2> Welcome to USS! </h2>
    <button onClick={()=>updateRideStatus()}>Refresh</button>
    <div className='frame'>
      { 
        isLoadingRide 
        ? <div className='spinner' />
        : rideNames.length === 0 
        ? "No Rides Found."
        : rideNames.map(
          (rideName)=>
            <RideCard key={`ride-card-${rideName}`} ride={rideStatus[rideName]} />
        )
      }
    </div>
  </>)
}