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
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;

import com.mentation.fsm.message.IMessage;

public class FiniteStateMachine {
	private volatile FiniteState _current;
	private final BlockingQueue<IMessage> _messageQueue = new LinkedBlockingQueue<>();
	private Thread _fsmThread = null;
	private String _name;
	private java.util.logging.Logger _logger = java.util.logging.Logger.getLogger("FiniteStateMachine");
	
	public FiniteStateMachine(String name, FiniteState initialState) {
		_name = name;
		_current = initialState;
	}
	
	public void consumeMessage(IMessage message) {
		_logger.log(Level.FINE, "Queuing message " + message);
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
			_fsmThread.setDaemon(true);		
			_fsmThread.start();
		}
	}
	
	protected void step() {
		try {
			StringBuilder sb = new StringBuilder(_name).append(' ').append(_current.getName());
			
			IMessage message = _messageQueue.take();
			_current = _current.processMessage(message);
			
			_logger.log(Level.INFO, sb.append(" processing message type ").append(message).
					append(" New state is ").append(_current.getName()).toString());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected Thread getThread() {
		return _fsmThread;
	}
	
	class FsmRunner extends Thread {
		FsmRunner(String name) {
			super(name);
		}
		
		public void run() {
			while (true) {
				step();
			}
		}

	}
}
