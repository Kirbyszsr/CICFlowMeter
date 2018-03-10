package swing.common;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.io.RandomAccessFile;

import static cic.cs.unb.ca.Sys.FILE_SEP;
import static cic.cs.unb.ca.Sys.LINE_SEP;

public class InsertCsvRow implements Runnable {

    private String header;
    private List<String> rows;
    private String savepath = null;
    private String filename = null;

    public InsertCsvRow(String header, List<String> rows, String savepath, String filename) {
        this.header = header;
        this.rows = rows;
        this.savepath = savepath;
        this.filename = filename;
    }

    @Override
    public void run() {
        if (savepath == null || filename == null || rows == null || rows.size() <= 0) {
            String ex = String.format("savepath=%s,filename=%s", savepath, filename);
            throw new IllegalArgumentException(ex);
        }

        File fileSavPath = new File(savepath);

        if(!fileSavPath.exists()) {
            fileSavPath.mkdirs();
        }


        if(!savepath.endsWith(FILE_SEP)){
            savepath += FILE_SEP;
        }

        File file = new File(savepath+filename);
        FileOutputStream output = null;

        RandomAccessFile pipe = null;
  
        try {
            if (file.exists()) {
                output = new FileOutputStream(file, true);
            }else{
                file.createNewFile();
                output = new FileOutputStream(file);

                if (header != null) {
                    output.write((header+LINE_SEP).getBytes());
                }
            }
            // connect to the named pipe
            pipe = new RandomAccessFile("/tmp/flowmeter", "rw"); 
            for (String row : rows) {
                output.write((row+LINE_SEP).getBytes());
                // write to named pipe
                pipe.write((row+LINE_SEP).getBytes(), 0, (row+LINE_SEP).length());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (output != null) {
                    output.close();
                }
                if (pipe != null) {
                    // close the pipe
                    pipe.close();                
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
