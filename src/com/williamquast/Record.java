package com.williamquast;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * Created by wquast on 12/23/14.
 */
public class Record {
    String id;
    String name;
    String county;
    Coordinate coordinate;
    Double length;
    Double depth;
    Boolean survey;
    Boolean map;
    Boolean tag;
    Boolean photo;
    String notes;

    public Record(String id, String name, String county, Coordinate coordinate, Double length, Double depth, Boolean survey, Boolean map, Boolean tag, Boolean photo, String notes) {
        this.id = id;
        this.name = name;
        this.county = county;
        this.coordinate = coordinate;
        this.length = length;
        this.depth = depth;
        this.survey = survey;
        this.map = map;
        this.tag = tag;
        this.photo = photo;
        this.notes = notes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getDepth() {
        return depth;
    }

    public void setDepth(Double depth) {
        this.depth = depth;
    }

    public Boolean getSurvey() {
        return survey;
    }

    public void setSurvey(Boolean survey) {
        this.survey = survey;
    }

    public Boolean getMap() {
        return map;
    }

    public void setMap(Boolean map) {
        this.map = map;
    }

    public Boolean getTag() {
        return tag;
    }

    public void setTag(Boolean tag) {
        this.tag = tag;
    }

    public Boolean getPhoto() {
        return photo;
    }

    public void setPhoto(Boolean photo) {
        this.photo = photo;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    @Override
    public String toString() {
        return "Record{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", county='" + county + '\'' +
                ", coordinate=" + coordinate +
                ", length=" + length +
                ", depth=" + depth +
                ", survey=" + survey +
                ", map=" + map +
                ", tag=" + tag +
                ", photo=" + photo +
                ", notes='" + notes + '\'' +
                '}';
    }
}