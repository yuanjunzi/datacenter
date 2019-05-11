package com.git.service.yuanjunzi.boot;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.zip.GZIPOutputStream;

/**
 * Created by yuanjunzi on 2019/5/10.
 */
@Slf4j
public class GzCompress {
    private static final int BUF_SIZE = 8102;

    public GzCompress() {
    }

    public static void execute(File source, File destination, boolean deleteSource) throws IOException {
        if (source.exists()) {
            FileInputStream fis = new FileInputStream(source);
            FileOutputStream fos = new FileOutputStream(destination);
            GZIPOutputStream gzos = new GZIPOutputStream(fos);
            BufferedOutputStream os = new BufferedOutputStream(gzos);
            byte[] inbuf = new byte[8102];

            try {
                int n;
                try {
                    while ((n = fis.read(inbuf)) != -1) {
                        os.write(inbuf, 0, n);
                    }
                } catch (IOException var13) {
                    throw var13;
                }
            } finally {
                os.close();
                fis.close();
            }

            if (deleteSource && !source.delete()) {
                log.warn("Unable to delete " + source.toString() + '.');
            }
        }

    }
}
