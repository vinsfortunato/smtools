package com.vinsfortunato.smtools;

import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.regex.Pattern;

/**
 * Searches for sim file inside a given directory a return a list of them.
 */
public class ScanTask extends Task<List<File>> {
    private static final Logger LOG = LogManager.getLogger(ScanTask.class);
    private File dir;

    public ScanTask(File dir) {
        if(dir.isFile()) {
            throw new IllegalArgumentException("Argument must be a directory");
        }
        this.dir = dir;
    }

    @Override
    protected List<File> call() throws Exception {
        LOG.info("Scanning for SIM files inside {}", dir.getPath());
        List<File> result = new ArrayList<>();

        //A queue containing all the directories to scan
        Queue<File> queue = new PriorityQueue<>();

        //Start with the root directory
        queue.add(this.dir);

        while(!queue.isEmpty()) {
            File dir = queue.poll();
            File[] files = dir.listFiles();

            if(files != null) {
                for(File file : files) {
                    if(file.isDirectory()) {
                        //Add to the directory to the scan queue
                        queue.add(file);
                    } else if(SimFormat.fromFile(file) != null){
                        //File name is a candidate SIM file
                        result.add(file);
                    }
                }
            }
        }

        LOG.info("Scanning has found {} sim files inside {}", result.size(), dir.getPath());

        return result;
    }
}
