/*
Copyright 2015 Lewis Foti

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.mentation.fsm.state;

import java.util.HashMap;
import java.util.Iterator;

import com.mentation.fsm.action.IStateEntryAction;
import com.mentation.fsm.message.IMessage;

public class FiniteState {
	private IStateEntryAction _stateEntryAction; 
	private HashMap<IMessage, FiniteState> _transitionTable = new HashMap<>();
	private String _name;
	
	public FiniteState(IStateEntryAction stateEntryAction, String name) {
		_stateEntryAction = stateEntryAction;
		_name = name;
	}
	
	public void enter() {
		if (_stateEntryAction != null) {
			_stateEntryAction.execute();
		}
	}
	
	public void addTransition(IMessage messageClass, FiniteState state) {
		_transitionTable.put(messageClass, state);
	}
	
	protected FiniteState processMessage(IMessage message) {
		FiniteState nextState = _transitionTable.get(message);
		
		if (nextState == null) {
			return this;
		}
		
		nextState.enter();
		return nextState;		
	}

	public String getName() {
		return _name;
	}
	
	@Override
	public String toString() {
		return "FiniteState [" + _name
				+ ", _stateEntryAction=" + _stateEntryAction
				+ ", " + listTransitionTable()
				+ "]";
	}	
	
	private String listTransitionTable() {
		StringBuilder sb = new StringBuilder("TransitionTable: ");

		Iterator<IMessage> it = _transitionTable.keySet().iterator();
		
		while (it.hasNext()) {
			IMessage c = it.next();
			sb.append(c).append(" -> ").append(_transitionTable.get(c).getName());
		}
		
		return sb.toString();
	}
	
}
