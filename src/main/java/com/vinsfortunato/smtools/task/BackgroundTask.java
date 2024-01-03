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
import java.io.IOException;
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
public class BackgroundTask extends Task<List<File>> {
    private static final Logger LOG = LogManager.getLogger(BackgroundTask.class);

    private List<File> files;
    private String replacement;

    /** Pattern used by SM, DWI and SSC formats */
    private static Pattern TAG_PATTERN = Pattern.compile("#\\s*(?:BACKGROUND)\\s*:\\s*([^;]*?)\\s*;");

    public BackgroundTask(List<File> files, String replacement) {
        this.files = files;
        this.replacement = replacement;
    }

    @Override
    protected List<File> call() throws InterruptedException {
        LOG.info("Replacing SIM files backgrounds with {}", replacement);
        updateTitle(Main.getLang().getString("task.background.title"));

        List<File> result = new ArrayList<>();

        int i = 0;  //The file index in the list for progress tracking
        for(File file : files) {
            if(Thread.interrupted()) {
                //Interrupt task and notify
                LOG.info("Background task interrupted. {} files have been modified.", i);
                throw new InterruptedException();
            }

            updateMessage(MessageFormat.format(Main.getLang().getString("task.background.message.file"), file.getPath()));
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
                                output.append(content, cursor, matcher.start());
                                output.append(String.format("#BACKGROUND:%s;", replacement));
                                cursor = matcher.end();
                                changed = true;
                            }
                            if(cursor < content.length()) {
                                output.append(content, cursor, content.length());
                            }
                            break;
                    }
                    if(!changed) {
                        // Add it at the beginning
                        output = new StringBuilder();
                        output.append(String.format("#BACKGROUND:%s;", replacement));
                        output.append("\n");
                        output.append(content);
                    }

                    //Write changes to file
                    Files.write(file.toPath(), output.toString().getBytes());

                    //Mark file as changed
                    result.add(file);
                }
            } catch(IOException e) {
                //Suppress but warn
                LOG.warn("Cannot replace background for file {}", file.getPath());
            }
        }

        LOG.info("Background task done. {} files have been modified.", result.size());

        //Set done progress
        updateMessage(Main.getLang().getString("task.background.message.done"));
        updateProgress(files.size(), files.size());

        return result;
    }

    public List<File> getProcessedFiles() {
        return files;
    }
}
