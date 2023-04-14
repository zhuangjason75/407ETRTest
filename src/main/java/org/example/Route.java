package org.example;

import org.codehaus.jackson.annotate.JsonIgnore;

public class Route {
    private int toId;
    private double distance;
    @JsonIgnore
    private String startDate;
    private boolean enter = true;
    private boolean exit = true;

    public int getToId() {
        return toId;
    }

    public double getDistance() {
        return distance;
    }

    public boolean isEnter() {
        return enter;
    }

    public boolean isExit() {
        return exit;
    }

    public String getStartDate() {
        return startDate;
    }
}
