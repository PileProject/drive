/**
 * Copyright (C) 2011-2017 The PILE Developers <pile-dev@googlegroups.com>
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
package com.pileproject.drive.machine;

import com.pileproject.drivecommand.machine.MachineBase;
import com.pileproject.drivecommand.model.com.ICommunicator;

/**
 * An interface for providing {@link MachineBase} and {@link MachineController}.
 * Concrete classes are supposed to provide the concrete classes of {@link MachineBase}
 * and {@link MachineController} with respect to their responsible robots.
 *
 */
public interface MachineProvider {

    /**
     * Returns a concrete machine with the given communicator.
     *
     * @param communicator the instance of {@link ICommunicator}
     * @return a concrete instance of {@link MachineBase}
     */
    MachineBase getMachine(ICommunicator communicator);

    /**
     * Returns a concrete machine controller for the given machine.
     *
     * @param machineBase
     *      the instance of {@link MachineBase} which will be manipulated by the returned {@link MachineController}
     * @return a concrete instance of {@link MachineController}
     */
    MachineController getMachineController(MachineBase machineBase);
}
