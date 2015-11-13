package com.williamquast;

import com.csvreader.CsvWriter;
import com.vividsolutions.jts.geom.Coordinate;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.casaca.gpx4j.core.data.*;
import org.casaca.gpx4j.core.driver.GpxDriver;
import org.casaca.gpx4j.core.driver.IGpxReader;
import org.casaca.gpx4j.core.driver.IGpxWriter;
import org.casaca.gpx4j.core.util.SortedList;
import org.casaca.gpx4j.tools.GpxTools;
import org.casaca.gpx4j.tools.exporter.csv.CSVExporter;
import org.casaca.gpx4j.tools.rangefinder.IRangefinder;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeocentricCRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.referencing.datum.DefaultGeodeticDatum;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.datum.GeodeticDatum;
import org.opengis.referencing.operation.MathTransform;

import java.awt.geom.Line2D;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by wquast on 12/22/14.
 */
public class GpxDiff {

    CoordinateReferenceSystem wgs84Zone14n;
    CoordinateReferenceSystem nad27Zone14n;

    MathTransform wgs84Zone14nToDefault;
    MathTransform nad27Zone14nToDefault;

    MathTransform defaultToWgs84Zone14n;

    public static void main(String[] args) {
        try {

            GpxDiff prog = new GpxDiff();
            prog.init();

            //List<Record> records = prog.readFromCsv("CBSP_data.csv");
            //prog.writeToCsv("output.csv", records);
            List<Record> records = prog.readFromGpx("CBSP.gpx");
            prog.writeToCsv("output_gpx.csv", records);
            //prog.writeToGpx("output.gpx", records);
            //prog.printDiff();

        } catch (Exception e) {
            System.err.println("### Error ###");
            System.err.println(ExceptionUtils.getFullStackTrace(e));
        }
    }

    public void init() throws Exception {
        wgs84Zone14n = CRS.decode("EPSG:32614");
        nad27Zone14n = CRS.decode("EPSG:26714");

        wgs84Zone14nToDefault = CRS.findMathTransform(wgs84Zone14n, DefaultGeographicCRS.WGS84);
        nad27Zone14nToDefault = CRS.findMathTransform(nad27Zone14n, DefaultGeographicCRS.WGS84);

        defaultToWgs84Zone14n = CRS.findMathTransform(DefaultGeographicCRS.WGS84, wgs84Zone14n);
    }

