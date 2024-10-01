package net.shoreline.client.impl.manager.world;

import io.netty.util.internal.ConcurrentSet;
import net.shoreline.client.api.waypoint.Waypoint;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages waypoints for the client.
 *
 * @author linus
 * @since 1.0
 */
public class WaypointManager {
    // Using ConcurrentHashMap's newKeySet for thread-safe set operations
    private final Set<Waypoint> waypoints = ConcurrentHashMap.newKeySet();

    /**
     * Registers a single waypoint.
     *
     * @param waypoint the waypoint to register
     */
    public void register(Waypoint waypoint) {
        waypoints.add(waypoint);
    }

    /**
     * Registers multiple waypoints.
     *
     * @param waypoints the waypoints to register
     */
    public void register(Waypoint... waypoints) {
        Collections.addAll(this.waypoints, waypoints);
    }

    /**
     * Removes a specific waypoint.
     *
     * @param waypoint the waypoint to remove
     * @return true if the waypoint was successfully removed, false otherwise
     */
    public boolean remove(Waypoint waypoint) {
        return waypoints.remove(waypoint);
    }

    /**
     * Removes a waypoint by its name.
     *
     * @param waypointName the name of the waypoint to remove
     * @return true if the waypoint was found and removed, false otherwise
     */
    public boolean remove(String waypointName) {
        for (Waypoint w : waypoints) {
            if (w.getName().equalsIgnoreCase(waypointName)) {
                return waypoints.remove(w);
            }
        }
        return false;
    }

    /**
     * Gets all registered waypoints.
     *
     * @return an unmodifiable collection of waypoints
     */
    public Collection<Waypoint> getWaypoints() {
        return Collections.unmodifiableSet(waypoints);
    }

    /**
     * Gets all server IPs associated with the waypoints.
     *
     * @return a collection of server IPs
     */
    public Collection<String> getIps() {
        final Set<String> ips = new HashSet<>();
        for (Waypoint waypoint : waypoints) {
            ips.add(waypoint.getIp());
        }
        return ips;
    }
}
