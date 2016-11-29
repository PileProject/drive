/**
 * Copyright (C) 2011-2016 PILE Project, Inc. <dev@pileproject.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pileproject.drive.execution;

import com.pileproject.drivecommand.machine.MachineBase;
import com.pileproject.drivecommand.model.com.ICommunicator;
import com.pileproject.drivecommand.model.nxt.NxtMachine;

public class NxtMachineProvider implements MachineProvider {

    @Override
    public MachineBase getMachine(ICommunicator communicator) {
        return new NxtMachine(communicator);
    }

    @Override
    public MachineController getMachineController(MachineBase machineBase) {
        if (machineBase instanceof NxtMachine) {
            return new NxtController((NxtMachine) machineBase);
        }

        throw new IllegalArgumentException("Given MachineBase is not instance of NxtMachine: that was " + machineBase.getClass());
    }
}