    public void printDiff() throws Exception {

        List<Record> records1 = readFromCsv("CBSP_data.csv");
        List<Record> records2 = readFromGpx("CBSP.gpx");

        System.out.println("csv size=" + records1.size());
        System.out.println("gpx size=" + records2.size());

        GpxTools tools = GpxTools.getTools();
        tools.loadDefaultToolsProperties();

        HashMap<String, Record> rec1Ids = new HashMap<String, Record>();
        HashMap<String, Record> rec2Ids = new HashMap<String, Record>();

        for (int i=0; i < records1.size(); i++) {
            rec1Ids.put(records1.get(i).id, records1.get(i));
        }

        for (int i=0; i < records2.size(); i++) {
            rec2Ids.put(records2.get(i).id, records2.get(i));
        }

        for (int i=0; i < records1.size(); i++) {
            for (int j = 0; j < records2.size(); j++) {

                Record rec1 = records1.get(i);
                Record rec2 = records2.get(j);

                if (rec1.id.equalsIgnoreCase(rec2.id)) {
                    rec1Ids.remove(rec1.id);
                    rec2Ids.remove(rec2.id);

                    if (rec1.coordinate == null && rec2.coordinate == null) {
                        System.out.println(rec1.id + " CSV and GPX no coords.");
                    } else if (rec1.coordinate == null) {
                        Coordinate outCoord = new Coordinate();
                        JTS.transform(rec2.coordinate, outCoord, defaultToWgs84Zone14n);
                        System.out.println(rec1.id + " CSV missing coord. GPX= " + String.format("%.2f", outCoord.x) + " " + String.format("%.2f", outCoord.y) + " WGS84 Zone 14N");
                    } else if (rec2.coordinate == null) {
                        Coordinate outCoord = new Coordinate();
                        JTS.transform(rec1.coordinate, outCoord, defaultToWgs84Zone14n);
                        System.out.println(rec1.id + " GPX missing coord. CVS= " + String.format("%.2f", outCoord.x) + " " + String.format("%.2f", outCoord.y) + " WGS84 Zone 14N");
                    } else {
                        Coordinate hCoord1 = new Coordinate(rec1.coordinate.x, rec1.coordinate.y);
                        Coordinate hCoord2 = new Coordinate(rec2.coordinate.x, rec1.coordinate.y);
                        double hDist = JTS.orthodromicDistance(hCoord1, hCoord2, DefaultGeographicCRS.WGS84);

                        Coordinate vCoord1 = new Coordinate(rec1.coordinate.x, rec1.coordinate.y);
                        Coordinate vCoord2 = new Coordinate(rec1.coordinate.x, rec2.coordinate.y);
                        double vDist = JTS.orthodromicDistance(vCoord1, vCoord2, DefaultGeographicCRS.WGS84);

                        double distance = JTS.orthodromicDistance(rec1.coordinate, rec2.coordinate, DefaultGeographicCRS.WGS84);

                        System.out.println(rec1.id + " Distance diff=" + String.format("%.2f", distance) + " h=" +String.format("%.2f", hDist) + " v=" + String.format("%.2f", vDist));
                    }

                    if (!rec1.name.equals(rec2.name)) {
                        System.out.println(rec1.id + " Different name. CSV=" + rec1.name + " GPX=" +rec2.name);
                    }
                } else if (rec1.coordinate != null && rec2.coordinate != null && JTS.orthodromicDistance(rec1.coordinate, rec2.coordinate, DefaultGeographicCRS.WGS84) < 1.0) {
                    rec1Ids.remove(rec1.id);
                    rec2Ids.remove(rec2.id);

                    System.out.println(rec1.id + " " + rec2.id + " Same coords. ");
                } else if (rec1.id.startsWith(rec2.id) || rec2.id.startsWith(rec1.id)) {
                    rec1Ids.remove(rec1.id);
                    rec2Ids.remove(rec2.id);

                    System.out.println(rec1.id + " " + rec2.id + " Same base ID. '" + rec1.id + "' '" + rec2.id + "'");
                } else if (rec1.name.equalsIgnoreCase(rec2.name)) {
                    rec1Ids.remove(rec1.id);
                    rec2Ids.remove(rec2.id);

                    System.out.println(rec1.id + " " + rec2.id + " Same name. '" + rec1.name + "' '" + rec2.name + "'");
                }
            }
        }

        for (Map.Entry<String, Record> missRec1 : rec1Ids.entrySet()) {
            System.out.println(missRec1.getKey() + " is in CSV, but missing in GPX. " + toStringForCsv(missRec1.getValue()));
        }

        for (Map.Entry<String, Record> missRec2 : rec2Ids.entrySet()) {
            System.out.println(missRec2.getKey() + " is in GPX, but missing in CSV. " + toStringForCsv(missRec2.getValue()));
        }
    }

    private String toStringForCsv(Record record) throws Exception {
        StringBuilder sb = new StringBuilder();

        sb.append("id='" + record.id + '\'');
        sb.append(", name='" + record.name + '\'');
        if (record.coordinate != null) {
            Coordinate outCoord = new Coordinate();
            JTS.transform(record.coordinate, outCoord, defaultToWgs84Zone14n);
            sb.append(", coordinate=" + String.format("%.2f", outCoord.x) + " " + String.format("%.2f", outCoord.y) + " WGS14 Zone 14");
            sb.append(", elevation=" + (!Double.isNaN(outCoord.z) ? String.format("%.2f", outCoord.z) : "null"));
        } else {
            sb.append(", coordinate=null");
        }

        sb.append(", county=" + record.county);
        sb.append(", length=" + record.length);
        sb.append(", depth=" + record.depth);
        sb.append(", survey=" + record.survey);
        sb.append(", map=" + record.map);

        return sb.toString();
    }

    final String[] HEADER = {"ID", "Name", "County", "East", "North", "Zone", "Datum", "Elev", "Length", "Depth", "Survey", "Map", "Tag", "Photo", "Notes"};

