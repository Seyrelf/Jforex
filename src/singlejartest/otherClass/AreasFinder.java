package singlejartest.otherClass;

import com.dukascopy.api.IBar;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.Period;
import singlejartest.model.Area;
import singlejartest.model.Protorgovka;
import singlejartest.model.TrendDown;
import singlejartest.model.TrendUp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AreasFinder {
    List<Area> areas;
    List<Protorgovka> protorgovkas;
    List<TrendDown> trendDowns;
    List<TrendUp> trendUps;
    List<IBar> bars;
    Period period;
    Instrument instrument;

    public AreasFinder(List<IBar> bars, Period period, Instrument instrument) {
        this.bars = bars;
        this.period = period;
        this.instrument = instrument;
    }
    public long get_period(){
        return bars.get(1).getTime()-bars.get(0).getTime();
    }

    public void find_areas(){

    }

//    public List<TrendDown> find_trendDowns(){
//        List<IBar> bars_trend = new ArrayList<>();
//        List<List<IBar>> bars_trends = new ArrayList<>();
//        List<List<IBar>> bars_trends2 = new ArrayList<>();
//        List<TrendDown> trends = new ArrayList<>();
//        TrendDown trendDown = new TrendDown();
//        List<IBar> trend_creater = new ArrayList<>();
//        Integer size_bars_trends = Math.toIntExact(54000000 / (bars.get(1).getTime() - bars.get(0).getTime()));
//        System.out.println(size_bars_trends);
//        for (IBar bar : bars) {
//            bars_trend.add(bar);
//            if(bars_trend.size()==size_bars_trends){
//                bars_trends.add(bars_trend);
//                bars_trend =new ArrayList<>();
//            }}
//        for(int i = 1;i<bars_trends.size();i++){
//            List<IBar> first_trend = bars_trends.get(i-1);
//            List<IBar> second_trend = bars_trends.get(i);
//            if(i==1){
//                trend_creater.addAll(first_trend);
//            }
//            if(first_trend.stream().mapToDouble(b -> b.getClose()).sum() > second_trend.stream().mapToDouble(b -> b.getClose()).sum() &&
//                    first_trend.stream().mapToDouble(b -> b.getClose()).min().getAsDouble() > second_trend.stream().mapToDouble(b -> b.getClose()).min().getAsDouble()
//            ){
//                trend_creater.addAll(second_trend);
//            }
//            else {
//                if(trend_creater.size()>size_bars_trends){
//                    bars_trends2.add(trend_creater);}
//                trend_creater = new ArrayList<>();
//                trend_creater.addAll(second_trend);
//            }}
//        for(int i = 1;i<bars_trends2.size();i++){
//            List<IBar> first_trend = bars_trends2.get(i-1);
//            List<IBar> second_trend = bars_trends2.get(i);
//            if(i==1){
//                trendDown.addAll(first_trend);
//            }
//            if(first_trend.stream().mapToDouble(b -> b.getClose()).min().getAsDouble()>second_trend.stream().mapToDouble(b -> b.getClose()).min().getAsDouble()){
//                trendDown.addAll(second_trend);
//                if(i==bars_trends2.size()-1){
//                    trends.add(trendDown);
//                }
//            }
//            else {
//                trends.add(trendDown);
//                trendDown = new TrendDown();
//                trendDown.addAll(second_trend);
//            }
//        }
//        return trends;
//    }
//
//    public List<TrendUp> find_trendUp(){
//        List<TrendUp> trends = new ArrayList<>();
//        TrendUp trendUp = new TrendUp();
//        List<IBar> bars_trend = new ArrayList<>();
//        List<List<IBar>> bars_trends = new ArrayList<>();
//        List<List<IBar>> bars_trends2 = new ArrayList<>();
//        List<IBar> trend_creater = new ArrayList<>();
//        Integer size_bars_trends = Math.toIntExact(54000000 / (bars.get(1).getTime() - bars.get(0).getTime()));
//        for (IBar bar : bars) {
//            bars_trend.add(bar);
//            if(bars_trend.size()==size_bars_trends){
//                bars_trends.add(bars_trend);
//                bars_trend =new ArrayList<>();
//            }}
//
//        for(int i = 1;i<bars_trends.size();i++){
//            List<IBar> first_trend = bars_trends.get(i-1);
//            List<IBar> second_trend = bars_trends.get(i);
//            if(i==1){
//                trend_creater.addAll(first_trend);
//            }
//            if(first_trend.stream().mapToDouble(b -> b.getClose()).sum()<second_trend.stream().mapToDouble(b -> b.getClose()).sum()){
//                trend_creater.addAll(second_trend);
//            }
//            else {
//                if(trend_creater.size()>size_bars_trends){
//                    bars_trends2.add(trend_creater);}
//                trend_creater = new ArrayList<>();
//                trend_creater.addAll(second_trend);
//            }}
//        for(int i = 1;i<bars_trends2.size();i++){
//            List<IBar> first_trend = bars_trends2.get(i-1);
//            List<IBar> second_trend = bars_trends2.get(i);
//            if(second_trend.get(0).getTime() - first_trend.get(first_trend.size()-1).getTime() == this.get_period()){
//                System.out.println(first_trend.get(first_trend.size()-1)+""+second_trend.get(0));
//                System.out.println("!S!");//////////////!!!!!!!!!!!!!!
//                if(i==1){
//                    trendUp.addAll(first_trend);
//                }
//                if(first_trend.stream().mapToDouble(b -> b.getClose()).max().getAsDouble()<second_trend.stream().mapToDouble(b -> b.getClose()).max().getAsDouble()){
//                    trendUp.addAll(second_trend);
//                    if(i==bars_trends2.size()-1){
//                        trends.add(trendUp);
//                    }
//                }
//                else {
//                    trends.add(trendUp);
//                    trendUp = new TrendUp();
//                    trendUp.addAll(second_trend);
//                }
//
//        }}
//        return trends;
//    }

    public List<Protorgovka> find_protorgovkas(Double volatillity){
        bars = bars.stream().filter(b->new Date(b.getTime()).getDay()!=0 && new Date(b.getTime()).getDay()!=6).collect(Collectors.toList());
        List<Protorgovka> all_protorgovka_area = new ArrayList<>();
        List<IBar> bars_for_chech_trend;
        double trend_param_changer = volatillity / 4.5;
        double bars_trend_diference;
        double delta_between_bars;
        double delta_in_area;
        double max_cost_in_area;
        double min_cost_in_area;
        double close_cost_bar;
        double close_cost_old_bar;
        boolean flag;
        IBar last_bar = null;
        IBar previous_bar;
        IBar bar;
        Protorgovka protorgovka;
        for(int i = 0;i<bars.size();i++){
            bars_trend_diference = 0;
            flag = true;
            bars_for_chech_trend = new ArrayList<>();
            protorgovka = new Protorgovka(period, instrument);
            bar = bars.get(i);
            protorgovka.addBar(bar);
            delta_in_area = (bar.getClose()+bar.getClose())/2 * volatillity;
            max_cost_in_area = bars.get(i).getHigh();
            min_cost_in_area = bars.get(i).getLow();
            previous_bar = bar;
            bars_for_chech_trend.add(bar);
            for(int j = i+1;j<bars.size();j++){
                delta_between_bars = (previous_bar.getClose() + previous_bar.getClose())/2 * volatillity / 5;
                bar = bars.get(j);
                bars_for_chech_trend.add(bar);
                close_cost_bar = bar.getClose();
                close_cost_old_bar = previous_bar.getHigh();
                max_cost_in_area = Math.max(close_cost_bar, max_cost_in_area);
                min_cost_in_area = Math.min(close_cost_bar, min_cost_in_area);
                if(bars_for_chech_trend.size()==6){
                    bars_trend_diference = 0;
                    for(int b = 0;b<bars_for_chech_trend.size()-1;b++){
                        bars_trend_diference += bars_for_chech_trend.get(b).getClose() - bars_for_chech_trend.get(b+1).getClose();
                    }
                    bars_for_chech_trend.remove(0);
                }
                if(max_cost_in_area-min_cost_in_area>delta_in_area){
                    protorgovka.setWhy_close("Delta area Error: " + (max_cost_in_area-min_cost_in_area) + " > " + delta_in_area );
                    flag = false;
                }

                if(Math.abs(close_cost_bar-close_cost_old_bar)>delta_between_bars){
                    protorgovka.setWhy_close("Delta bars Error: " + ((Math.abs(close_cost_bar-close_cost_old_bar)) + " > " +delta_between_bars ));
                    flag = false;
                }

                if(trend_param_changer*bar.getClose() < Math.abs(bars_trend_diference)){
                    String error = "Delta trend Error: " + ((Math.abs(bars_trend_diference)) + " > " +trend_param_changer*bar.getClose());
                    for(IBar b:bars_for_chech_trend){
                        Date date = new Date(b.getTime());
                        error = error +"\n" +date + " | " + b.getClose();
                    }
                    protorgovka.setWhy_close(error);
                    flag = false;
                }
                if(flag == false){
                    break;
                }
                else {
                    protorgovka.addBar(bar);
                }
                previous_bar = bar;
            }
            if(protorgovka.getArea_length()>=10){
                if(all_protorgovka_area.size()==0){
                    all_protorgovka_area.add(protorgovka);
                    last_bar = protorgovka.getLast();
                }
                try {
                    if (!protorgovka.getLast().equals(last_bar)){
                        all_protorgovka_area.add(protorgovka);
                        last_bar = protorgovka.getLast();
                    }
                }
                catch (Exception e){
                    last_bar = protorgovka.getLast();
                }
            }}
        this.setProtorgovkas(all_protorgovka_area);
        return this.getProtorgovkas();
    }

    public List<TrendUp> getTrendUps() {
        return trendUps;
    }

    public void setTrendUps(List<TrendUp> trendUps) {
        this.trendUps = trendUps;
    }

    public List<TrendDown> getTrendDowns() {
        return trendDowns;
    }

    public void setTrendDowns(List<TrendDown> trendDowns) {
        this.trendDowns = trendDowns;
    }

    public List<Protorgovka> getProtorgovkas() {
        return protorgovkas;
    }

    public void setProtorgovkas(List<Protorgovka> protorgovkas) {
        this.protorgovkas = protorgovkas;
    }

    public List<Area> getAreas() {
        return areas;
    }

    public void setAreas(List<Area> areas) {
        this.areas = areas;
    }
}
