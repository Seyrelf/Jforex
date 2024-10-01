package singlejartest.model;

import com.dukascopy.api.IBar;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.Period;
import lombok.Getter;
import lombok.Setter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class Area {

    List<IBar> bars;
    double height_as_percent;
    IBar max_bar;
    IBar min_bar;
    Period period;
    Instrument instrument;
    //String why_close;

    public Area(Period period,Instrument instrument) {
        this.period = period;
        this.instrument = instrument;
        this.bars = new ArrayList<IBar>();
        this.height_as_percent = 0.0;
    }

    public void set_Max_Min_bar(IBar max,IBar min) {
        this.max_bar = max;
        this.min_bar = min;
    }

    public void set_Max_Min_bar_finder(){
        IBar max_bar = bars.get(0);
        IBar min_bar = bars.get(0);
        for (IBar bar:bars){
            if(bar.getHigh()>max_bar.getHigh()){
                max_bar = bar;
            }
            if(bar.getLow()<min_bar.getLow()){
                min_bar = bar;
            }
        }
        this.min_bar = min_bar;
        this.max_bar = max_bar;
    }

    public IBar getMaxBar(){
        return this.max_bar;
    }
    public IBar getMinBar(){
        return this.min_bar;
    }

    public void setHeight_as_percent() {
        this.height_as_percent = (this.max_bar.getHigh()-this.min_bar.getLow())/this.max_bar.getHigh();
    }
    public void setHeight_as_percent(double min, double max) {
        this.height_as_percent = (max-min)/max;
    }

    public void addBar(IBar bar) {
        bars.add(bar);
    }

    public void getFirstBarLastTime(){
        Date date1 = new Date(this.getFirst().getTime());
        Date date2 = new Date(this.getLast().getTime());
        System.out.println(date1+""+date2);
    }
    public IBar getFirst(){
        return this.bars.get(0);
    }
    public IBar getLast(){
        return this.bars.get(this.bars.size()-1);
    }

    public Integer getArea_length() {
        return bars.size();
    }

    @Override
    public String toString() {
        return "Protorgovka{" +
                "bars=" + bars +
                ", height_as_percent=" + height_as_percent +
                ", max_bar=" + max_bar +
                ", min_bar=" + min_bar +
                '}';
    }

    public void show_area(){
        ZoneId zone = ZoneId.of("Europe/Moscow");
        ZonedDateTime first_datetime = new Date(this.getFirst().getTime()).toInstant().atZone(zone);
        ZonedDateTime last_datetime = new Date(this.getLast().getTime()).toInstant().atZone(zone);
        System.out.println("Начало зоны: " + first_datetime + "Конец зоны: " + last_datetime + "Ширина зоны" + this.height_as_percent);
    }
    public void addAll(List<IBar> bars) {
        this.bars.addAll(bars);
    }
    public void deleteLast(){
        bars.remove(bars.size()-1);
    }
    public void deleteFirst(){
        bars.remove(0);
    }

}
