package com.williamquast;

import org.casaca.gpx4j.core.data.GpxDocument;
import org.casaca.gpx4j.core.data.Track;
import org.casaca.gpx4j.core.data.TrackSegment;
import org.casaca.gpx4j.core.data.Waypoint;
import org.casaca.gpx4j.core.driver.GpxDriver;
import org.casaca.gpx4j.core.driver.IGpxReader;
import org.casaca.gpx4j.core.util.SortedList;
import org.casaca.gpx4j.tools.GpxTools;
import org.casaca.gpx4j.tools.rangefinder.IRangefinder;

import java.awt.geom.Line2D;
import java.io.File;
import java.util.*;

public class Pathfinder {

    public static void main(String[] args) {

        try {
            GpxDriver driver = GpxDriver.getGpxDriver();
            driver.loadDefaultDriverProperties();

            GpxTools tools = GpxTools.getTools();
            tools.loadDefaultToolsProperties();

            IGpxReader reader = driver.createReader();

            GpxDocument doc1 = reader.readGpxDocument(new File("CBSP.gpx"));
            SortedList<Waypoint> input = doc1.getWaypoints();
            System.out.println("waypoints1=" + input.size());



//            GpxDocument doc2 = reader.readGpxDocument(new File("CBSP_2.gpx"));
//            SortedList<Waypoint> waypoints2 = doc1.getWaypoints();
//            System.out.println("waypoints2=" + waypoints1.size());

            IRangefinder rangefinder = tools.getRangefinder();

            List<Edge> edgesSort = new ArrayList<Edge>();

            for(int i=0; i < input.size(); i++) {
                for (int j=i+1; j < input.size(); j++) {
                    Waypoint p1 = input.get(i);
                    Waypoint p2 = input.get(j);

                    if (p1.getName().equals(p2.getName()) && locationEquals(p1, p2)) {
                        // same name same location, skip
                        continue;
                    }
                    double distance = rangefinder.getDistance(p1, p2).doubleValue();

                    Edge edge = new Edge(p1, p2, distance);
                    edgesSort.add(edge);
                }
            }




//            for (int i=0; i < waypoints1.size(); i++) {
//                for (int j=0; j < waypoints2.size(); j++) {
//                    Waypoint p1 = waypoints1.get(i);
//                    Waypoint p2 = waypoints2.get(j);
//
//
//                    if (p1.getName().equals(p2.getName())
//                            && locationEquals(p1, p2)) {
//                        // same name same location, skip
//                        continue;
//                    }
//                    double distance = rangefinder.getDistance(p1, p2).doubleValue();
//
//                    Edge edge = new Edge(p1, p2, distance);
//
//                    pairs.add(edge);
//
////                    String name1 = p1.getName();
////                    String name2 = p2.getName();
////                    if (name1.substring(0, Math.min(16, name1.length())).toLowerCase().equals(name2.substring(0, Math.min(16, name2.length())).toLowerCase())) {
////                        System.out.println("same name= " + p1.getName() + " = " + p2.getName() + "  distance=" + distance);
////                    }
////
////                    if (distance < 1.0) {
////                        System.out.println("same location= " + p1.getName() + " = " + p2.getName());
////                    }
//                }
//            }

            Collections.sort(edgesSort);

            LinkedHashSet<Edge> edges = new LinkedHashSet<Edge>();
            edges.addAll(edgesSort);

            List<Edge> usedEdges = new ArrayList<Edge>();

            LinkedHashSet<Waypoint> waypoints = new LinkedHashSet<Waypoint>();
            waypoints.addAll(input);


            Iterator<Edge> edgeItr = edges.iterator();

            Edge nextEdge = edgeItr.next();
            edgeItr.remove();
            usedEdges.add(nextEdge);

            Waypoint prev = nextEdge.p1;
            waypoints.remove(prev);
            System.out.println(prev.getLatitude() + "," + prev.getLongitude());

            Waypoint cur = nextEdge.p2;
            waypoints.remove(cur);
            System.out.println(cur.getLatitude() + "," + cur.getLongitude());

            pathfinding:
            while (!waypoints.isEmpty()) {
                int edgeCt = 0;
                int intersectCt = 0;
                boolean found = false;

                edgeItr = edges.iterator();
                edgeSearch:
                while (edgeItr.hasNext()) {
                    nextEdge = edgeItr.next();
                    edgeCt++;
                    for (Edge usedEdge : usedEdges) {
                        if (!nextEdge.p1.equals(usedEdge.p1)
                                && !nextEdge.p1.equals(usedEdge.p2)
                                && !nextEdge.p2.equals(usedEdge.p1)
                                && !nextEdge.p2.equals(usedEdge.p2)
                                && intersects(nextEdge.p1, nextEdge.p2, usedEdge.p1, usedEdge.p2)) {
                            intersectCt++;
                            continue edgeSearch;
                        }
                    }

                    if (cur.equals(nextEdge.p1) && waypoints.contains(nextEdge.p2)) {
                        found = true;
                        break edgeSearch;
                    }
                    if (cur.equals(nextEdge.p2) && waypoints.contains(nextEdge.p1)) {
                        Waypoint temp = nextEdge.p1;
                        nextEdge.p1 = nextEdge.p2;
                        nextEdge.p2 = temp;
                        found = true;
                        break edgeSearch;
                    }
                }
                //System.out.println("searched " + edgeCt + " of " + edges.size() + " where " + intersectCt + " intersect.");

                if (!found) {
                    System.out.println("no next vertex found");
                    break pathfinding;
                } else {
                    prev = nextEdge.p1;
                    cur = nextEdge.p2;
                    waypoints.remove(cur);

                    usedEdges.add(nextEdge);

                    edgeItr = edges.iterator();
                    while (edgeItr.hasNext()) {
                        Edge edge = edgeItr.next();
                        if (edge.p1.equals(prev) || edge.p2.equals(prev)) {
                            edgeItr.remove();
                        }
                    }

                    //System.out.println(prev.getName() + " -> " + cur.getName());
                    System.out.println(cur.getLatitude() + "," + cur.getLongitude());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean locationEquals(Waypoint p1, Waypoint p2) {
        return p1.getLatitude().doubleValue() == p2.getLatitude().doubleValue()
                && p1.getLongitude().doubleValue() == p2.getLongitude().doubleValue();
    }

    private static boolean intersects(Waypoint e1From, Waypoint e1To, Waypoint e2From, Waypoint e2To) {
        double e1FromLat = e1From.getLatitude().doubleValue();
        double e1FromLng = e1From.getLongitude().doubleValue();
        double e1ToLat = e1To.getLatitude().doubleValue();
        double e1ToLng = e1To.getLongitude().doubleValue();

        double e2FromLat = e2From.getLatitude().doubleValue();
        double e2FromLng = e2From.getLongitude().doubleValue();
        double e2ToLat = e2To.getLatitude().doubleValue();
        double e2ToLng = e2To.getLongitude().doubleValue();

        return Line2D.linesIntersect(e1FromLng, e1FromLat, e1ToLng, e1ToLat, e2FromLng, e2FromLat, e2ToLng, e2ToLat);
    }

    private static class Edge implements Comparable<Edge> {

        private Waypoint p1;
        private Waypoint p2;
        private double distance;

        public Edge(Waypoint p1, Waypoint p2, double distance) {
            this.p1 = p1;
            this.p2 = p2;
            this.distance = distance;
        }

        @Override
        public int compareTo(Edge o) {
            return Double.compare(distance, o.distance);
        }
    }
}
