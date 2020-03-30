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

package com.vinsfortunato.smtools.task;

import com.vinsfortunato.smtools.Main;
import com.vinsfortunato.smtools.SimFormat;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Increment the offset of the sim file by the given increment.
 * The task result is a list of files that have been effectively modified.
 */
public class OffsetTask extends Task<List<File>> {
    private static final Logger LOG = LogManager.getLogger(OffsetTask.class);

    private List<File> files;
    private double increment;

    /** Pattern used by SM, DWI and SSC formats */
    private static Pattern TAG_PATTERN = Pattern.compile("#\\s*(?:OFFSET)\\s*:\\s*([^;]*?)\\s*;");

    public OffsetTask(List<File> files, double increment) {
        this.files = files;
        this.increment = increment;
    }

    @Override
    protected List<File> call() throws Exception {
        LOG.info("Incrementing SIM files offsets by {}", String.format("%.3f", increment));
        updateTitle(Main.getLang().getString("task.offset.title"));

        List<File> result = new ArrayList<>();

        int i = 0;  //The file index in the list for progress tracking
        for(File file : files) {
            updateMessage(MessageFormat.format(Main.getLang().getString("task.offset.message.file"), file.getPath()));
            updateProgress(++i, files.size());

            try {
                SimFormat format = SimFormat.fromFile(file);
                if(format != null) {
                    String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                    StringBuilder output = new StringBuilder(); //Holds the output
                    boolean changed = false;
                    switch (format) {
                        case SM:
                        case DWI:
                        case SSC:
                            Matcher matcher = TAG_PATTERN.matcher(content);
                            int cursor = 0;
                            while(matcher.find()) {
                                double offset = Float.parseFloat(matcher.group(1));
                                offset += increment;
                                output.append(content, cursor, matcher.start());
                                output.append(String.format("#OFFSET:%.3f;", offset));
                                cursor = matcher.end();
                                changed = true;
                            }
                            if(cursor < content.length()) {
                                output.append(content, cursor, content.length());
                            }
                            break;
                    }
                    if(changed) {
                        //Write changes to file
                        Files.write(file.toPath(), output.toString().getBytes());

                        //Mark file as changed
                        result.add(file);
                    }
                }
            } catch(Exception e) {
                //Suppress but warn
                LOG.warn("Cannot increment offset for file {}", file.getPath());
            }
        }

        LOG.info("Offset task done. {} files have been modified.", result.size());

        //Set done progress
        updateMessage(Main.getLang().getString("task.offset.message.done"));
        updateProgress(files.size(), files.size());

        return result;
    }

    public List<File> getProcessedFiles() {
        return files;
    }
}
