package singlejartest.otherClass;

import com.dukascopy.api.*;
import com.dukascopy.api.impl.History;
import singlejartest.model.Protorgovka;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AnyFunction {
    IContext context;
    IHistory history;
    IAccount account;
    IEngine engine;

    public AnyFunction(IContext context){
        this.context = context;
        this.history = context.getHistory();
        this.account = context.getAccount();
        this.engine = context.getEngine();
    }

    public void check_kombi_long(Instrument instrument,Period period) throws JFException {
        IBar lastBar = history.getBar(instrument,period,OfferSide.BID,1);
        IBar prevBar = history.getBar(instrument,period,OfferSide.BID,2);
        if(lastBar.getClose()>prevBar.getClose() && lastBar.getLow()<prevBar.getLow()){

        }
    }

    public void check_kombi_short(Instrument instrument,Period period) throws JFException {
        IBar lastBar = history.getBar(instrument,period,OfferSide.BID,1);
        IBar prevBar = history.getBar(instrument,period,OfferSide.BID,2);
        if(lastBar.getClose()<prevBar.getClose() && lastBar.getHigh()>prevBar.getHigh()){
        }
    }

    public void get_acc_info() throws JFException {
        System.out.println("ORDERS:  " + engine.getOrders());
        System.out.println("Client Balance: " + account.getClients().iterator().next().getBalance());
    }

    public List<IBar> getBars(Period period, Instrument instrument, int numberOfBars) throws JFException {
        long prevBarTime = history.getPreviousBarStart(period, history.getLastTick(instrument).getTime());
        long startTime =  history.getTimeForNBarsBack(period, prevBarTime, numberOfBars);
        List<IBar> bars = history.getBars(instrument, period, OfferSide.BID, startTime, prevBarTime);
        return bars;
    }

    public List<IBar> getBarsWithTimePeriod(Period period,Instrument instrument,Date startTime,Date prevBarTime) throws JFException {
        List<IBar> bars = history.getBars(instrument, period, OfferSide.BID, startTime.getTime(), prevBarTime.getTime());
        bars = bars.stream().filter(b -> b.getVolume() > 0).collect(Collectors.toList());
        return bars;
    }

    public Protorgovka get_last_protorgovka(Instrument instrument, Period period) throws JFException {
        Protorgovka protorgovka = new Protorgovka(period,instrument);
        double volatillity = get_volatility_v1(instrument);
        List<IBar> bars_for_chech_trend;
        double trend_param_changer = volatillity / 4.5;
        double bars_trend_diference;
        double delta_between_bars;
        double delta_in_area;
        double max_cost_in_area;
        double min_cost_in_area;
        double close_cost_bar;
        double close_cost_old_bar;
        boolean flag_1 = true;
        boolean flag_2;
        IBar last_bar = null;
        IBar previous_bar;
        IBar bar;
        int counter_1 = 0;
        int counter_2 = 1;
        while (flag_1){
            counter_1+=1;
            bars_trend_diference = 0;
            flag_2 = true;
            bars_for_chech_trend = new ArrayList<>();
            protorgovka = new Protorgovka(period,instrument);
            bar = history.getBar(instrument,period,OfferSide.BID,counter_1);
            while (new Date(bar.getTime()).getDay()==0||new Date(bar.getTime()).getMonth()==6 || bar.getVolume()==0){
                counter_1+=1;
                bar = history.getBar(instrument,period,OfferSide.BID,counter_1);
            }
            bar = history.getBar(instrument,period,OfferSide.BID,counter_1);
            protorgovka.addBar(bar);
            delta_in_area = (bar.getClose()+bar.getClose())/2 * volatillity;
            max_cost_in_area = bar.getHigh();
            min_cost_in_area = bar.getLow();
            previous_bar = bar;
            bars_for_chech_trend.add(bar);
            counter_2 = counter_1;
            while (flag_2){
                counter_2 +=1;
                delta_between_bars = (previous_bar.getClose() + previous_bar.getClose())/2 * volatillity / 5;
                bar = history.getBar(instrument,period,OfferSide.BID,counter_2);
                while (new Date(bar.getTime()).getDay()==0||new Date(bar.getTime()).getMonth()==6||bar.getVolume()==0){
                    counter_2+=1;
                    bar = history.getBar(instrument,period,OfferSide.BID,counter_2);
                }
                bars_for_chech_trend.add(bar);
                close_cost_bar = bar.getClose();
                close_cost_old_bar = previous_bar.getClose();
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
                    flag_2 = false;
                }

                if(Math.abs(close_cost_bar-close_cost_old_bar)>delta_between_bars){
                    protorgovka.setWhy_close("Delta bars Error: " + ((Math.abs(close_cost_bar-close_cost_old_bar)) + " > " +delta_between_bars ));
                    flag_2 = false;
                }

                if(trend_param_changer*bar.getClose() < Math.abs(bars_trend_diference)){
                    String error = "Delta trend Error: " + ((Math.abs(bars_trend_diference)) + " > " +trend_param_changer*bar.getClose());
                    for(IBar b:bars_for_chech_trend){
                        Date date = new Date(b.getTime());
                        error = error +"\n" +date + " | " + b.getClose();
                    }
                    protorgovka.deleteLast();
                    protorgovka.setWhy_close(error);
                    flag_2 = false;
                }
                if(flag_2 == false){
                    break;
                }
                else {
                    protorgovka.addBar(bar);
                }
                previous_bar = bar;
            }
            if(protorgovka.getArea_length()>=10){
                List<IBar> l = protorgovka.getBars();
                Collections.reverse(l);
                protorgovka.setBars(l);
                return protorgovka;
            }
        }
        return protorgovka;
    }

    public boolean check_first_long_param_formac1(Protorgovka protorgovka) throws JFException {
        IBar bar = history.getBar(protorgovka.getInstrument(),protorgovka.getPeriod(),OfferSide.BID,1);
        return protorgovka.getMinBar().getClose()-bar.getClose() > protorgovka.get_width_80_percent() ? true:false;
    }

    public boolean check_first_short_param_formac1(Protorgovka protorgovka) throws JFException {
        IBar bar = history.getBar(protorgovka.getInstrument(),protorgovka.getPeriod(),OfferSide.BID,1);
        return bar.getClose()-protorgovka.getMaxBar().getClose() > protorgovka.get_width_80_percent() ? true:false;
    }

    public Double get_volatility_v1(List<IBar> bars){
        bars = bars.stream().filter(b->new Date(b.getTime()).getDay()!=0 && new Date(b.getTime()).getDay()!=6).collect(Collectors.toList());
        if(bars.size() == 0){
            System.out.println("List of bars is empty");
            return null;
        }
        Double result = bars.stream().mapToDouble(b->((b.getHigh()/b.getLow())-1)).sum()/bars.size();
        return result;
    }

    public Double get_volatility_v1(Instrument instrument) throws JFException {
        List<IBar> bars = getBars(Period.DAILY,instrument,30);
        bars = bars.stream().filter(b->new Date(b.getTime()).getDay()!=0 && new Date(b.getTime()).getDay()!=6).collect(Collectors.toList());
        if(bars.size() == 0){
            System.out.println("List of bars is empty");
            return null;
        }
        Double result = bars.stream().mapToDouble(b->((b.getHigh()/b.getLow())-1)).sum()/bars.size();
        return result;
    }

    public Double get_volatility_v2(Instrument instrument) throws JFException {
        List<IBar> bars = getBars(Period.DAILY,instrument,30);
        bars = bars.stream().filter(b->new Date(b.getTime()).getDay()!=0 && new Date(b.getTime()).getDay()!=6).collect(Collectors.toList());
        if(bars.size() == 0){
            System.out.println("List of bars is empty");
            return null;
        }
        Double sum_average = bars.stream().mapToDouble(b->(b.getHigh()+b.getLow())/2).sum()/bars.size();
        Double result = bars.stream().mapToDouble(b->b.getHigh()-b.getLow()).sum()/bars.size()/sum_average;
        return result;
    }

    public Double get_volatility_v2(List<IBar> bars){
        bars = bars.stream().filter(b->new Date(b.getTime()).getDay()!=0 && new Date(b.getTime()).getDay()!=6).collect(Collectors.toList());
        if(bars.size() == 0){
            System.out.println("List of bars is empty");
            return null;
        }
        Double sum_average = bars.stream().mapToDouble(b->(b.getHigh()+b.getLow())/2).sum()/bars.size();
        Double result = bars.stream().mapToDouble(b->b.getHigh()-b.getLow()).sum()/bars.size()/sum_average;
        return result;
    }

    public void buy(Instrument instrument,String label,Double price,Integer percent) throws JFException {
        double value = account.getClients().iterator().next().getBalance() * 0.01 * percent / price;
        Double price_profit = price * 1.07;
        Double price_loss = price * 0.97;
        price_profit = Math.floor(price_profit * 100000) / 100000;
        price_loss = Math.floor(price_loss * 100000) / 100000;
        IOrder order = engine.submitOrder(label,instrument, IEngine.OrderCommand.BUY,value*0.0001,
                history.getLastTick(instrument).getBid(),1,price_loss,price_profit);
    }

}
