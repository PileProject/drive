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
package com.pileproject.drive.app;

import com.pileproject.drive.execution.ExecutionActivity;
import com.pileproject.drive.module.FlavorModule;
import com.pileproject.drive.programming.visual.activity.BlockListActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * A component class that deals with build-flavor variants. We use this class following the Dagger instruction.
 *
 * @see <a href="https://google.github.io/dagger/">Dagger</a>
 */
@Singleton @Component(modules = {FlavorModule.class})
public interface DriveComponent {

    void inject(BlockListActivity blockListActivity);

    void inject(ExecutionActivity executionActivity);
}
