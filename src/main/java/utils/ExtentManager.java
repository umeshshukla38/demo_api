package utils;
import java.util.Date;
import java.net.InetAddress;
import common.GlobalConfigHandler;
import common.GlobalPath;
import java.net.UnknownHostException;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

    private static ExtentReports extent;

    public static ExtentReports createExtentReportInstance() {
        String path = System.getProperty("user.dir") +GlobalPath.CUSTOM_REPORT_FOLDER +"/ExtentReport.html";
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter(path);
        htmlReporter.config().setAutoCreateRelativePathMedia(false);
        htmlReporter.config().setEncoding("utf-8");
        htmlReporter.config().setDocumentTitle("GGL");
        htmlReporter.config().setReportName("Demo Test Suite Report");
        htmlReporter.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");
        htmlReporter.config().setTheme(Theme.STANDARD);
        htmlReporter.config().setCSS(".step-details > img { border: 2px solid #ccc; display: block; margin-top: 5px;height: 30px;width: 50px;}");
        extent = new ExtentReports();
        try {
            extent.setSystemInfo("Organization Name", "Demo Report");
            extent.setSystemInfo("QA Name", "Umesh Shukla");
            extent.setSystemInfo("Os Name", System.getProperty("os.name"));
            extent.setSystemInfo("User Name", System.getProperty("user.name"));
            extent.setSystemInfo("Host Name", InetAddress.getLocalHost().getHostName());
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("Execution Context", GlobalConfigHandler.getEnv());
            extent.setReportUsesManualConfiguration(true);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        extent.attachReporter(htmlReporter);
        return extent;
    }

    public static String getReportName() {
        Date date = new Date();
        String fileName = "Demo_Report" + "_" + date.toString().replace(":", "_").replace(" ", "_");
        return fileName;
    }
}