package momdp;

import java.io.File;

public class Main {

    private String pathFolder= "./instances";

    private void readFolder(){
        String [] folders =new File(pathFolder).list();
        File file;

        for (String f:folders) {
            file=new File(pathFolder+"/"+f);
            if(!f.startsWith(".") && !f.startsWith("..") && file.isDirectory()){
                
            }
        }

    }
}
