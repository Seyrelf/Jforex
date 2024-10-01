package singlejartest.model;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.Period;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Protorgovka extends Area {
    String why_close;

    public Protorgovka(Period period, Instrument instrument){
        super(period,instrument);
        why_close = "";
    }

    public double get_width_80_percent(){
        return (this.getMaxBar().getClose()-this.getMinBar().getClose()) * 0.8;
    }
}
