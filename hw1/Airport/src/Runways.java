import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by liujiaye on 4/11/17.
 */
public class Runways {

    private Airport m_airport;
    private int m_id;

    private boolean m_freeToLand;               //runway attribute
    private boolean m_freeToTakeOff;            //runway attribute

    public Runways(Airport airport, int Id){
        m_airport = airport;
        m_freeToLand = true;
        m_freeToTakeOff = true;
        m_id = Id;
    }

    public boolean getFreeToLand() {
        return m_freeToLand;
    }
    public boolean getFreeToTakeOff() {
        return m_freeToTakeOff;
    }

    public void setFreeToLand(boolean freeToLand) { m_freeToLand = freeToLand; }
    public void setFreeToTakeOff(boolean freeToTakeOff) { m_freeToTakeOff = freeToTakeOff; }

    // handle previous event and schedule next event
    public AirportEvent handle(Event event) {

        // cast event to airevent
        AirportEvent airEvent = (AirportEvent)event;
        // get the plane object of this airevent
        Airplane plane = airEvent.getPlane();

        switch(airEvent.getType()) {

            // arrive -> land
            case AirportEvent.PLANE_ARRIVES:

                // set the runway to land
                plane.setRunway(this);

                // declare a landedevent
                AirportEvent landedEvent = new AirportEvent(m_airport.getRunwayTimeToLand(), m_airport, AirportEvent.PLANE_LANDED, plane);
                System.out.println(String.format("%.2f",Simulator.getCurrentTime()) + ": "+ plane.getName()+" arrived at airport " + m_airport.getName());

                // occupy the runway to land
                m_freeToLand = false;
                return landedEvent;


            // land -> depart
            case AirportEvent.PLANE_LANDED:

                // randomly choose the destination airport from global airportlist
                Airport[] airportList = AirportSim.airportList;
                int max = airportList.length;

                Random rand = new Random(1);
                int destination = rand.nextInt(max);
                // select a different airport
                while (m_airport == airportList[destination]){
                    destination = rand.nextInt(max);
                }
                plane.setDestination(airportList[destination]);
                plane.setDepart(m_airport);

                // set the number of passengers arriving to the airport
                m_airport.setNumPassArrive(plane.getNumPassengers());

                // calculate circling time
                if (plane.getArriveTime()!= -1) {
                    m_airport.setCirclingTime( Simulator.getCurrentTime() - plane.getArriveTime() );
                }
                else {
                    plane.setArriveTime(0);
                }

                plane.clearRunway();
                System.out.println(String.format("%.2f",Simulator.getCurrentTime()) + ":" + plane.getName()+ " lands at airport " + m_airport.getName());
                AirportEvent departureEvent = new AirportEvent(m_airport.getRequiredTimeOnGround(), m_airport, AirportEvent.PLANE_DEPARTS, plane);

                return departureEvent;


            case AirportEvent.PLANE_DEPARTS:
                // stats on the number of passengers departing

                m_airport.setNumPassDepart(plane.getNumPassengers());

                // compute the flight time of next flight
                double flightTime = plane.getDistance()/plane.getSpeed();

                System.out.println(String.format("%.2f",Simulator.getCurrentTime()) + ":" + plane.getName()+ " departs from airport " + m_airport.getName());
                AirportEvent arriveEvent = new AirportEvent(flightTime, plane.getDestination(), AirportEvent.PLANE_ARRIVES, plane);
                m_freeToTakeOff = true;
                plane.clearRunway();
                return arriveEvent;

        }
        System.out.println("I'm writing null!!!!!");
        return null;
    }

    public int getId() {return m_id;}

}
