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

import com.mentation.fsm.message.IMessage;
import com.mentation.fsm.state.FiniteState;

public class FiniteStateTest {	

	class M1 implements IMessage {}
	class M2 implements IMessage {}
	class M3 implements IMessage {}
	
  @Test
  public void processMessage() {
	  FiniteState stateOne = new FiniteState(null, "stateOne");
	  FiniteState stateTwo = new FiniteState(null, "stateTwo");
	  FiniteState stateThree = new FiniteState(null, "stateThree");
	  
	  IMessage messageOne = new M1();
	  IMessage messageTwo = new M2();
	  IMessage messageThree = new M3();
	  	  
	  Assert.assertEquals(stateOne.processMessage(messageTwo), stateOne);
	  
	  stateOne.addTransition(messageTwo, stateTwo);
	  stateOne.addTransition(messageThree, stateThree);
	  
	  stateTwo.addTransition(messageThree, stateThree);
	  
	  Assert.assertEquals(stateOne.processMessage(messageOne), stateOne);
	  Assert.assertEquals(stateOne.processMessage(messageTwo), stateTwo);
	  Assert.assertEquals(stateOne.processMessage(messageThree), stateThree);
	  
	  Assert.assertEquals(stateTwo.processMessage(messageOne), stateTwo);
	  Assert.assertEquals(stateTwo.processMessage(messageTwo), stateTwo);
	  Assert.assertEquals(stateTwo.processMessage(messageThree), stateThree);
  }
}
