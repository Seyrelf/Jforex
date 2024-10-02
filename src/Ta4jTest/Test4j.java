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
import singlejartest.otherClass.AnyFunction;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Test4j {
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        Thread thread = new Thread(() -> {
            while(true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                final String[] s = {scanner.nextLine()};
                if(!s[0].equals("t")){
                    System.out.println("wait trend");}
                else {
                    System.out.println("trend!!!");
                }
            }
        });
        thread.start();


        }


    }



