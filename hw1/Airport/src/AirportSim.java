//YOUR NAME HERE

import java.util.ArrayList;
import java.util.Random;

public class AirportSim {
    public static Airport[] airportList;

    public static void main(String[] args) {

        airportList = new Airport[2];
//        ArrayList<Airplane> airplaneList = new ArrayList<Airplane>();

        Airport lax = new Airport("LAX", 5, 7, 5, 5, 2);
        airportList[0] = lax;
        Airport atl = new Airport("ATL", 3, 2, 1, 2, 2);
        airportList[1] = atl;

        //Airport fll = new Airport("FLL", 10,5, 80, 26, 2);
        //airportList.add(fll);
        //Airport dca = new Airport("DCA", 12,10, 77, 38, 2);
        //airportList.add(dca);
        //Airport dfw = new Airport("DFW", 10, 20, 97, 32, 2);
        //airportList.add(dfw);

//        int numberPlanes = 10;
////        double meanSpeed = 500, stdSpeed = 100;
//        int max = 2;
//        Random rand = new Random(1);
//        for (int i =1; i<= numberPlanes; i++){
////            int speed = (int)(rand.nextGaussian()*stdSpeed+meanSpeed);
//            int speed = 5;
//            int depart = rand.nextInt(max);
//            int destination = rand.nextInt(max);
//            while (depart == destination){
//                destination = rand.nextInt(max);
//            }
//            Airplane plane = new Airplane("plane"+i, airportList[depart], airportList[destination], 200, speed);
//
////            int delay = rand.nextInt(100);
//            int delay = 1;
//            AirportEvent arriveEvent = new AirportEvent(delay, plane.getDestination(),AirportEvent.PLANE_ARRIVES, plane);
//            Simulator.schedule(arriveEvent);
//        }

        int speed =5;
        Airplane plane = new Airplane("plane"+1, airportList[0], airportList[1], 200, speed);
        AirportEvent arriveEvent1 = new AirportEvent(0, plane.getDestination(),AirportEvent.PLANE_ARRIVES, plane);
        Simulator.schedule(arriveEvent1);

        Airplane plane2 = new Airplane("plane"+2, airportList[0], airportList[1], 200, speed);
        AirportEvent arriveEvent2 = new AirportEvent(1, plane2.getDestination(),AirportEvent.PLANE_ARRIVES, plane2);
        Simulator.schedule(arriveEvent2);

        Airplane plane3 = new Airplane("plane"+3, airportList[0], airportList[1], 200, speed);
        AirportEvent arriveEvent3 = new AirportEvent(2, plane3.getDestination(),AirportEvent.PLANE_ARRIVES, plane3);
        Simulator.schedule(arriveEvent3);

        Simulator.stopAt(100);
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
