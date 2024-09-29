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
        this.algoritms = new Algoritms(this.account,this.history,this.engine);
        console.getOut().println("Started");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date dateFrom = new Date();
        Date dateTo = new Date();
        try {
            dateFrom = dateFormat.parse("16/06/2024 00:00:00");
            dateTo = dateFormat.parse("11/07/2024 00:00:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Double v = anyFunction.get_volatility_v1(getBars(Period.DAILY,Instrument.USDJPY,30));
        System.out.println(v);
        List<IBar> bars = getBarsWithTimePeriod(Period.ONE_HOUR,Instrument.USDJPY,dateFrom,dateTo);
        AreasFinder areasFinder = new AreasFinder(bars);
        for (TrendDown trend:areasFinder.find_trendDowns()){
            trend.getFirstBarLastTime();
        }
        System.out.println("");
        for (TrendUp trend:areasFinder.find_trendUp()){
            trend.getFirstBarLastTime();
        }





        // Analysis
        System.out.println("SLEEEP!!");
       try {
           Thread.sleep(10000000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
       }
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

    public HashMap<String, Double> getMax_Min(List<IBar> bars){
        HashMap<String,Double> max_min = new HashMap<>();
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (IBar bar : bars) {
            if (bar.getHigh() > max) {
                max = bar.getHigh();
            }
            if (bar.getLow() < min) {
                min = bar.getLow();
            }
        }
        max_min.put("Max", max);
        max_min.put("Min", min);
        return max_min;
    }

    public Double get_80_percent_last_area(HashMap<String,Double> max_min){
        return (max_min.get("Max")-max_min.get("Min"))*0.8;
    }


    public void onTick(Instrument instrument, ITick tick) throws JFException {
        Scanner sc = new Scanner(System.in);
        if (instrument.isTradable()) {
            //System.out.println("Введите команду для покупки продажи");
            System.out.println("Алгоритм в работе");
            List<IBar> bars = getBars(Period.TEN_MINS,Instrument.EURUSD,60);
            HashMap<String,Double> max_min = getMax_Min(bars);
            Double max_cost = max_min.get("Max");
            Double min_cost = max_min.get("Min");
            Double max_cost_for_bye = max_cost*0.999;
            Double min_cost_for_bye = min_cost*1.001;
            Double cost_bye = history.getLastTick(instrument).getAsk();
            Double cost_sell = history.getLastTick(instrument).getBid();
            // String label = sc.nextLine();
            if(cost_sell>=max_cost_for_bye){
                System.out.println(cost_sell);
                System.out.println(max_cost_for_bye);
                System.out.println(max_cost);

                //algoritms.algoritm_second(instrument,cost_sell);
            }
            if(cost_bye<=min_cost_for_bye){
                System.out.println(cost_bye);
                System.out.println(min_cost_for_bye);
                System.out.println(min_cost);

                //algoritms.algoritm_first(instrument, cost_bye);
            }
//            if(label.equals("b")){
//                algoritm_first(instrument,bar.getHigh());
//            }
//            if(label.equals("s")){
//                algoritm_second(instrument,bar.getLow());
//            }
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