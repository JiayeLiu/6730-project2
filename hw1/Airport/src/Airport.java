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
    private int m_numRunways;

    // the location of airport
    final private double m_longitude;
    final private double m_latitude;

    private double m_runwayTimeToLand;
    private double m_requiredTimeOnGround;
    private double s_runwayTimeToLand;
    private double s_requiredTimeOnGround;

    private String m_airportName;

    private LinkedList<AirportEvent> m_waitToTakeOff = new LinkedList<AirportEvent>();   //runway attribute
    private LinkedList<AirportEvent> m_waitToLand = new LinkedList<AirportEvent>();     //runway attribute

    private Runways[] m_runwaylist;
    private int m_weather = -1;

    public Airport(String name, double runwayTimeToLand, double requiredTimeOnGround, double longitude, double latitude, int numRunways, int weather) {
        m_airportName = name;
        m_numPassArrive = 0;
        m_numPassDepart = 0;
        m_circlingTime = 0;
        s_runwayTimeToLand = runwayTimeToLand;
        s_requiredTimeOnGround = requiredTimeOnGround;
        m_longitude = longitude;
        m_latitude = latitude;
        m_numRunways = numRunways;
        m_runwaylist = new Runways[numRunways];
        setRunways(numRunways);
        setWeather(weather);

    }

    public void setRunways( int numRunways){
        for (int i=0; i<numRunways;i++){
            m_runwaylist[i] = new Runways(this,i);
        }
    }

    public static double distance(Airport depart, Airport destination) {
        double d1 = depart.getLongitude() - destination.getLongitude();
        double d2 = depart.getLatitude() - destination.getLatitude();
        double coef = 1.0;
//        double coef = 111.0;
        return Math.sqrt(Math.pow(d1, 2) + Math.pow(d2, 2)) * coef;
    }

    public void handle(Event event) {

        AirportEvent airEvent = (AirportEvent)event;
        Airplane plane = airEvent.getPlane();
        Runways runway;

        int debug;

        switch(airEvent.getType()) {
            case AirportEvent.PLANE_ARRIVES:

                plane.setArriveTime(Simulator.getCurrentTime());

                int i = 0;

                while(!m_waitToLand.isEmpty() && i<m_numRunways) {

                    // assign the empty runway to the plane in the first event in the landing queue
                    AirportEvent toLandEvent = m_waitToLand.getFirst();


                    runway = m_runwaylist[i];

                    if(runway.getFreeToLand() && runway.getFreeToTakeOff()) {
                        //
                        toLandEvent.getPlane().setRunway(runway);
                        Simulator.schedule(toLandEvent);

                        debug = (toLandEvent.getPlane().getRunway() == null)?-1:toLandEvent.getPlane().getRunway().getId();
                        System.out.println(toLandEvent.getPlane().getName() +" is on " + "lane" + debug);

                        m_waitToLand.removeFirst();
                        runway.setFreeToLand(false);
                    }

                    i++;
                }

                for (; i<m_numRunways;i++){
                    runway = m_runwaylist[i];

                    if(runway.getFreeToLand() && runway.getFreeToTakeOff()) {
                        // if the runway is empty, the airplane is able to land.
                        Simulator.schedule(runway.handle(airEvent));
                        break;
                    }

                }

                debug = (plane.getRunway() == null)?-1:plane.getRunway().getId();
                System.out.println(plane.getName() +" is on " + "lane" + debug);


                if (i==m_numRunways){
                    // landevent: note!!!! the plane has not been assigned runway due to all runways are full; add runway in the landed case
                    AirportEvent landedEvent = new AirportEvent(this.getRunwayTimeToLand(), this, AirportEvent.PLANE_LANDED, plane);
                    System.out.println(String.format("%.2f",Simulator.getCurrentTime()) + ": "+ plane.getName()+" arrived at airport " + this.getName());

                    m_waitToLand.add(landedEvent);
                }



                break;

            case AirportEvent.PLANE_LANDED:

                // randomly choose the destination airport

                runway = plane.getRunway();

                debug = (plane.getRunway() == null)?-1:plane.getRunway().getId();
                System.out.println(plane.getName() +" is on " + "lane" + debug);

                AirportEvent departureEvent = runway.handle(airEvent);


                if(!m_waitToLand.isEmpty()) {
                    AirportEvent toLandEvent = m_waitToLand.getFirst();
                    // assign the empty runway to the plane in the first event in the landing queue
                    toLandEvent.getPlane().setRunway(runway);
                    Simulator.schedule(toLandEvent);

                    m_waitToLand.removeFirst();
                    m_waitToTakeOff.add(departureEvent);
                }
                else {
                    runway.setFreeToLand(true);

                    if (!m_waitToTakeOff.isEmpty()){
                        // Let the first plane in the waiting list to take off
                        AirportEvent takeOffEvent = m_waitToTakeOff.getFirst();
                        // assign the empty runway to the plane in the first event in the takeoff queue
                        takeOffEvent.getPlane().setRunway(runway);
                        Simulator.schedule(takeOffEvent);
                        m_waitToTakeOff.removeFirst();

                        // put this plane event into waiting list
                        m_waitToTakeOff.add(departureEvent);
                    }
                    else {
                        plane.setRunway(runway);
                        Simulator.schedule(departureEvent);
                    }
                    runway.setFreeToTakeOff(false);
                }
                break;

            case AirportEvent.PLANE_DEPARTS:

                runway = plane.getRunway();
                debug = (plane.getRunway() == null)?-1:plane.getRunway().getId();
                System.out.println(plane.getName() +" is on " + "lane" + debug);

                AirportEvent arriveEvent = runway.handle(airEvent);
                Simulator.schedule(arriveEvent);

                if (!m_waitToLand.isEmpty()){
                    AirportEvent toLandEvent = m_waitToLand.getFirst();
                    // assign the empty runway to the plane in the first event in the landing queue
                    toLandEvent.getPlane().setRunway(runway);
                    Simulator.schedule(toLandEvent);

                    m_waitToLand.removeFirst();
                    runway.setFreeToLand(false);
                }
                else {
                    runway.setFreeToLand(true);
                    if(!m_waitToTakeOff.isEmpty()){
                        runway.setFreeToTakeOff(false);
                        AirportEvent takeOffEvent = m_waitToTakeOff.getFirst();

                        // assign the empty runway to the plane in the first event in the takeoff queue
                        takeOffEvent.getPlane().setRunway(runway);
                        Simulator.schedule(takeOffEvent);
                        m_waitToTakeOff.removeFirst();
                    }
                    else {
                        runway.setFreeToTakeOff(true);
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

    public void setNumPassArrive(int numPassArrive) { m_numPassArrive += numPassArrive; }

    public void setNumPassDepart(int numPassDepart) { m_numPassDepart += numPassDepart; }

    public double getCirclingTime() {return m_circlingTime; }

    public void setCirclingTime(double circlingTime) {m_circlingTime += circlingTime-m_runwayTimeToLand; }

    public double getRunwayTimeToLand() {return m_runwayTimeToLand;}

    public double getRequiredTimeOnGround() {return m_requiredTimeOnGround;}

    public int getWeather() {return m_weather;}

    public void setWeather(int weather) {
        if (m_weather == Weather.Typhoon){
            for (int i=0; i<m_numRunways; i++){
                m_runwaylist[i].setFreeToLand(true);
                m_runwaylist[i].setFreeToTakeOff(true);
            }
        }

        if (weather == Weather.Rainy){
            m_runwayTimeToLand = 2* s_runwayTimeToLand;
            m_requiredTimeOnGround = 2* s_requiredTimeOnGround;
        }
        else if (weather == Weather.Sunny){
            m_runwayTimeToLand = s_runwayTimeToLand;
            m_requiredTimeOnGround = s_requiredTimeOnGround;
        }
        else {
            for (int i=0; i<m_numRunways; i++){
                m_runwaylist[i].setFreeToLand(false);
                m_runwaylist[i].setFreeToTakeOff(false);

            }
            m_runwayTimeToLand = 2*s_runwayTimeToLand;
            m_requiredTimeOnGround = 2*s_requiredTimeOnGround;
        }
        m_weather = weather;
    }



}
