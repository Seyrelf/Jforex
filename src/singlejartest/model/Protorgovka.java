package singlejartest.model;

import com.dukascopy.api.IBar;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Protorgovka extends Area {
    String why_close;

    public Protorgovka(){
        super();
        why_close = "";
    }

    public String getWhy_close() {
        return why_close;
    }

    public void setWhy_close(String why_close) {
        this.why_close = why_close;
    }

    public double get_width_80_percent(){
        return (this.getMaxBar().getClose()-this.getMinBar().getClose()) * 0.8;
    }
}
