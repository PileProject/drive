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
package com.pileproject.drive.module;

import com.pileproject.drive.machine.MachineProvider;
import com.pileproject.drive.machine.NxtMachineProvider;
import com.pileproject.drive.programming.visual.block.BlockProvider;
import com.pileproject.drive.programming.visual.block.NxtBlockProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * A module class for configuring Product Flavor related injection.
 *
 * @see <a href="https://google.github.io/dagger/">Dagger</a>
 */
@Module
public class FlavorModule {

    @Provides @Singleton
    public BlockProvider provideBlockProvider() {
        return new NxtBlockProvider();
    }

    @Provides @Singleton
    public MachineProvider provideMachineProvider() {
        return new NxtMachineProvider();
    }
}
