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
package jforex.plugin.ordertable;

import com.dukascopy.api.IOrder;
import com.dukascopy.api.JFException;

//each order column has its name, value representation function and value update function
class OrderColumn {

	private final String name;
	private final boolean editable;
	private final IOrderGet orderGet;
	private final IOrderSet orderSet;

	static OrderColumn newReadOnlyColumn(String name, IOrderGet getValueFunc) {
		return new OrderColumn(name, false, getValueFunc, new IOrderSet() {
			public void setValue(IOrder order, Object value) throws JFException {
			}
		});
	}

	static OrderColumn newEditableColumn(String name, IOrderGet getValueFunc, IOrderSet onChangeFunc) {
		return new OrderColumn(name, true, getValueFunc, onChangeFunc);
	}

	private OrderColumn(String name, boolean editable, IOrderGet getValueFunc, IOrderSet onChangeFunc) {
		this.name = name;
		this.editable = editable;
		this.orderGet = getValueFunc;
		this.orderSet = onChangeFunc;
	}

	public String getName() {
		return name;
	}

	public boolean isEditable() {
		return editable;
	}

	public IOrderGet getOrderGet() {
		return orderGet;
	}

	public IOrderSet getOrderSet() {
		return orderSet;
	}
}