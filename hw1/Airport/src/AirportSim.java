//YOUR NAME HERE

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class AirportSim {
    public static Airport[] airportList;

    public static void main(String[] args) {
//        String csvFile = "airport.csv";
//        String line = "";
//        String csvsplit = ",";
//        String[] airport_name = new String[100];
//        try(BufferedReader br = new BufferedReader(new FileReader(csvFile)))
//        {
//            int i = 0;
//            while ((line = br.readLine())!= null)
//            {
//                String[] temp = line.split(csvsplit);
//                airport_name[i] = temp[0];
//                i++;
//
//
//            }
//
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//        }


        airportList = new Airport[4];
//        ArrayList<Airplane> airplaneList = new ArrayList<Airplane>();

        Airport lax = new Airport("LAX", 5, 7, 5, 5, 4, Weather.Typhoon);
        airportList[0] = lax;
        Airport atl = new Airport("ATL", 3, 2, 1, 2, 4, Weather.Typhoon);
        airportList[1] = atl;
        Airport abc = new Airport("ABC", 5, 10, 3, 8, 5, Weather.Typhoon);
        airportList[2] = abc;



        int numberPlanes = 60;
        double meanSpeed = 500, stdSpeed = 100;
        int max = 3;
        Random rand = new Random(1);
        for (int i =1; i<= numberPlanes; i++){
            int speed = (int)(rand.nextGaussian()*stdSpeed+meanSpeed);
            int depart = rand.nextInt(max);
            int destination = rand.nextInt(max);
            while (depart == destination){
                destination = rand.nextInt(max);
            }
            Airplane plane = new Airplane("plane"+i, airportList[depart], airportList[destination], 200, speed);

            int delay = rand.nextInt(100);
            AirportEvent arriveEvent = new AirportEvent(delay, plane.getDestination(),AirportEvent.PLANE_ARRIVES, plane);
            Simulator.schedule(arriveEvent);
        }


        Simulator.stopAt(100);
        Simulator.run();


        for (Airport airport : airportList){
            System.out.println("-------------");
            System.out.println(airport.getName());
            System.out.println("number of passengers arriving:"+ airport.getNumPassArrive());
            System.out.println("number of passengers departing:"+ airport.getNumPassDepart());
            System.out.println("Total circling time:" + airport.getCirclingTime());
        }


    }
}
