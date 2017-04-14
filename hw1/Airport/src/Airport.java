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

    // real time based on weather
    private double m_runwayTimeToLand;
    private double m_requiredTimeOnGround;

    // standard time
    private double s_runwayTimeToLand;
    private double s_requiredTimeOnGround;

    private String m_airportName;

    // Queues for takeoff and landing
    private LinkedList<AirportEvent> m_waitToTakeOff = new LinkedList<AirportEvent>();
    private LinkedList<AirportEvent> m_waitToLand = new LinkedList<AirportEvent>();

    // runway list
    private Runways[] m_runwaylist;

    // weather initialization to -1 (bad weather)
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
        // initialize runway list
        m_numRunways = numRunways;
        m_runwaylist = new Runways[numRunways];
        // build runways to runway list
        setRunways(numRunways);
        // set weather for the airport
        setWeather(weather);

    }


    // build empty runways for the runway list
    public void setRunways( int numRunways){
        for (int i=0; i<numRunways;i++){
            m_runwaylist[i] = new Runways(this,i);
        }
    }

    // compute distance between two airports on the globe
    public static double distance(Airport depart, Airport destination) {
        double d1 = depart.getLongitude() - destination.getLongitude();
        double d2 = depart.getLatitude() - destination.getLatitude();
        double coef = 111.0; // 111.0 km/deg
        return Math.sqrt(Math.pow(d1, 2) + Math.pow(d2, 2)) * coef;
    }

    // deal with airevent
    public void handle(Event event) {
        // cast the event into airport event
        AirportEvent airEvent = (AirportEvent)event;

        // obtain the plane object
        Airplane plane = airEvent.getPlane();

        // declare a temporary runway for many uses
        Runways runway;


        switch(airEvent.getType()) {
            // arrive -> land
            case AirportEvent.PLANE_ARRIVES:
                // record the arrival time to compute cycling time
                plane.setArriveTime(Simulator.getCurrentTime());

                // i: runway id starting from the first one
                int i = 0;

                // whenever there is an empty runway, let the waiting airplane to land
                while(!m_waitToLand.isEmpty() && i<m_numRunways) {
                    // check the i th runway
                    runway = m_runwaylist[i];

                    if(runway.getFreeToLand() && runway.getFreeToTakeOff()) {

                        // assign the empty runway to the plane in the first event in the landing queue
                        AirportEvent toLandEvent = m_waitToLand.getFirst();

                        // set the runway to land for the plane
                        toLandEvent.getPlane().setRunway(runway);

                        // schedule the landing event
                        Simulator.schedule(toLandEvent);

                        // remove the event from the queue
                        m_waitToLand.removeFirst();

                        // occupy the runway
                        runway.setFreeToLand(false);
                    }

                    // increment i to check the next runway
                    i++;
                }

                // starting from the last possible available runway
                for (; i<m_numRunways;i++){
                    // check the i th runway
                    runway = m_runwaylist[i];

                    if(runway.getFreeToLand() && runway.getFreeToTakeOff()) {
                        // if the runway is empty, the airplane is able to land.
                        // let the runway to handle arriving event
                        Simulator.schedule(runway.handle(airEvent));
                        break;
                    }

                }

                // if all runways are occupied, then add this event to landing queue
                if (i == m_numRunways){
                    // landing event: note!!!! the plane has not been assigned runway due to all runways are full; add runway in the landed case
                    // create the landing event
                    AirportEvent landedEvent = new AirportEvent(this.getRunwayTimeToLand(), this, AirportEvent.PLANE_LANDED, plane);
                    System.out.println(String.format("%.2f",Simulator.getCurrentTime()) + ": "+ plane.getName()+" arrived at airport " + this.getName());

                    // add the landing event to the queue
                    m_waitToLand.add(landedEvent);
                }

                // case break!
                break;

            case AirportEvent.PLANE_LANDED:

                // get the runway the plane has used
                runway = plane.getRunway();

                // let the runway to handle the landing event
                AirportEvent departureEvent = runway.handle(airEvent);

                // if there are planes waiting
                if(!m_waitToLand.isEmpty()) {
                    // peek the first event in the landing queue in the sky
                    AirportEvent toLandEvent = m_waitToLand.removeFirst();

                    // assign the empty runway to the plane in the first event in the landing queue
                    toLandEvent.getPlane().setRunway(runway);

                    // schedule the landing event and add it to departing queue
                    Simulator.schedule(toLandEvent);
                    m_waitToTakeOff.add(departureEvent);
                }

                // if there are no planes in the sky
                else {
                    // set the runway to free
                    runway.setFreeToLand(true);

                    // if planes are waiting to depart
                    if (!m_waitToTakeOff.isEmpty()){

                        // Let the first plane in the waiting list to take off
                        AirportEvent takeOffEvent = m_waitToTakeOff.removeFirst();

                        // assign the empty runway to the plane in the first event in the takeoff queue
                        takeOffEvent.getPlane().setRunway(runway);

                        // schedule the departing event and put this plane event into waiting list
                        Simulator.schedule(takeOffEvent);
                        m_waitToTakeOff.add(departureEvent);
                    }
                    // if no planes are waiting to depart
                    else {
                        // use the runway to go and schedule the departing event
                        plane.setRunway(runway);
                        Simulator.schedule(departureEvent);
                    }
                    runway.setFreeToTakeOff(false);
                }
                // break case!
                break;

            case AirportEvent.PLANE_DEPARTS:

                // obtain the runway to takeoff
                runway = plane.getRunway();

                // let the runway to handle the departing event
                AirportEvent arriveEvent = runway.handle(airEvent);
                // schedule the arriving event in the destination
                Simulator.schedule(arriveEvent);

                // if planes are waiting in the sky
                if (!m_waitToLand.isEmpty()){
                    // get the first landing event in the landing queue
                    AirportEvent toLandEvent = m_waitToLand.removeFirst();

                    // assign the empty runway to the plane in the first event in the landing queue
                    toLandEvent.getPlane().setRunway(runway);

                    // schedule the landing event
                    Simulator.schedule(toLandEvent);

                    // occupy the runway to land
                    runway.setFreeToLand(false);
                }

                // if there are no planes are waiting in the sky
                else {
                    // the runway is free to land
                    runway.setFreeToLand(true);

                    // check if there are planes waiting to takeoff
                    if(!m_waitToTakeOff.isEmpty()){

                        // occupy the runway to takeoff
                        runway.setFreeToTakeOff(false);

                        // get the first takeoff event
                        AirportEvent takeOffEvent = m_waitToTakeOff.removeFirst();

                        // assign the empty runway to the plane in the first event in the takeoff queue
                        takeOffEvent.getPlane().setRunway(runway);

                        // schedule the takeoff event
                        Simulator.schedule(takeOffEvent);
                    }
                    else {
                        // free the runway and allow takeoff
                        runway.setFreeToTakeOff(true);
                    }
                }
                // break case!
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

    // set the weather for the airport
    public void setWeather(int weather) {
        // if previous weather is typhoon
        // then free all the runways
        if (m_weather == Weather.Typhoon){
            for (int i=0; i<m_numRunways; i++){
                m_runwaylist[i].setFreeToLand(true);
                m_runwaylist[i].setFreeToTakeOff(true);
            }
        }

        // if rainy : double the standard time
        if (weather == Weather.Rainy){
            m_runwayTimeToLand = 2* s_runwayTimeToLand;
            m_requiredTimeOnGround = 2* s_requiredTimeOnGround;
        }
        // if sunny : keep the standard time
        else if (weather == Weather.Sunny){
            m_runwayTimeToLand = s_runwayTimeToLand;
            m_requiredTimeOnGround = s_requiredTimeOnGround;
        }
        // if typhoon : close all the runways
        // double the standard time to simulate the time when storm -> sunny
        else {
            for (int i=0; i<m_numRunways; i++){
                m_runwaylist[i].setFreeToLand(false);
                m_runwaylist[i].setFreeToTakeOff(false);

            }
            m_runwayTimeToLand = 2*s_runwayTimeToLand;
            m_requiredTimeOnGround = 2*s_requiredTimeOnGround;
        }

        // reset the weather
        m_weather = weather;
    }



}
