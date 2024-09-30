/*
 * Copyright (c) 2017 Dukascopy (Suisse) SA. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *
 * Neither the name of Dukascopy (Suisse) SA or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. DUKASCOPY (SUISSE) SA ("DUKASCOPY")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL DUKASCOPY OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF DUKASCOPY HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 */
package singlejartest;

import com.dukascopy.api.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import singlejartest.model.Area;
import singlejartest.model.Protorgovka;
import singlejartest.model.TrendDown;
import singlejartest.model.TrendUp;
import singlejartest.otherClass.AnyFunction;
import singlejartest.otherClass.AreasFinder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class MA_Play implements IStrategy {
    private IEngine engine = null;
    private IIndicators indicators = null;
    private int tagCounter = 0;
    private IConsole console;
    private IHistory history;
    private IAccount account;
    private int number_order;
    private Algoritms algoritms;
    private AnyFunction anyFunction = new AnyFunction();
    ObjectMapper mapper = new ObjectMapper();


    public void onStart(IContext context) throws JFException {
        this.number_order = 1;
        this.history = context.getHistory();
        this.account = context.getAccount();
        this.engine = context.getEngine();
        this.console = context.getConsole();
        indicators = context.getIndicators();
        List<IBar> bars = getBars(Period.FIFTEEN_MINS,Instrument.GBPUSD,5000);
        AreasFinder areasFinder = new AreasFinder(bars,Period.FIFTEEN_MINS,Instrument.GBPUSD);
        List<Protorgovka> protorgovkas = areasFinder.find_protorgovkas(0.01);
        for (Protorgovka protorgovka : protorgovkas) {
            protorgovka.getFirstBarLastTime();
        }
        Protorgovka protorgovka = get_last_protorgovka(Instrument.GBPUSD,Period.FIFTEEN_MINS);
        protorgovka.getFirstBarLastTime();
        // Analysis
//        System.out.println("SLEEEP!!");
//       try {
//           Thread.sleep(1);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//       }
    }


    public void get_acc_info() throws JFException {
        //System.out.println("BALANCE: " + account.getBalance());
        System.out.println("ORDERS:  " + engine.getOrders());
        System.out.println("Client Balance: " + account.getClients().iterator().next().getBalance());
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

    public List<IBar> getBars(Period period,Instrument instrument,int numberOfBars) throws JFException {
        long prevBarTime = history.getPreviousBarStart(period, history.getLastTick(instrument).getTime());
        long startTime =  history.getTimeForNBarsBack(period, prevBarTime, numberOfBars);
        List<IBar> bars = history.getBars(instrument, period, OfferSide.BID, startTime, prevBarTime);
        return bars;
    }

    public Protorgovka get_last_protorgovka(Instrument instrument,Period period) throws JFException {
        Protorgovka protorgovka = new Protorgovka(period,instrument);
        List<IBar> bars = getBars(Period.DAILY,instrument,30);
        double volatillity = anyFunction.get_volatility_v1(bars);
        System.out.println(volatillity);
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
                System.out.println(counter_2);
                delta_between_bars = (previous_bar.getClose() + previous_bar.getClose())/2 * volatillity / 5;
                bar = history.getBar(instrument,period,OfferSide.BID,counter_2);
                while (new Date(bar.getTime()).getDay()==0||new Date(bar.getTime()).getMonth()==6||bar.getVolume()==0){
                    counter_2+=1;
                    bar = history.getBar(instrument,period,OfferSide.BID,counter_2);
                }
                bars_for_chech_trend.add(bar);
                close_cost_bar = bar.getClose();
                close_cost_old_bar = previous_bar.getClose();
                System.out.println(Math.abs(close_cost_bar-close_cost_old_bar)>delta_between_bars);
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
                    System.out.println("!!");
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

    public List<IBar> getBarsWithTimePeriod(Period period,Instrument instrument,Date startTime,Date prevBarTime) throws JFException {
        List<IBar> bars = history.getBars(instrument, period, OfferSide.BID, startTime.getTime(), prevBarTime.getTime());
        bars = bars.stream().filter(b -> b.getVolume() > 0).collect(Collectors.toList());
        return bars;
    }

    public void onStop() throws JFException {
        for (IOrder order : engine.getOrders()) {
            order.close();
        }
        console.getOut().println("Stopped");
    }


    public boolean check_first_long_param(Protorgovka protorgovka) throws JFException {
        IBar bar = history.getBar(protorgovka.getInstrument(),protorgovka.getPeriod(),OfferSide.BID,1);
        return protorgovka.getMinBar().getClose()-bar.getClose() > protorgovka.get_width_80_percent() ? true:false;
    }

    public boolean check_first_short_param(Protorgovka protorgovka) throws JFException {
        IBar bar = history.getBar(protorgovka.getInstrument(),protorgovka.getPeriod(),OfferSide.BID,1);
        return bar.getClose()-protorgovka.getMaxBar().getClose() > protorgovka.get_width_80_percent() ? true:false;
    }


    public void onTick(Instrument instrument, ITick tick) throws JFException {
        if (instrument.isTradable()) {
            System.out.println("Алгоритм в работе");
            Protorgovka protorgovka = get_last_protorgovka(Instrument.USDJPY,Period.FIFTEEN_MINS);
            protorgovka.getFirstBarLastTime();



            try {
                System.out.println("sleep");
                Thread.sleep(5000);
                System.out.println("wake up");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) {
    }

    //count open positions
    protected int positionsTotal(Instrument instrument) throws JFException {
        int counter = 0;
        for (IOrder order : engine.getOrders(instrument)) {
            if (order.getState() == IOrder.State.FILLED) {
                counter++;
            }
        }
        return counter;
    }

    protected String getLabel(Instrument instrument) {
        String label = instrument.name();
        label = label.substring(0, 2) + label.substring(3, 5);
        label = label + (tagCounter++);
        label = label.toLowerCase();
        return label;
    }

    public void onMessage(IMessage message) throws JFException {
    }

    public void onAccount(IAccount account) throws JFException {
    }
}