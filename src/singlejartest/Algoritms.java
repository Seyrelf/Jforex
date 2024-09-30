package singlejartest;

import com.dukascopy.api.*;
import singlejartest.model.MyBar;
import singlejartest.model.Protorgovka;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Algoritms {

    IAccount account;
    IHistory history;
    IEngine engine;
    Integer number_order = 0;

    public Algoritms(IAccount account, IHistory history,IEngine engine){
        this.account = account;
        this.history = history;
        this.engine = engine;
    }

    public void algoritm_first(Instrument instrument, Double price) throws JFException {
        System.out.println(account.getClients().iterator().next().getEquity());
        Double percent = 0.15;
        double value = account.getClients().iterator().next().getEquity() * 0.01 * percent / price;
        Double price_profit = price * 1.03;
        Double price_loss = price * 0.98;
        price_profit = Math.floor(price_profit * 100000) / 100000;
        price_loss = Math.floor(price_loss * 100000) / 100000;
        IOrder order = engine.submitOrder("OrderNumber"+number_order,instrument, IEngine.OrderCommand.BUY,value*0.0001,
                history.getLastTick(instrument).getBid(),1,price_loss,price_profit);
        number_order+=1;
    }

    public void algoritm_second(Instrument instrument,Double price) throws JFException {
        System.out.println(account.getClients().iterator().next().getEquity());
        Double percent = 0.15;
        double value = account.getClients().iterator().next().getEquity() * 0.01 * percent / price;
        System.out.println(account.getClients().iterator().next().getBalance());
        System.out.println(account.getClients().iterator().next().getAccountCurrency());
        Double price_profit = price * 1.03;
        Double price_loss = price * 0.98;
        price_profit = Math.floor(price_profit * 100000) / 100000;
        price_loss = Math.floor(price_loss * 100000) / 100000;
        try {
            IOrder order = engine.submitOrder("OrderNumber"+number_order,instrument, IEngine.OrderCommand.SELL,
                    value*0.0001,history.getLastTick(instrument).getBid(),1,price_loss,price_profit);
            number_order+=1;
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }


//    public List<Protorgovka> find_area_mybars(List<MyBar> bars){
//        List<Protorgovka> all_protorgovka_area = new ArrayList<>();
//        double delta = bars.get(0).getHigh()*0.0025;
//        double delta_in_area = bars.get(0).getHigh()*0.014;
//        IBar max_bar_in_area;
//        IBar min_bar_in_area;
//        double max_cost_bar;
//        double min_cost_bar;
//        double max_cost_old_bar;
//        double min_cost_old_bar;
//        boolean flag;
//        Protorgovka previous_protorgovka = null;
//        MyBar previous_bar;
//        MyBar bar;
//        Protorgovka protorgovka;
//        for(int i = 0;i<bars.size();i++){
//            flag = true;
//            max_bar_in_area = bars.get(i);
//            min_bar_in_area = bars.get(i);
//            protorgovka = new Protorgovka();
//            protorgovka.set_Max_Min_bar(max_bar_in_area,min_bar_in_area);
//            bar = bars.get(i);
//            previous_bar = bar;
//            protorgovka.addBar(bar);
//            for(int j = i+1;j<bars.size();j++){
//                bar = bars.get(j);
//                max_cost_bar = bar.getHigh();
//                min_cost_bar = bar.getLow();
//                max_cost_old_bar = previous_bar.getHigh();
//                min_cost_old_bar = previous_bar.getLow();
//                if(bar.getHigh()>max_bar_in_area.getHigh()){
//                    max_bar_in_area = bar;
//                }
//                if(bar.getLow()<min_bar_in_area.getLow()){
//                    min_bar_in_area = bar;
//                }
//                if(
//                    (max_bar_in_area.getHigh()-min_bar_in_area.getLow()>delta_in_area) ||
//                    (Math.abs(max_cost_bar-max_cost_old_bar)>delta) ||
//                    (Math.abs(min_cost_bar-min_cost_old_bar)>delta)
//                ){
//                    System.out.println("Flag false!!!");
//                    flag = false;
//                }
//                if(flag == false){
//                    protorgovka.setHeight_as_percent();
//                    break;
//                }
//                else {
//                    protorgovka.set_Max_Min_bar(max_bar_in_area,min_bar_in_area);
//                    protorgovka.addBar(bar);
//                }
//                previous_bar = bar;
//            }
//            if(all_protorgovka_area.size()==0){
//                all_protorgovka_area.add(protorgovka);
//                previous_protorgovka = protorgovka;
//            }
//            try {
//                if (!protorgovka.getLast().equals(previous_protorgovka.getLast())){
//                    all_protorgovka_area.add(protorgovka);
//                }
//                previous_protorgovka = protorgovka;
//            }
//            catch (Exception e){
//                previous_protorgovka = protorgovka;
//            }
//        }
//        return all_protorgovka_area;
//}
}
