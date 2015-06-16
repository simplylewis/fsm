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

import org.testng.Assert;
import org.testng.annotations.Test;

import mockit.*;

import com.mentation.fsm.action.IStateEntryAction;
import com.mentation.fsm.message.IMessage;

public final class FiniteStateMachineTest {

	@Mocked IStateEntryAction actionOne;
	@Mocked IStateEntryAction actionTwo;
	
	static class M1 implements IMessage {};
	static class M2 implements IMessage {};
	
	@Test
	public void consumeMessageNoAction() {
		FiniteState stateOne = new FiniteState(null, "t1StateOne");
		FiniteState stateTwo = new FiniteState(null, "t1StateTwo");

		IMessage messageOne = new M1();
		IMessage messageTwo = new M2();

		stateOne.addTransition(messageTwo, stateTwo);
		stateTwo.addTransition(messageOne, stateOne);

		FiniteStateMachine fsm = new FiniteStateMachine("fsmOne", stateOne);
		
		
		Assert.assertEquals(fsm.getState(), stateOne);
		
		fsm.consumeMessage(messageOne);
		fsm.step();
		
		Assert.assertEquals(fsm.getState(), stateOne);

		fsm.consumeMessage(messageTwo);
		fsm.step();
		
		Assert.assertEquals(fsm.getState(), stateTwo);

		fsm.consumeMessage(messageTwo);
		fsm.step();
		
		Assert.assertEquals(fsm.getState(), stateTwo);

		fsm.consumeMessage(messageOne);
		fsm.step();
		
		Assert.assertEquals(fsm.getState(), stateOne);
	}

	@Test
	public void invokeActionOnEntry() {
		FiniteState stateOne = new FiniteState(actionOne, "t2StateOne");
		FiniteState stateTwo = new FiniteState(actionTwo, "t2StateTwo");

		IMessage messageTwo = new M2();

		stateOne.addTransition(messageTwo, stateTwo);

		FiniteStateMachine fsm = new FiniteStateMachine("fsmTwo", stateOne);
		
		fsm.consumeMessage(messageTwo);
		fsm.step();
		
		new Verifications() {{ actionOne.execute(); times = 0; }};
		new Verifications() {{ actionTwo.execute(); times = 1; }};
	}

	@Test
	public void showStartWorks() {
		FiniteState stateOne = new FiniteState(actionOne, "t3StateOne");
		FiniteState stateTwo = new FiniteState(actionTwo, "t3StateTwo");

		IMessage messageTwo = new M2();

		stateOne.addTransition(messageTwo, stateTwo);

		FiniteStateMachine fsm = new FiniteStateMachine("fsmThree", stateOne);
		fsm.start();
		
		fsm.consumeMessage(messageTwo);
		sleep();
		
		Assert.assertEquals(fsm.getState(), stateTwo);
		
		new Verifications() {{ actionOne.execute(); times = 0; }};
		new Verifications() {{ actionTwo.execute(); times = 1; }};
				
		Assert.assertTrue(isThreadPresent(fsm));
		
		Thread t1 = fsm.getThread();
		fsm.start(); // Show this does not replace the existing thread
		Assert.assertEquals(fsm.getThread(), t1);
	}

	private boolean isThreadPresent(FiniteStateMachine fsm) {
		Thread[] threads = new Thread[Thread.activeCount()+1];
		int threadCount = Thread.enumerate(threads);
		
		for(int i = 0; i < threadCount; i++) {
			if (threads[i].getName().equals(fsm.getName())) {
				return true;
			}
		}
		
		return false;
	}

	private void sleep() {
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
