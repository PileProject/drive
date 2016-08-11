/*
 * Copyright (C) 2011-2015 PILE Project, Inc. <dev@pileproject.com>
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

package com.pileproject.drive.programming.visual.activity;

import android.content.Intent;

import com.pileproject.drive.execution.NxtExecutionActivity;
import com.pileproject.drive.setting.SettingActivity;

public class NxtProgrammingActivity extends ProgrammingActivityBase {

    @Override
    protected Intent getIntentToBlockList() {
        return new Intent(getApplicationContext(), NxtBlockListActivity.class);
    }

    @Override
    protected Intent getIntentToDeviceList() {
        return new Intent(getApplicationContext(), SettingActivity.class);
    }

    @Override
    protected Intent getIntentToExecute() {
        return new Intent(getApplicationContext(), NxtExecutionActivity.class);
    }
}
