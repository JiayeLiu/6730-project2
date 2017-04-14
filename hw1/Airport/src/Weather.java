import java.util.Random;

/**
 * Created by ywu on 4/12/17.
 */
public class Weather {
    // weather type:
    public static final int Sunny = 0;
    public static final int Rainy = 1;
    public static final int Typhoon = 2;

    // season type:
    public static final int SPRING = 0;
    public static final int SUMMER = 1;
    public static final int AUTUMN = 2;
    public static final int WINTER = 3;

    // transition matrix (cumulative possibility):
    private static double[][] matrix;

    private static Random rand = new Random(0);

    // initialize the weather matrix based on the season
    public Weather(int season) {
        switch (season) {
            case SPRING:
                matrix = new double[][] {
                        {0.70, 0.95, 1.00},
                        {0.50, 0.95, 1.00},
                        {0.30, 0.90, 1.00}
                };
                break;

            case SUMMER:
                matrix = new double[][] {
                        {0.90, 0.99, 1.00},
                        {0.80, 0.95, 1.00},
                        {0.40, 0.90, 1.00}
                };
                break;

            case AUTUMN:
                matrix = new double[][] {
                        {0.95, 1.00, 1.00},
                        {0.90, 1.00, 1.00},
                        {1.00, 1.00, 1.00}
                };
                break;

            case WINTER:
                matrix = new double[][] {
                        {0.80, 1.00, 1.00},
                        {0.70, 1.00, 1.00},
                        {1.00, 1.00, 1.00}
                };
                break;
        }
    }

    // change the weather randomly
    public static int change(int weather) {
        double r = rand.nextDouble();
        for (int i = 0; i < 3; i++) {
            if (r <= matrix[weather][i]) {
                return i;
            }
        }
        return 2;
    }

    // print the weather for debugging purpose
    public static String printWeather(int weather){
        switch (weather) {
            case Sunny:
                return "Sunny";
            case Rainy:
                return "Rainy";
            case Typhoon:
                return "Typhoon";

        }
        return "Weather error";
    }
}
