//YOUR NAME HERE

import java.util.ArrayList;
import java.util.Random;

public class AirportSim {
    public static void main(String[] args) {

        ArrayList<Airport> airportList = new ArrayList<Airport>();
        ArrayList<Airplane> airplaneList = new ArrayList<Airplane>();

        Airport lax = new Airport("LAX", 10, 15, 118, 34);
        airportList.add(lax);
        Airport atl = new Airport("ATL", 15, 20, 84, 34);
        airportList.add(atl);
        Airport fll = new Airport("FLL", 10,5, 80, 26);
        airportList.add(fll);
        Airport dca = new Airport("DCA", 12,10, 77, 38 );
        airportList.add(dca);
        Airport dfw = new Airport("DFW", 10, 20, 97, 32);
        airportList.add(dfw);

        int numberPlanes = 20;
        double meanSpeed = 500, stdSpeed = 100;
        int min = 0, max = 5;
        Random rand = new Random();
        for (int i =1; i<= numberPlanes; i++){
            int speed = (int)(rand.nextGaussian()*stdSpeed+meanSpeed);
            int depart = rand.nextInt(max);
            int destination = rand.nextInt(max);
            while (depart == destination){
                destination = rand.nextInt(max);
            }
            Airplane plane = new Airplane("plane"+i, airportList.get(depart), airportList.get(destination), 200, speed, airportList);

            int delay = rand.nextInt(100);
            AirportEvent landingEvent = new AirportEvent(delay, plane.getDestination(),AirportEvent.PLANE_ARRIVES, plane);
            Simulator.schedule(landingEvent);
        }

        Simulator.stopAt(1000);
        Simulator.run();

        /*
        for (Airport airport : airportList){
            System.out.println("-------------");
            System.out.println(airport.getName());
            System.out.println("number of passengers arriving:"+ airport.getNumPassArrive());
            System.out.println("number of passengers departing:"+ airport.getNumPassDepart());
            System.out.println("Total circling time:" + airport.getCirclingTime());
        }
        */

    }
}
