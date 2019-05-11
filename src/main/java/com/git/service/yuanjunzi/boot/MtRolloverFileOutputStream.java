package com.git.service.yuanjunzi.boot;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yuanjunzi on 2019/5/10.
 */
public class MtRolloverFileOutputStream extends FilterOutputStream {
    private static Timer __rollover;
    static final String YYYY_MM_DD = "yyyy_mm_dd";
    static final String ROLLOVER_FILE_DATE_FORMAT = "yyyy_MM_dd";
    static final String ROLLOVER_FILE_BACKUP_FORMAT = "HHmmssSSS";
    static final int ROLLOVER_FILE_RETAIN_DAYS = 31;
    static final int ROLLOVER_GZ_OLD_FILE_DAYS = 10;
    private static final String EXT_GZIP = ".gz";
    private MtRolloverFileOutputStream.RollTask _rollTask;
    private SimpleDateFormat _fileBackupFormat;
    private SimpleDateFormat _fileDateFormat;
    private String _filename;
    private File _file;
    private boolean _append;
    private int _retainDays;
    private int _nonCompressDays;

    public MtRolloverFileOutputStream(String filename) throws IOException {
        this(filename, true, 31, 10);
    }

    public MtRolloverFileOutputStream(String filename, boolean append) throws IOException {
        this(filename, append, 31, 10);
    }

    public MtRolloverFileOutputStream(String filename, boolean append, int retainDays, int nonCompressDays) throws IOException {
        this(filename, append, retainDays, nonCompressDays, TimeZone.getDefault());
    }

    public MtRolloverFileOutputStream(String filename, boolean append, int retainDays, int nonCompressDays, TimeZone zone) throws IOException {
        this(filename, append, retainDays, nonCompressDays, zone, (String)null, (String)null);
    }

    public MtRolloverFileOutputStream(String filename, boolean append, int retainDays, int nonCompressDays, TimeZone zone, String dateFormat, String backupFormat) throws IOException {
        super((OutputStream)null);
        if(dateFormat == null) {
            dateFormat = "yyyy_MM_dd";
        }

        this._fileDateFormat = new SimpleDateFormat(dateFormat);
        if(backupFormat == null) {
            backupFormat = "HHmmssSSS";
        }

        this._fileBackupFormat = new SimpleDateFormat(backupFormat);
        this._fileBackupFormat.setTimeZone(zone);
        this._fileDateFormat.setTimeZone(zone);
        if(filename != null) {
            filename = filename.trim();
            if(filename.length() == 0) {
                filename = null;
            }
        }

        if(filename == null) {
            throw new IllegalArgumentException("Invalid filename");
        } else {
            this._filename = filename;
            this._append = append;
            this._retainDays = retainDays;
            this._nonCompressDays = nonCompressDays;
            this.setFile();
            Class var8 = MtRolloverFileOutputStream.class;
            synchronized(MtRolloverFileOutputStream.class) {
                if(__rollover == null) {
                    __rollover = new Timer(MtRolloverFileOutputStream.class.getName(), true);
                }

                this._rollTask = new MtRolloverFileOutputStream.RollTask();
                Calendar now = Calendar.getInstance();
                now.setTimeZone(zone);
                GregorianCalendar midnight = new GregorianCalendar(now.get(1), now.get(2), now.get(5), 23, 0);
                midnight.setTimeZone(zone);
                midnight.add(10, 1);
                __rollover.scheduleAtFixedRate(this._rollTask, midnight.getTime(), 86400000L);
            }
        }
    }

    public String getFilename() {
        return this._filename;
    }

    public String getDatedFilename() {
        return this._file == null?null:this._file.toString();
    }

    public int getRetainDays() {
        return this._retainDays;
    }

    public int getNonCompressDays() {
        return this._nonCompressDays;
    }

