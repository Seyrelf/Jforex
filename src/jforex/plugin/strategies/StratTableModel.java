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
package jforex.plugin.strategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import com.dukascopy.api.IContext;
import com.dukascopy.api.JFException;
import com.dukascopy.api.strategy.IStrategyDescriptor;
import com.dukascopy.api.strategy.local.ILocalStrategyDescriptor;
import com.dukascopy.api.util.DateUtils;

@SuppressWarnings("serial")
class StratTableModel extends AbstractTableModel {
        
    private final IContext context;
    private Set<IStrategyDescriptor> stopped = new HashSet<IStrategyDescriptor>();
    
    public StratTableModel(IContext context){
    	this.context = context;  
    }    
    
    private final Column[] columns = new Column[]{
         Column.newReadOnlyColumn(
            "Name",               
            new IGet() {
                @Override
                public String getValue(IStrategyDescriptor order) {
                    return order.getName();
                }
            }), 
         Column.newReadOnlyColumn(
        	"Start time",               
        	new IGet() {
        		@Override
                public String getValue(IStrategyDescriptor order) {
                	return DateUtils.format(order.getStartTime());
                }
         }),
         Column.newReadOnlyColumn(
        	"Params",               
        	new IGet() {
        		@Override
                public String getValue(IStrategyDescriptor order) {
                	return order.getParameters().toString();
                }
         }), 
         Column.newReadOnlyColumn(
        	"Mode",               
        	new IGet() {
        		@Override
                public String getValue(IStrategyDescriptor order) {
                	return order instanceof ILocalStrategyDescriptor ? "LOCAL" : "REMOTE";
                }
         }), 
    };
    
	private List<IStrategyDescriptor> strategyDescriptors = new ArrayList<IStrategyDescriptor>();

	public void resetData(Set<IStrategyDescriptor> strats) {		
		List<IStrategyDescriptor> stratList =new ArrayList<IStrategyDescriptor>(strats);
		Collections.sort(new ArrayList<IStrategyDescriptor>(strats), new Comparator<IStrategyDescriptor>(){
			@Override
			public int compare(IStrategyDescriptor o1, IStrategyDescriptor o2) {				
				return o1.getName().compareTo(o2.getName()) != 0 
							? o1.getName().compareTo(o2.getName())
							: Long.compare(o1.getStartTime(), o2.getStartTime());
			}});
		this.strategyDescriptors = stratList;
		stopped.clear();
		fireTableDataChanged();
	}
	
	public void addStrategy(IStrategyDescriptor strategyDescriptor) {
		this.strategyDescriptors.add(strategyDescriptor);
		stopped.remove(strategyDescriptor);
		fireTableDataChanged();
	}
	
	public void setStopped(IStrategyDescriptor strategyDescriptor) {
		stopped.add(strategyDescriptor);
		fireTableDataChanged();
	}
	
	public void removeStrategy(IStrategyDescriptor lastSelected) {
		strategyDescriptors.remove(lastSelected);
		fireTableDataChanged();
	}
	
	public IStrategyDescriptor getStrategy(int rowNr){
		if(rowNr >= strategyDescriptors.size()){
			return null;
		}
		return strategyDescriptors.get(rowNr);
	}
	
	public boolean isStopped(int rowNr){
		return stopped.contains(strategyDescriptors.get(rowNr));
	}
	
	public boolean isStopped(IStrategyDescriptor strat){
		return stopped.contains(strat);
	}

	public int getRowCount() {
		return strategyDescriptors.size();
	}

	public int getColumnCount() {
		return columns.length;
	}

	public Object getValueAt(int row, int column) {
		IStrategyDescriptor strat = strategyDescriptors.get(row);
		return columns[column].get().getValue(strat);
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columns[columnIndex].isEditable();
	}

	public void setValueAt(final Object aValue, int rowIndex, final int columnIndex) {
		IStrategyDescriptor strat = strategyDescriptors.get(rowIndex);
		try {
			columns[columnIndex].set().setValue(strat, aValue);
		} catch (Exception e) {
			context.getConsole().getErr().format("Could not set value %s to [%s;%s] - %s", aValue, rowIndex, columnIndex, e).println();
		}
	}

	public String getColumnName(int column) {
		return columns[column].getName();
	}



}

