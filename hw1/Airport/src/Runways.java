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

    public AirportEvent handle(Event event) {
        /*System.out.println("--------------");
        for (AirportEvent e : m_waitToLand) {
            System.out.println(e.getPlane().getName()+" waits to land at airport "+this.getName());
        }
        for (AirportEvent e : m_waitToTakeOff) {
            System.out.println(e.getPlane().getName()+" waits to depart from airport " + this.getName());
        }
        */

        AirportEvent airEvent = (AirportEvent)event;
        Airplane plane = airEvent.getPlane();

        switch(airEvent.getType()) {
            case AirportEvent.PLANE_ARRIVES:

                plane.setRunway(this);
                AirportEvent landedEvent = new AirportEvent(m_airport.getRunwayTimeToLand(), m_airport, AirportEvent.PLANE_LANDED, plane);
                System.out.println(String.format("%.2f",Simulator.getCurrentTime()) + ": "+ plane.getName()+" arrived at airport " + m_airport.getName());

                m_freeToLand = false;
                return landedEvent;


            case AirportEvent.PLANE_LANDED:

                // randomly choose the destination airport

                //ArrayList<Airport> airportList = airportList(); global variable

                Airport[] airportList = AirportSim.airportList;
                int max = airportList.length;

                Random rand = new Random();
                int destination = rand.nextInt(max);
                while (m_airport == airportList[destination]){
                    destination = rand.nextInt(max);
                }
                plane.setDestination(airportList[destination]);
                plane.setDepart(m_airport);

                // stats on the number of passengers arriving
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
