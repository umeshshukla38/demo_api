package common;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileActions {

    public static Properties prop;
    private static Logger log = LoggerFactory.getLogger(FileActions.class);

    
     /**
     * Read Prop file
     */
    public static Properties readProp(String filename) {
        String filepath = System.getProperty("user.dir") + "/src/main/resources/properties/" + filename;
        try {
            FileInputStream file = new FileInputStream(filepath);
            prop = new Properties();
            prop.load(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

    /**
     * Create Directory
     */
    public static String checkFolderExists(String ex_folder_path) {
        File folder_name = new File(ex_folder_path);
        if (!folder_name.isDirectory()) {
            log.info("Folder does not exists creating new folder with name : " + ex_folder_path);
            folder_name.mkdir();
            folder_name.setReadable(true);
            folder_name.setWritable(true);
            folder_name.setExecutable(true);
        } else {
            log.info("Folder already exists with name : " + ex_folder_path);
        }
        return ex_folder_path;
    }

    public static void main(String[] args) {
        readProp("app.properties");
    }
}