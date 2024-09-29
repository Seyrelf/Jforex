package singlejartest.otherClass;

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

public class TestStrategy {
    public TradingRecord TestStrategyFisher(AbstractIndicator indicator, BarSeries series){
        FisherIndicator fisherIndicator = new FisherIndicator(indicator, 14);
        Num num1 = series.numOf(1.5);
        Num num2 = series.numOf(-1.5);
        for(int i = 0;i<fisherIndicator.stream().count();i++){
            try {
                System.out.println(fisherIndicator.getValue(i));
            }
            catch(Exception e){
                System.out.println(i);
                break;
            }
        }
        Rule entryRule = new CrossedDownIndicatorRule(fisherIndicator,num1);
        Rule exitRule = new CrossedUpIndicatorRule(fisherIndicator,num2);
        Strategy strategy = new BaseStrategy(entryRule, exitRule);
        BarSeriesManager seriesManager = new BarSeriesManager(series);
        TradingRecord tradingRecord = seriesManager.run(strategy);
        return tradingRecord;
    }
    public TradingRecord TestStrategyRSI(int count_bars, AbstractIndicator indicator, BarSeries series,int n1,int n2){
        RSIIndicator rsiIndicator = new RSIIndicator(indicator,count_bars);
        SMAIndicator smaIndicator = new SMAIndicator(indicator,count_bars);
        Rule entryRule = new CrossedDownIndicatorRule(rsiIndicator,n1);
        Rule exitRule = new CrossedUpIndicatorRule(rsiIndicator,n2);
        Strategy strategy = new BaseStrategy(entryRule, exitRule);
        BarSeriesManager seriesManager = new BarSeriesManager(series);
        TradingRecord tradingRecord = seriesManager.run(strategy);
        return tradingRecord;
    }
    public TradingRecord TestStrategySMA(int count_bars, AbstractIndicator indicator, BarSeries series){
        SMAIndicator smaIndicator = new SMAIndicator(indicator,count_bars);
        StandardDeviationIndicator standardDeviationIndicator = new StandardDeviationIndicator(indicator,count_bars);
        BollingerBandsMiddleIndicator bollingerBandsMiddleIndicator = new BollingerBandsMiddleIndicator(smaIndicator);
        BollingerBandsLowerIndicator bollingerBandsLowerIndicator = new BollingerBandsLowerIndicator(bollingerBandsMiddleIndicator,standardDeviationIndicator);
        BollingerBandsUpperIndicator bollingerBandsUpperIndicator = new BollingerBandsUpperIndicator(bollingerBandsMiddleIndicator,standardDeviationIndicator);
        Rule entryRule = new CrossedDownIndicatorRule(indicator,bollingerBandsLowerIndicator);
        Rule exitRule = new CrossedDownIndicatorRule(indicator,bollingerBandsUpperIndicator);
        Strategy strategy = new BaseStrategy(entryRule, exitRule);
        BarSeriesManager seriesManager = new BarSeriesManager(series);
        TradingRecord tradingRecord = seriesManager.run(strategy);
        return tradingRecord;
    }
    public TradingRecord TestStrategyEMA(int count_bars, AbstractIndicator indicator, BarSeries series){
        EMAIndicator smaIndicator = new EMAIndicator(indicator,count_bars);
        StandardDeviationIndicator standardDeviationIndicator = new StandardDeviationIndicator(indicator,count_bars);
        BollingerBandsMiddleIndicator bollingerBandsMiddleIndicator = new BollingerBandsMiddleIndicator(smaIndicator);
        BollingerBandsLowerIndicator bollingerBandsLowerIndicator = new BollingerBandsLowerIndicator(bollingerBandsMiddleIndicator,standardDeviationIndicator);
        BollingerBandsUpperIndicator bollingerBandsUpperIndicator = new BollingerBandsUpperIndicator(bollingerBandsMiddleIndicator,standardDeviationIndicator);
        Rule entryRule = new CrossedDownIndicatorRule(indicator,bollingerBandsLowerIndicator);
        Rule exitRule = new CrossedDownIndicatorRule(indicator,bollingerBandsUpperIndicator);
        Strategy strategy = new BaseStrategy(entryRule, exitRule);
        BarSeriesManager seriesManager = new BarSeriesManager(series);
        TradingRecord tradingRecord = seriesManager.run(strategy);
        return tradingRecord;
    }
    public TradingRecord TestStrategyEMAHL(int count_bars,  BarSeries series){
        ClosePriceIndicator indicator = new ClosePriceIndicator(series);
        HighPriceIndicator highPriceIndicator = new HighPriceIndicator(series);
        LowPriceIndicator lowPriceIndicator = new LowPriceIndicator(series);
        EMAIndicator emaIndicator = new EMAIndicator(indicator,count_bars);
        StandardDeviationIndicator standardDeviationIndicator = new StandardDeviationIndicator(indicator,count_bars);
        BollingerBandsMiddleIndicator bollingerBandsMiddleIndicator = new BollingerBandsMiddleIndicator(emaIndicator);
        BollingerBandsLowerIndicator bollingerBandsLowerIndicator = new BollingerBandsLowerIndicator(bollingerBandsMiddleIndicator,standardDeviationIndicator);
        BollingerBandsUpperIndicator bollingerBandsUpperIndicator = new BollingerBandsUpperIndicator(bollingerBandsMiddleIndicator,standardDeviationIndicator);
        Rule entryRule = new CrossedDownIndicatorRule(highPriceIndicator,bollingerBandsLowerIndicator);
        Rule exitRule = new CrossedDownIndicatorRule(lowPriceIndicator,bollingerBandsUpperIndicator);
        Strategy strategy = new BaseStrategy(entryRule, exitRule);
        BarSeriesManager seriesManager = new BarSeriesManager(series);
        TradingRecord tradingRecord = seriesManager.run(strategy);
        return tradingRecord;
    }
}
