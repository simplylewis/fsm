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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

import com.mentation.fsm.message.IMessage;

public class FiniteStateMachine {
	private volatile FiniteState _current;
	private final BlockingQueue<IMessage> _messageQueue = new LinkedBlockingQueue<>();
	private FsmRunner _fsmThread = null;
	private String _name;
	private java.util.logging.Logger _logger = java.util.logging.Logger.getLogger("FiniteStateMachine");
	
	public FiniteStateMachine(String name, FiniteState initialState) {
		_name = name;
		_current = initialState;
	}
	
	public void consumeMessage(IMessage message) {
		_logger.log(Level.FINE, "Queuing message " + message.name());
		_messageQueue.add(message);
	}

	public String getName() {
		return _name;
	}
	
	public FiniteState getState() {
		return _current;
	}

	public void start() {
		synchronized(this) {
			if (_fsmThread != null) return;
			_fsmThread = new FsmRunner(_name);
			_fsmThread.setDaemon(false);		
			_fsmThread.start();
		}
	}
	
	protected void step() {
		try {
			
			
			IMessage message = _messageQueue.take();
			StringBuilder sb = new StringBuilder(_name).append(' ').append(_current.getName()).append(" processing message type ").append(message.name());
			
			FiniteState newState = _current.processMessage(message);
			
			if (_current.equals(newState)) {
				sb.append(" No change");
			}
			else {
				_current = newState;
				sb.append(" New state is ").append(_current.getName());
			}
			
			_logger.log(Level.INFO, sb.toString());
		} catch (InterruptedException e) {
			// Ignore
		}
	}

	protected Thread getThread() {
		return _fsmThread;
	}
	
	class FsmRunner extends Thread {
		private boolean _running = false;

		FsmRunner(String name) {
			super(name);
		}
		
		@Override
		public void run() {
			_running = true;
			while (_running ) {
				step();
			}
		}

		void end() {
			_running = false;
			_fsmThread.interrupt();
		}
	}

	public void end() {
		_fsmThread.end();
	}
}
