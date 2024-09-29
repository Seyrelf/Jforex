package Ta4jTest;

import com.dukascopy.api.IBar;
import com.dukascopy.api.Instrument;
import com.dukascopy.indicators.AlligatorIndicator;
import com.dukascopy.indicators.BollingerBands;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser;
import com.opencsv.exceptions.CsvException;
import org.ta4j.core.*;
import org.ta4j.core.backtest.BarSeriesManager;
import org.ta4j.core.criteria.PositionsRatioCriterion;
import org.ta4j.core.criteria.ReturnOverMaxDrawdownCriterion;
import org.ta4j.core.criteria.VersusEnterAndHoldCriterion;
import org.ta4j.core.criteria.pnl.ReturnCriterion;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.WMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.CrossedDownIndicatorRule;
import org.ta4j.core.rules.CrossedUpIndicatorRule;
import org.ta4j.core.rules.StopGainRule;
import org.ta4j.core.rules.StopLossRule;
import singlejartest.model.MyBar;
import singlejartest.otherClass.AnyFunction;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Test4j {
    public static void main(String[] args) throws IOException, CsvException {
        Strata strata = new Strata();
        BarSeries series = strata.getBarSeries();
        AlligatorIndicator indicator;
        BollingerBands bollingerBands;
        Instrument instrument = Instrument.EURUSD;
        List<String> any = new ArrayList<>();
        any.add("aaa");
        any.add("aaa");
        System.out.println(any);
        any.clear();
        Date date2 = new Date(1718575200000l);
        Date date = new Date(1718571600000l);
        System.out.println(Duration.between(date.toInstant(),date2.toInstant()));
        System.out.println(54000l/(1718575200l-1718571600l));

//        System.out.println(series.getBar(0).getTimePeriod());
//        Num firstClosePrice = series.getBar(0).getClosePrice();
//        System.out.println("First close price: " + firstClosePrice.doubleValue());
//        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
//        System.out.println(firstClosePrice.isEqual(closePrice.getValue(0))); // equal to firstClosePrice
//        WMAIndicator shortSma = new WMAIndicator(closePrice,5);
//        System.out.println("5-bars-SMA value at the 42nd index: " + shortSma.getValue(42).doubleValue());
//        WMAIndicator longSma = new WMAIndicator(closePrice,60);
//        System.out.println("30-bars-SMA value at the 42nd index: " + longSma.getValue(51).doubleValue());
//        Rule buyingRule = new CrossedUpIndicatorRule(shortSma, longSma)
//                .or(new CrossedDownIndicatorRule(closePrice, 800));
//        Rule sellingRule = new CrossedDownIndicatorRule(shortSma,longSma).or(new StopLossRule(closePrice,series.numOf(3))).
//                or(new StopGainRule(closePrice,series.numOf(2)));
//        BarSeriesManager seriesManager = new BarSeriesManager(series);
//        TradingRecord tradingRecord = seriesManager.run(new BaseStrategy(buyingRule,sellingRule));
//        System.out.println("Number of positions for our strategy: " + tradingRecord.getPositionCount());
//        AnalysisCriterion winningPositionsRatio = new PositionsRatioCriterion(AnalysisCriterion.PositionFilter.PROFIT);
//        System.out.println("Winning positions ratio: " + winningPositionsRatio.calculate(series, tradingRecord));
//        // Getting a risk-reward ratio
//        AnalysisCriterion romad = new ReturnOverMaxDrawdownCriterion();
//        System.out.println("Return over Max Drawdown: " + romad.calculate(series, tradingRecord));
//
//        // Total return of our strategy vs total return of a buy-and-hold strategy
//        AnalysisCriterion vsBuyAndHold = new VersusEnterAndHoldCriterion(new ReturnCriterion());
//        System.out.println("Our return vs buy-and-hold return: " + vsBuyAndHold.calculate(series, tradingRecord));
    }
    }

    class Strata{
    public BarSeries getBarSeries() throws IOException, CsvException {
        ObjectMapper mapper = new ObjectMapper();
        List<MyBar> bars = List.of(mapper.readValue(new File("bars_usd_jpy.json"), MyBar[].class));
        MyBar Mybar;
        BarSeries series = new BaseBarSeriesBuilder().withName("USD/EUR_SERIES").build();
        for(MyBar bar : bars){
            BaseBar bbar = bar.convertIBarToBaseBar(bar,Duration.ofMinutes(10));
            series.addBar(bbar);
        }
        return series;
    }}

