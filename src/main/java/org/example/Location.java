package org.example;

import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.List;

public class Location {

    private String name;

    private double lat;

    private double lng;

    private List<Route> routes;

    @JsonIgnore
    private String devcomment;

    @JsonIgnore
    private boolean forwardEnterEnabled = true;
    @JsonIgnore
    private boolean forwardExitEnabled = true;
    @JsonIgnore
    private boolean reverseEnterEnabled = true;
    @JsonIgnore
    private boolean reverseExitEnabled = true;

    public String getName() {
        return name;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setForwardEnterEnabled(boolean forwardEnterEnabled) {
        this.forwardEnterEnabled = forwardEnterEnabled;
    }

    public void setForwardExitEnabled(boolean forwardExitEnabled) {
        this.forwardExitEnabled = forwardExitEnabled;
    }

    public void setReverseEnterEnabled(boolean reverseEnterEnabled) {
        this.reverseEnterEnabled = reverseEnterEnabled;
    }

    public void setReverseExitEnabled(boolean reverseExitEnabled) {
        this.reverseExitEnabled = reverseExitEnabled;
    }

    public boolean isForwardEnterEnabled() {
        return forwardEnterEnabled;
    }

    public boolean isForwardExitEnabled() {
        return forwardExitEnabled;
    }

    public boolean isReverseEnterEnabled() {
        return reverseEnterEnabled;
    }

    public boolean isReverseExitEnabled() {
        return reverseExitEnabled;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    public String getDevcomment() {
        return devcomment;
    }

    public void setDevcomment(String devcomment) {
        this.devcomment = devcomment;
    }
}
