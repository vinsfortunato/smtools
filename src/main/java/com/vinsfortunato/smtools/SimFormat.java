/*
 * Copyright [2020] [Vincenzo Fortunato]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.vinsfortunato.smtools;

import java.io.File;

public enum SimFormat {
    SM("sm"),
    SSC("ssc"),
    DWI("dwi");

    public final String extension;

    SimFormat(String extension) {
        this.extension = extension;
    }

    public static SimFormat fromFile(File file) {
        return fromFile(file.getName());
    }

    public static SimFormat fromFile(String fileName) {
        fileName = fileName.toLowerCase().trim();
        for(SimFormat format : values()) {
            if(fileName.endsWith("." + format.extension)) {
                return format;
            }
        }
        return null;
    }
}
