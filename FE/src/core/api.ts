
export type Ride = {
  duration: number;
  capacity: number;
  currentQueue: number[];
  lastStart: number;
  name: string
}

const API_URL = 'http://localhost:8080'
const GET_RIDE_ENDPOINT = 'rideMap'

export const apiGetRide = async ()=>{
  try {
    const res = await fetch(
      `${API_URL}/${GET_RIDE_ENDPOINT}`
    )
    const rideRecord: Record<string, Ride> = await res.json(); 
    return rideRecord;
  } catch (e) {
    console.log(e.message);
  }
}