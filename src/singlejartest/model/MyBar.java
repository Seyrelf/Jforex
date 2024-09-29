package singlejartest.model;

import com.dukascopy.api.IBar;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.ta4j.core.BaseBar;

import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MyBar implements IBar {
    public double open;
    public double close;
    public double high;
    public double low;
    public double volume;
    public long time;


    @Override
    public String toString() {
        return "MyBar{" +
                "open=" + open +
                ", close=" + close +
                ", high=" + high +
                ", low=" + low +
                ", volume=" + volume +
                ", time=" + time +
                '}';
    }

    @Override
    public double getOpen() {
        return this.open;
    }

    @Override
    public double getClose() {
        return this.close;
    }

    @Override
    public double getLow() {
        return this.low;
    }

    @Override
    public double getHigh() {
        return this.high;
    }

    @Override
    public double getVolume() {
        return this.volume;
    }

    @Override
    public long getTime() {
        return this.time;
    }

    public BaseBar convertIBarToBaseBar(IBar iBar, Duration duration) {
        Date date = new Date((iBar.getTime()));
        ZoneId zoneId = ZoneId.of("Europe/Moscow");
        ZonedDateTime time = date.toInstant().atZone(zoneId);
        BaseBar bar = new BaseBar(duration,time,iBar.getOpen(),iBar.getHigh(),iBar.getLow(),iBar.getClose(),iBar.getVolume());
        return bar;
    }

}
