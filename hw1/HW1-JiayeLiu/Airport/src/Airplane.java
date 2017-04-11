//YOUR NAME HERE

//TODO add number of passengers, speed

import java.util.ArrayList;
import java.util.Random;

public class Airplane {
    private String m_name;

    // the depart and destination airport of airplane
    private Airport m_depart;
    private Airport m_destination;

    // airplane information
    private int m_numberPassengers;
    private int m_numPassMax;
    private int m_speed;
    private double m_arriveTime;
    private double m_distance;
    private ArrayList<Airport> m_airportList;

    public Airplane(String name, Airport depart, Airport destination, int numPassMax, int speed, ArrayList airportList) {
        m_name = name;
        m_depart = depart;
        m_destination = destination;
        m_numPassMax = numPassMax;
        randomNumPassengers();
        m_speed = speed;
        m_arriveTime = -1;
        m_distance = Airport.distance(depart, destination);
        m_airportList = airportList;
    }

    public void randomNumPassengers(){
        Random rand = new Random();
        double mean = 0.75, std = 0.1;
        m_numberPassengers = (int)((rand.nextGaussian()*std+mean)*m_numPassMax);
    }


    public String getName() { return m_name; }

    public void setName(String name) { m_name = name; }

    public Airport getDepart() { return m_depart; }

    public void setDepart(Airport depart) { m_depart = depart; }

    public Airport getDestination() { return m_destination; }

    public void setDestination(Airport destination) { m_destination = destination; }

    public int getNumPassengers() { return m_numberPassengers; }

    public void setNumPassengers(int numPassengers) { m_numberPassengers = numPassengers; }

    public int getSpeed() { return m_speed; }

    public double getArriveTime() { return m_arriveTime; }

    public void setArriveTime(double arriveTime) { m_arriveTime = arriveTime; }

    public double getDistance() { return m_distance; }

    public void setDistance(double distance) { m_distance = distance; }

    public ArrayList<Airport> getAirportList() {return m_airportList; }


}
