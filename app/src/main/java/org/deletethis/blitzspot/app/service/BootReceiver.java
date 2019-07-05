/*
 * blitzspot
 * Copyright (C) 2018-2019 Peter Hanula
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.deletethis.blitzspot.app.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.deletethis.blitzspot.lib.Logging;
import org.deletethis.blitzspot.app.InstantState;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        Logging.SERVICE.d("BootDeviceReceiver onReceive, action is " + action);

        if(action != null) {
            switch (action) {
                case "android.intent.action.QUICKBOOT_POWERON":
                case Intent.ACTION_BOOT_COMPLETED:
                    InstantState.get(context).start(context);
            }
        }
    }
}