    private List<Record> readFromCsv(String fileName) throws Exception {
        CSVParser csvParser = new CSVParser(new FileReader(fileName), CSVFormat.DEFAULT.withHeader());

        List<Record> answer = new ArrayList<Record>();

        Iterator<CSVRecord> itr = csvParser.iterator();
        while (itr.hasNext()) {
            CSVRecord rec = itr.next();

            String id = rec.get("ID");
            String name = rec.get("Name");
            String county = !rec.get("County").isEmpty() ? rec.get("County") : null;
            Double length = !rec.get("Length").isEmpty() ? Double.parseDouble(rec.get("Length")) : null;
            Double depth = !rec.get("Depth").isEmpty() ? Double.parseDouble(rec.get("Depth")) : null;
            Double elevation = !rec.get("Elev").isEmpty() ? Double.parseDouble(rec.get("Elev")) * 0.3048 : null;
            Boolean survey = fromBooleanValue(rec.get("Survey"));
            Boolean map = fromBooleanValue(rec.get("Map"));
            Boolean tag = fromBooleanValue(rec.get("Tag"));
            Boolean photo = fromBooleanValue(rec.get("Photo"));
            String notes = !rec.get("Notes").isEmpty() ? rec.get("Notes") : null;

            Double east = !rec.get("East").isEmpty() ? Double.parseDouble(rec.get("East")) : null;
            Double north = !rec.get("North").isEmpty() ? Double.parseDouble(rec.get("North")) : null;
            Integer zone = !rec.get("Zone").isEmpty() ? Integer.parseInt(rec.get("Zone")) : null;
            String datum = rec.get("Datum");

            Coordinate coordinate = null;
            if (!datum.isEmpty()) {
                if (datum.equals("NAD27") && zone == 14) {
                    Coordinate input = new Coordinate(east, north);
                    coordinate = new Coordinate();
                    JTS.transform(input, coordinate, nad27Zone14nToDefault);
                    //System.out.println("nad27= " + input.x + "," + input.y + " -> " + coordinate.x + "," + coordinate.y);
                } else if (datum.equals("WGS84") && zone == 14) {
                    Coordinate input = new Coordinate(east, north);
                    coordinate = new Coordinate();
                    JTS.transform(input, coordinate, wgs84Zone14nToDefault);
                    //System.out.println("wgs84= " + input.x + "," + input.y + " -> " + coordinate.x + "," + coordinate.y);
                } else {
                    throw new RuntimeException("Unhandled datum " + datum);
                }

                if (elevation != null) {
                    coordinate.z = elevation;
                }
            }

            Record record = new Record(id, name, county, coordinate, length, depth, survey, map, tag, photo, notes);
            answer.add(record);
        }

        return answer;
    }

    private Boolean fromBooleanValue(String value) {
        Boolean answer = null;
        if (value == null || value.isEmpty()) {
            answer = null;
        } else if (value.startsWith("y")) {
            answer = Boolean.TRUE;
        } else if (value.startsWith("n")) {
            answer = Boolean.FALSE;
        } else {
            throw new RuntimeException("unknown boolean. " + value);
        }
        return answer;
    }

    private void writeToCsv(String fileName, List<Record> records) throws Exception{
        CSVPrinter printer = new CSVPrinter(new FileWriter(fileName), CSVFormat.DEFAULT.withHeader(HEADER));

        for (Record record : records) {
            String east = null;
            String north = null;
            String zone = null;
            String datum = null;
            String elev = null;
            if (record.coordinate != null) {
                Coordinate outCoord = new Coordinate();
                JTS.transform(record.coordinate, outCoord, defaultToWgs84Zone14n);
                east = String.format("%.2f", outCoord.x);
                north = String.format("%.2f", outCoord.y);
                zone = "14";
                datum = "WGS84";
                if (!Double.isNaN(record.coordinate.z)) {
                    elev = String.format("%.2f", record.coordinate.z);
                }
            }

            printer.printRecord(record.id, record.name, record.county, east, north, zone, datum, elev, record.length, record.depth, record.survey, record.map, record.tag, record.photo, record.notes);
        }

        printer.close();
    }

    private List<Record> readFromGpx(String fileName) throws Exception {
        GpxDriver driver = GpxDriver.getGpxDriver();
        driver.loadDefaultDriverProperties();

        IGpxReader reader = driver.createReader();

        GpxDocument doc1 = reader.readGpxDocument(new File(fileName));
        SortedList<Waypoint> waypoints = doc1.getWaypoints();

        List<Record> answer = new ArrayList<Record>();
        for (Waypoint waypoint : waypoints) {
            try {

                String id = waypoint.getName();
                String description = waypoint.getDescription();
                String[] descriptionParts = description.split("\n");
                String name = stringAt(descriptionParts, 0, null);
                String county = stringAt(descriptionParts, 1, "county:");
                Double length = doubleAt(descriptionParts, 2, "length:");
                Double depth = doubleAt(descriptionParts, 3, "depth:");
                Boolean survey = booleanAt(descriptionParts, 4, "survey:");
                Boolean map = booleanAt(descriptionParts, 5, "map:");
                Boolean tag = booleanAt(descriptionParts, 6, "tag:");
                Boolean photo = booleanAt(descriptionParts, 7, "photo:");

                Coordinate coordinate;
                if (waypoint.getElevation() == null) {
                    coordinate = new Coordinate(waypoint.getLongitude().doubleValue(), waypoint.getLatitude().doubleValue());
                } else {
                    coordinate = new Coordinate(waypoint.getLongitude().doubleValue(), waypoint.getLatitude().doubleValue(), waypoint.getElevation().doubleValue());
                }

                Record record = new Record(id, name, county, coordinate, length, depth, survey, map, tag, photo, null);
                answer.add(record);
            } catch (Exception e) {
                throw new RuntimeException("Parse error " + waypoint.getName() + " " + waypoint.getDescription());
            }
        }
        return answer;
    }

