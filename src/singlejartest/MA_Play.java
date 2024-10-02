package singlejartest;

import com.dukascopy.api.*;
import singlejartest.otherClass.AnyFunction;


public class MA_Play implements IStrategy {
    private IEngine engine = null;
    private IIndicators indicators = null;
    private IConsole console;
    private IHistory history;
    private IAccount account;
    private AnyFunction anyFunction;


    public void onStart(IContext context) throws JFException {
        anyFunction = new AnyFunction(context);
        this.history = context.getHistory();
        this.account = context.getAccount();
        this.engine = context.getEngine();
        this.console = context.getConsole();
        indicators = context.getIndicators();
    }

    public void onStop() throws JFException {
        for (IOrder order : engine.getOrders()) {
            order.close();
        }
        console.getOut().println("Stopped");
    }

    public void onTick(Instrument instrument, ITick tick) throws JFException {
        if (instrument.isTradable()) {
            System.out.println("Алгоритм в работе");

        }
    }

    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) {
    }

    public void onMessage(IMessage message) throws JFException {
    }

    public void onAccount(IAccount account) throws JFException {
    }
}