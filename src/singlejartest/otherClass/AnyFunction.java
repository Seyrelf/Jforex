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

    public Double get_volatility_v1(List<IBar> bars){
        bars = bars.stream().filter(b->new Date(b.getTime()).getDay()!=0 && new Date(b.getTime()).getDay()!=6).collect(Collectors.toList());
        if(bars.size() == 0){
            System.out.println("List of bars is empty");
            return null;
        }
        Double result = bars.stream().mapToDouble(b->((b.getHigh()/b.getLow())-1)).sum()/bars.size();
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