    private static Double doubleAt(String[] values, int index, String prefix) {
        Double answer = null;
        if (values.length >= index+1 && !values[index].isEmpty()) {
            String value = values[index];
            if (prefix != null) {
                if (!value.startsWith(prefix)) {
                    throw new RuntimeException("invalid prefix. must be '" + prefix + "'");
                }
                value = value.substring(prefix.length());
            }

            answer = Double.parseDouble(value);
        }
        return answer;
    }

    private static String stringAt(String[] values, int index, String prefix) {
        String answer = null;
        if (values.length >= index+1 && !values[index].isEmpty()) {
            String value = values[index];
            if (prefix != null) {
                if (!value.startsWith(prefix)) {
                    throw new RuntimeException("invalid prefix. must be '" + prefix + "'");
                }
                value = value.substring(prefix.length());
            }

            answer = value;
        }
        return answer;
    }

    private static Boolean booleanAt(String[] values, int index, String prefix) {
        Boolean answer = null;
        if (values.length >= index+1 && !values[index].isEmpty()) {
            String value = values[index];
            if (prefix != null) {
                if (!value.startsWith(prefix)) {
                    throw new RuntimeException("invalid prefix. must be '" + prefix + "'");
                }
                value = value.substring(prefix.length());
            }

            if (value.equalsIgnoreCase("yes")) {
                answer = Boolean.TRUE;
            } else if (value.equalsIgnoreCase("no")) {
                answer = Boolean.FALSE;
            } else {
                throw new RuntimeException("unknown boolean. must be 'yes' or 'no'");
            }
        }
        return answer;
    }

    private void writeToGpx(String fileName, List<Record> records) throws Exception {
        Calendar now = Calendar.getInstance();
        GpxDriver driver = GpxDriver.getGpxDriver();
        driver.loadDefaultDriverProperties();

        IGpxWriter writer = driver.createWriter();

        GpxDocument doc = new GpxDocument();
        doc.setCreator("me");

        Metadata meta = new Metadata();
        meta.setDate(now);
        doc.setMetadata(meta);

        SortedList<Waypoint> waypoints = doc.getWaypoints();

        for (Record record : records) {
            if (record.coordinate != null) {

                try {
                    BigDecimal lat = BigDecimal.valueOf(record.coordinate.y);
                    BigDecimal lng = BigDecimal.valueOf(record.coordinate.x);
                    BigDecimal elev = !Double.isNaN(record.coordinate.z) ? BigDecimal.valueOf(record.coordinate.z) : null;

                    Waypoint waypoint;
                    if (elev == null) {
                        waypoint = new Waypoint(lat, lng);
                    } else {
                        waypoint = new Waypoint(lat, lng, elev);
                    }

                    waypoint.setName(record.id);
                    waypoint.setSym("Flag, Green");
                    waypoint.setType("user");
                    waypoint.setTime(now);

                    StringBuilder description = new StringBuilder();
                    description
                            .append(record.name != null ? record.name : "?").append("\n")
                            .append("county:").append(record.county != null ? record.county : "?").append("\n")
                            .append("length:").append(record.length != null ? record.length : "?").append("\n")
                            .append("depth:").append(record.depth != null ? record.depth : "?").append("\n")
                            .append("survey:").append(record.survey != null ? (record.survey ? "yes" : "no") : "?").append("\n")
                            .append("map:").append(record.map != null ? (record.map ? "yes" : "no") : "?");
                    waypoint.setDescription(description.toString());

                    waypoints.add(waypoint);

                } catch (Exception e) {
                    throw new RuntimeException("record=" + record, e);
                }
            }
        }

        //System.out.print(writer.writeToString(doc));
        writer.write(doc, fileName);
    }



    ///////////////  UNUSED

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
}
