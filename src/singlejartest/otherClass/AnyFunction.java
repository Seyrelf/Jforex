package singlejartest.otherClass;

import com.dukascopy.api.IBar;
import com.dukascopy.api.Instrument;
import com.dukascopy.api.Period;
import com.dukascopy.indicators.VortexIndicator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ta4j.core.*;
import org.ta4j.core.backtest.BarSeriesManager;
import org.ta4j.core.indicators.*;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import singlejartest.model.MyBar;
import singlejartest.model.Protorgovka;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AnyFunction {
    ObjectMapper mapper = new ObjectMapper();
    public void downloadBars(List<IBar> bars,String file_name){
        File file = new File(file_name+".json");
        try {
            mapper.writeValue(file,bars);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Protorgovka> find_area(List<IBar> bars,Double volatillity){
        bars = bars.stream().filter(b->new Date(b.getTime()).getDay()!=0 && new Date(b.getTime()).getDay()!=6).collect(Collectors.toList());
        List<Protorgovka> all_protorgovka_area = new ArrayList<>();
        List<IBar> bars_for_chech_trend;
        double trend_param_changer = volatillity / 4.5;
        double bars_trend_diference;
        double delta_between_bars;
        double delta_in_area;
        double max_cost_in_area;
        double min_cost_in_area;
        double max_cost_bar;
        double min_cost_bar;
        double max_cost_old_bar;
        double min_cost_old_bar;
        boolean flag;
        IBar last_bar = null;
        IBar previous_bar;
        IBar bar;
        Protorgovka protorgovka;
        for(int i = 0;i<bars.size();i++){
            bars_trend_diference = 0;
            flag = true;
            bars_for_chech_trend = new ArrayList<>();
            protorgovka = new Protorgovka();
            bar = bars.get(i);
            protorgovka.addBar(bar);
            delta_in_area = (bar.getHigh()+bar.getLow())/2 * volatillity;
            max_cost_in_area = bars.get(i).getHigh();
            min_cost_in_area = bars.get(i).getLow();
            previous_bar = bar;
            bars_for_chech_trend.add(bar);
            for(int j = i+1;j<bars.size();j++){
                delta_between_bars = (previous_bar.getHigh() + previous_bar.getLow())/2 * volatillity / 5;
                bar = bars.get(j);
                bars_for_chech_trend.add(bar);
                max_cost_bar = bar.getHigh();
                min_cost_bar = bar.getLow();
                max_cost_old_bar = previous_bar.getHigh();
                min_cost_old_bar = previous_bar.getLow();
                max_cost_in_area = Math.max(max_cost_bar, max_cost_in_area);
                min_cost_in_area = Math.min(min_cost_bar, min_cost_in_area);
                if(bars_for_chech_trend.size()==6){
                    bars_trend_diference = 0;
                    for(int b = 0;b<bars_for_chech_trend.size()-1;b++){
                        bars_trend_diference += bars_for_chech_trend.get(b).getClose() - bars_for_chech_trend.get(b+1).getClose();
                    }
                    bars_for_chech_trend.remove(0);
                }
                if(max_cost_in_area-min_cost_in_area>delta_in_area){
                    protorgovka.setWhy_close("Delta area Error: " + (max_cost_in_area-min_cost_in_area) + " > " + delta_in_area );
                    System.out.println("Flag false!!!");
                    flag = false;
                }

                if(Math.abs(max_cost_bar-max_cost_old_bar)>delta_between_bars){
                    protorgovka.setWhy_close("Delta bars Error: " + ((Math.abs(max_cost_bar-max_cost_old_bar)) + " > " +delta_between_bars ));
                    System.out.println("Flag false!!!");
                    flag = false;
                }

                if(Math.abs(min_cost_bar-min_cost_old_bar)>delta_between_bars){
                    protorgovka.setWhy_close("Delta bars Error: " + ((Math.abs(min_cost_bar-min_cost_old_bar)) + " > " +delta_between_bars ));
                    System.out.println("Flag false!!!");
                    flag = false;
                }

                if(trend_param_changer*bar.getClose() < Math.abs(bars_trend_diference)){
                    String error = "Delta trend Error: " + ((Math.abs(bars_trend_diference)) + " > " +trend_param_changer*bar.getClose());
                    for(IBar b:bars_for_chech_trend){
                        Date date = new Date(b.getTime());
                        error = error +"\n" +date + " | " + b.getClose();
                    }
                    protorgovka.setWhy_close(error);
                    System.out.println("Flag false!!!");
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
        return all_protorgovka_area;
    }


    public List<Protorgovka> find_area_mybars(List<MyBar> bars){
        List<Protorgovka> all_protorgovka_area = new ArrayList<>();
        double delta = bars.get(0).getHigh()*0.0025;
        double delta_in_area = bars.get(0).getHigh()*0.014;
        IBar max_bar_in_area;
        IBar min_bar_in_area;
        double max_cost_bar;
        double min_cost_bar;
        double max_cost_old_bar;
        double min_cost_old_bar;
        boolean flag;
        Protorgovka previous_protorgovka = null;
        MyBar previous_bar;
        MyBar bar;
        Protorgovka protorgovka;
        for(int i = 0;i<bars.size();i++){
            flag = true;
            max_bar_in_area = bars.get(i);
            min_bar_in_area = bars.get(i);
            protorgovka = new Protorgovka();
            protorgovka.set_Max_Min_bar(max_bar_in_area,min_bar_in_area);
            bar = bars.get(i);
            previous_bar = bar;
            protorgovka.addBar(bar);
            for(int j = i+1;j<bars.size();j++){
                bar = bars.get(j);
                max_cost_bar = bar.getHigh();
                min_cost_bar = bar.getLow();
                max_cost_old_bar = previous_bar.getHigh();
                min_cost_old_bar = previous_bar.getLow();
                if(bar.getHigh()>max_bar_in_area.getHigh()){
                    max_bar_in_area = bar;
                }
                if(bar.getLow()<min_bar_in_area.getLow()){
                    min_bar_in_area = bar;
                }
                if(
                        (max_bar_in_area.getHigh()-min_bar_in_area.getLow()>delta_in_area) ||
                                (Math.abs(max_cost_bar-max_cost_old_bar)>delta) ||
                                (Math.abs(min_cost_bar-min_cost_old_bar)>delta)
                ){
                    System.out.println("Flag false!!!");
                    flag = false;
                }
                if(flag == false){
                    protorgovka.setHeight_as_percent();
                    break;
                }
                else {
                    protorgovka.set_Max_Min_bar(max_bar_in_area,min_bar_in_area);
                    protorgovka.addBar(bar);
                }
                previous_bar = bar;
            }
            if(all_protorgovka_area.size()==0){
                all_protorgovka_area.add(protorgovka);
                previous_protorgovka = protorgovka;
            }
            try {
                if (!protorgovka.getLast().equals(previous_protorgovka.getLast())){
                    all_protorgovka_area.add(protorgovka);
                }
                previous_protorgovka = protorgovka;
            }
            catch (Exception e){
                previous_protorgovka = protorgovka;
            }
        }
        return all_protorgovka_area;}

    public Double get_volatility_v1(List<IBar> bars){
        bars = bars.stream().filter(b->new Date(b.getTime()).getDay()!=0 && new Date(b.getTime()).getDay()!=6).collect(Collectors.toList());
        if(bars.size() == 0){
            System.out.println("List of bars is empty");
            return null;
        }
        Double result = bars.stream().mapToDouble(b->((b.getHigh()/b.getLow())-1)*100).sum()/bars.size();
        return result;
    }

    public Double get_volatility_v2(List<IBar> bars){
        bars = bars.stream().filter(b->new Date(b.getTime()).getDay()!=0 && new Date(b.getTime()).getDay()!=6).collect(Collectors.toList());
        if(bars.size() == 0){
            System.out.println("List of bars is empty");
            return null;
        }
        Double sum_average = bars.stream().mapToDouble(b->(b.getHigh()+b.getLow())/2).sum()/bars.size();
        Double result = bars.stream().mapToDouble(b->b.getHigh()-b.getLow()).sum()/bars.size()*100/sum_average;
        return result;
    }

    public Double get_volatility_v3(List<IBar> bars){
        bars = bars.stream().filter(b->new Date(b.getTime()).getDay()!=0 && new Date(b.getTime()).getDay()!=6).collect(Collectors.toList());
        if(bars.size() == 0){
            System.out.println("List of bars is empty");
            return null;
        }
        Double result = 0.0;
        for(int i = 1; i < bars.size(); i++){
            result += Math.abs(bars.get(i-1).getClose() - bars.get(i).getHigh());
        }
        Double sum_average = bars.stream().mapToDouble(b->(b.getHigh()+b.getLow())/2).sum()/bars.size();
        result = result/bars.size()*100/sum_average;
        return result;
    }

    public Double get_volatility_v4(List<IBar> bars){
        bars = bars.stream().filter(b->new Date(b.getTime()).getDay()!=0 && new Date(b.getTime()).getDay()!=6).collect(Collectors.toList());
        if(bars.size() == 0){
            System.out.println("List of bars is empty");
            return null;
        }
        Double result = 0.0;
        for(int i = 1; i < bars.size(); i++){
            result += Math.abs(bars.get(i-1).getOpen() - bars.get(i).getLow());
        }
        Double sum_average = bars.stream().mapToDouble(b->(b.getHigh()+b.getLow())/2).sum()/bars.size();
        result = result/bars.size()*100/sum_average;
        return result;
    }
}
