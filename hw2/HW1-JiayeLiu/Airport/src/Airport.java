//YOUR NAME HERE

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.TreeSet;

public class Airport implements EventHandler {

    //TODO add landing and takeoff queues, random variables

    private int m_numPassArrive;
    private int m_numPassDepart;
    private int m_circlingTime;

    // the location of airport
    final private double m_longitude;
    final private double m_latitude;

    private boolean m_freeToLand;
    private boolean m_freeToTakeOff;

    private double m_flightTime;
    private double m_runwayTimeToLand;
    private double m_requiredTimeOnGround;

    private String m_airportName;

    private LinkedList<AirportEvent> m_waitToTakeOff = new LinkedList<AirportEvent>();
    private LinkedList<AirportEvent> m_waitToLand = new LinkedList<AirportEvent>();

    public Airport(String name, double runwayTimeToLand, double requiredTimeOnGround, double longitude, double latitude) {
        m_airportName = name;
        m_numPassArrive = 0;
        m_numPassDepart = 0;
        m_circlingTime = 0;
        m_freeToLand = true;
        m_freeToTakeOff = true;
        m_runwayTimeToLand = runwayTimeToLand;
        m_requiredTimeOnGround = requiredTimeOnGround;
        m_longitude = longitude;
        m_latitude = latitude;
    }

    public static double distance(Airport depart, Airport destination) {
        double d1 = depart.getLongitude() - destination.getLatitude();
        double d2 = depart.getLatitude() - destination.getLatitude();
        return Math.sqrt(Math.pow(d1, 2) + Math.pow(d2, 2)) * 111;
    }

    public void handle(Event event) {
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

                plane.setArriveTime(Simulator.getCurrentTime());

                AirportEvent landedEvent = new AirportEvent(m_runwayTimeToLand, this, AirportEvent.PLANE_LANDED, plane);
                System.out.println(String.format("%.2f",Simulator.getCurrentTime()) + ": "+ plane.getName()+" arrived at airport " + this.getName());

                if(m_freeToLand && m_freeToTakeOff) {
                    // if the runway is empty, the airplane is able to land.
                    m_freeToLand = false;
                    Simulator.schedule(landedEvent);
                }
                else {
                    // if else, put the airplane into wait to land list
                    m_waitToLand.add(landedEvent);
                }
                break;

            case AirportEvent.PLANE_LANDED:

                // randomly choose the destination airport
                int min = 0, max = 5;
                ArrayList<Airport> airportList = plane.getAirportList();

                Random rand = new Random();
                int destination = rand.nextInt(max);
                while (this == airportList.get(destination)){
                    destination = rand.nextInt(max);
                }
                plane.setDestination(airportList.get(destination));
                plane.setDepart(this);

                // stats on the number of passengers arriving
                m_numPassArrive += plane.getNumPassengers();

                // calculate circling time
                if (plane.getArriveTime()!= -1) {
                    m_circlingTime += Simulator.getCurrentTime() - plane.getArriveTime() - m_runwayTimeToLand;
                }
                else {
                    plane.setArriveTime(0);
                }

                System.out.println(String.format("%.2f",Simulator.getCurrentTime()) + ":" + plane.getName()+ " lands at airport " + this.getName());
                AirportEvent departureEvent = new AirportEvent(m_requiredTimeOnGround, this, AirportEvent.PLANE_DEPARTS, plane);

                if(!m_waitToLand.isEmpty()) {
                    AirportEvent toLandEvent = m_waitToLand.getFirst();
                    Simulator.schedule(toLandEvent);
                    m_waitToLand.removeFirst();
                    m_waitToTakeOff.add(departureEvent);
                }
                else {
                    m_freeToLand = true;

                    if (!m_waitToTakeOff.isEmpty()){
                        // Let the first plane in the waiting list to take off
                        AirportEvent takeOffEvent = m_waitToTakeOff.getFirst();
                        Simulator.schedule(takeOffEvent);
                        m_waitToTakeOff.removeFirst();

                        // put this plane event into waiting list
                        m_waitToTakeOff.add(departureEvent);
                    }
                    else {
                        Simulator.schedule(departureEvent);
                    }
                    m_freeToTakeOff = false;
                }
                break;

            case AirportEvent.PLANE_DEPARTS:
                // stats on the number of passengers departing
                m_numPassDepart += plane.getNumPassengers(); //

                // compute the flight time of next flight
                m_flightTime = plane.getDistance()/plane.getSpeed();

                System.out.println(String.format("%.2f",Simulator.getCurrentTime()) + ":" + plane.getName()+ " departs from airport " + this.getName());
                AirportEvent arriveEvent = new AirportEvent(m_flightTime, plane.getDestination(), AirportEvent.PLANE_ARRIVES, plane);
                Simulator.schedule(arriveEvent);
                m_freeToTakeOff = true;

                if (!m_waitToLand.isEmpty()){
                    AirportEvent toLandEvent = m_waitToLand.getFirst();
                    Simulator.schedule(toLandEvent);
                    m_waitToLand.removeFirst();
                    m_freeToLand = false;
                }
                else {
                    m_freeToLand = true;
                    if(!m_waitToTakeOff.isEmpty()){
                        m_freeToTakeOff = false;
                        AirportEvent toDepartEvent = m_waitToTakeOff.getFirst();
                        Simulator.schedule(toDepartEvent);
                        m_waitToTakeOff.removeFirst();
                    }
                    else {
                        m_freeToTakeOff = true;
                    }
                }
                break;
        }
    }

    public String getName() {
        return m_airportName;
    }

    public double getLongitude() { return m_longitude; }

    public double getLatitude() { return m_latitude; }

    public int getNumPassArrive() { return m_numPassArrive; }

    public int getNumPassDepart() { return m_numPassDepart; }

    public void setNumPassArrive(int numPassArrive) { m_numPassArrive = numPassArrive; }

    public void setNumPassDepart(int numPassDepart) { m_numPassDepart = numPassDepart; }

    public double getCirclingTime() {return m_circlingTime; }

}
