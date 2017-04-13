//YOUR NAME HERE

public class AirportEvent extends Event {
    public static final int PLANE_ARRIVES = 0;
    public static final int PLANE_LANDED = 1;
    public static final int PLANE_DEPARTS = 2;

    private Airplane m_plane;

    AirportEvent(double delay, EventHandler handler, int eventType, Airplane plane) {
        super(delay, handler, eventType);
        m_plane = plane;
    }

    public void setPlane(Airplane plane) { m_plane = plane;}

    public Airplane getPlane() { return m_plane; }
}