    private synchronized void setFile() throws IOException {
        File file = new File(this._filename);
        this._filename = file.getCanonicalPath();
        file = new File(this._filename);
        File dir = new File(file.getParent());
        if(dir.isDirectory() && dir.canWrite()) {
            Date now = new Date();
            String filename = file.getName();
            int i = filename.toLowerCase(Locale.ENGLISH).indexOf("yyyy_mm_dd");
            if(i >= 0) {
                file = new File(dir, filename.substring(0, i) + this._fileDateFormat.format(now) + filename.substring(i + "yyyy_mm_dd".length()));
            }

            if(file.exists() && !file.canWrite()) {
                throw new IOException("Cannot write log file " + file);
            } else {
                if(this.out == null || !file.equals(this._file)) {
                    this._file = file;
                    if(!this._append && file.exists()) {
                        file.renameTo(new File(file.toString() + "." + this._fileBackupFormat.format(now)));
                    }

                    OutputStream oldOut = this.out;

                    try {
                        this.out = new FileOutputStream(file.toString(), this._append);
                    } catch (Exception var11) {
                        throw new IOException("Cannot write to file " + file);
                    } finally {
                        if(oldOut != null) {
                            oldOut.close();
                        }

                    }
                }

            }
        } else {
            throw new IOException("Cannot write log directory " + dir);
        }
    }

    private void removeOldFiles() {
        if(this._retainDays > 0) {
            long now = System.currentTimeMillis();
            File file = new File(this._filename);
            File dir = new File(file.getParent());
            String fn = file.getName();
            int s = fn.toLowerCase(Locale.ENGLISH).indexOf("yyyy_mm_dd");
            if(s < 0) {
                return;
            }

            String prefix = fn.substring(0, s);
            String suffix = fn.substring(s + "yyyy_mm_dd".length());
            String[] logList = dir.list();

            for(int i = 0; i < logList.length; ++i) {
                fn = logList[i];
                if(fn.startsWith(prefix) && fn.indexOf(suffix, prefix.length()) >= 0) {
                    File f = new File(dir, fn);
                    long date = f.lastModified();
                    if((now - date) / 86400000L > (long)this._retainDays) {
                        f.delete();
                    }
                }
            }
        }

    }

    private void compressOldFiles() throws IOException {
        if(this._nonCompressDays > 0 && this._nonCompressDays < this._retainDays) {
            long now = System.currentTimeMillis();
            File file = new File(this._filename);
            File dir = new File(file.getParent());
            String fn = file.getName();
            int s = fn.toLowerCase(Locale.ENGLISH).indexOf("yyyy_mm_dd");
            if(s < 0) {
                return;
            }

            String prefix = fn.substring(0, s);
            String suffix = fn.substring(s + "yyyy_mm_dd".length());
            String[] logList = dir.list();

            for(int i = 0; i < logList.length; ++i) {
                fn = logList[i];
                if(!fn.endsWith(".gz") && fn.startsWith(prefix) && fn.indexOf(suffix, prefix.length()) >= 0) {
                    File source = new File(dir, fn);
                    File destination = new File(dir, fn + ".gz");
                    long date = source.lastModified();
                    if((now - date) / 86400000L >= (long)this._nonCompressDays) {
                        GzCompress.execute(source, destination, true);
                    }
                }
            }
        }

    }

    public void write(byte[] buf) throws IOException {
        this.out.write(buf);
    }

    public void write(byte[] buf, int off, int len) throws IOException {
        this.out.write(buf, off, len);
    }

    public void close() throws IOException {
        Class var1 = MtRolloverFileOutputStream.class;
        synchronized(MtRolloverFileOutputStream.class) {
            try {
                super.close();
            } finally {
                this.out = null;
                this._file = null;
            }

            this._rollTask.cancel();
        }
    }

    private class RollTask extends TimerTask {
        private RollTask() {
        }

        public void run() {
            try {
                MtRolloverFileOutputStream.this.setFile();
                MtRolloverFileOutputStream.this.removeOldFiles();
                MtRolloverFileOutputStream.this.compressOldFiles();
            } catch (IOException var2) {
                var2.printStackTrace();
            }

        }
    }
}
