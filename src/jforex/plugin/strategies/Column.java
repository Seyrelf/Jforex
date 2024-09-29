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

import com.dukascopy.api.JFException;
import com.dukascopy.api.strategy.IStrategyDescriptor;

//each order column has its name, value representation function and value update function
class Column {

	private final String name;
	private final boolean editable;
	private final IGet get;
	private final ISet set;

	static Column newReadOnlyColumn(String name, IGet getValueFunc) {
		return new Column(name, false, getValueFunc, new ISet() {
			public void setValue(IStrategyDescriptor order, Object value) throws JFException {
			}
		});
	}

	static Column newEditableColumn(String name, IGet getValueFunc, ISet onChangeFunc) {
		return new Column(name, true, getValueFunc, onChangeFunc);
	}

	private Column(String name, boolean editable, IGet getValueFunc, ISet onChangeFunc) {
		this.name = name;
		this.editable = editable;
		this.get = getValueFunc;
		this.set = onChangeFunc;
	}

	public String getName() {
		return name;
	}

	public boolean isEditable() {
		return editable;
	}

	public IGet get() {
		return get;
	}

	public ISet set() {
		return set;
	}
}