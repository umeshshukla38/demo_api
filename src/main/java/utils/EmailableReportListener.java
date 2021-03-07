package utils;
import common.FileActions;
import common.GlobalPath;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.collections.Lists;
import org.testng.internal.Utils;
import org.testng.xml.XmlSuite;

public class EmailableReportListener implements IReporter {
    private String report_title = "Demo Report Title";
    private String report_name = "";
	private String file_name = "EmailerReport.html";
    protected PrintWriter writer;
    protected final List<SuiteResult> suiteResults = Lists.newArrayList();
    private static Logger log = LoggerFactory.getLogger(EmailableReportListener.class);
    private final StringBuilder buffer = new StringBuilder();   // Reusable buffer

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outDir) {
        report_name = "Demo Test Report";
        outDir = System.getProperty("user.dir")+GlobalPath.CUSTOM_REPORT_FOLDER;
        FileActions.checkFolderExists(outDir);
        writer = createWriter(outDir);
        for (ISuite suite : suites) {
            suiteResults.add(new SuiteResult(suite));
        }
        writeDocumentStart();
        writeHead();
        writeBody();
        writeDocumentEnd();
        writer.close();
    }

    protected PrintWriter createWriter(String outdir) {
		try {
            new File(outdir).mkdirs();
            return new PrintWriter(new BufferedWriter(new FileWriter(new File(outdir, file_name))));
        } catch (IOException e) {
            log.error("Unable to create output file for "+report_name+".");
            e.printStackTrace();
        }
        return null;
    }

    protected void writeDocumentStart() {
        writer.println("<span style=\"font-size:5px;\"> </span>");
        writer.println("<span style=\"font-size:5px;\"> </span>");
        writer.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\" \"http://www.w3.org/TR/html4/loose.dtd\">");
    }

    protected void writeHead() {
        writer.println("<head>");
        writer.println("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>");
        writer.println("<title>"+report_title+"</title>");
        writeStylesheet();
        writer.println("</head>");
    }
    

    protected void writeStylesheet() {
    	writer.println("<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css\">");
        writer.print("<style type=\"text/css\">");
        writer.print("table {margin-bottom:10px;border-collapse:collapse;empty-cells:show}");
        writer.print("#summary {margin-top:30px; margin-left: 5%; margin-right: 5%;}");
        writer.print("#m {margin-left: 5%; font-size: 16px; font-style: oblique;}");
        writer.print("h1 {font-size:30px}");
        writer.print("body {width:100%;}");
        writer.print("th,td {padding: 8px}");
        writer.print("th {vertical-align:bottom}");
        writer.print("td {vertical-align:top}");
        writer.print("table a {font-weight:bold;color:#0D1EB6;}");
        writer.print(".tbl2 {table-layout: fixed; background-color: antiquewhite;}");
        writer.print(".easy-overview  {margin-left: 5%; margin-right: 5%; table-layout: fixed}");
        // writer.print(".easy-test-overview tr:first-child {background: #3092c0; color: white; font-family: serif;}");
        writer.print(".easy-test-summary {margin-right: 5%; margin-left: 5%; table-layout: fixed;}");
        writer.print(".stripe td {background-color: #E6EBF9}");
        writer.print(".num {text-align:right}");
        writer.print(".passedodd td {background-color: #3F3}");
        writer.print(".passedeven td {background-color: #0A0}");
        writer.print(".skippedodd td {background-color: #DDD}");
        writer.print(".skippedeven td {background-color: #CCC}");
        writer.print(".failedodd td,.attn {background-color: #F33}");
        writer.print(".failedeven td,.stripe .attn {background-color: #D00}");
        writer.print(".stacktrace {font-family:monospace}");
        writer.print(".totop {font-size:85%;text-align:center;border-bottom:2px solid #000}");
        writer.print(".invisible {display:none}");
        writer.print(".result {margin-left: 5%; margin-right: 5%; table-layout: fixed; background-color: #a5ada5;}");
        writer.println("</style>");
    }

    protected void writeBody() {
        writer.println("<body>");
        writeSuiteSummary();
        writer.println("</body>");
    }

    protected void writeDocumentEnd() {
        writer.println("</html>");
    }

    protected void writeSuiteSummary() {
        NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");

        // int totalTestsCount = 0;
        int totalPassedTests = 0;
        int totalSkippedTests = 0;
        int totalFailedTests = 0;
        long totalDuration = 0;

        writer.println("<div class=\"easy-test-overview\">");
        writer.println("<table class=\"table-bordered\" align=\"center\">");
        writer.print("<th style=\"background: #3092c0; color: white; font-family: serif; border: 1px solid #b3b0b0; \">Test Name</th>");
        // writer.print("<th style=\"background: #3092c0; color: white; font-family: serif; border: 1px solid #b3b0b0; \">Test Steps</th>");
        writer.print("<th style=\"background: #3092c0; color: white; font-family: serif; border: 1px solid #b3b0b0; \">Passed</th>");
        writer.print("<th style=\"background: #3092c0; color: white; font-family: serif; border: 1px solid #b3b0b0; \">Retry</th>");
        writer.print("<th style=\"background: #3092c0; color: white; font-family: serif; border: 1px solid #b3b0b0; \">Failed</th>");
        writer.print("<th style=\"background: #3092c0; color: white; font-family: serif; border: 1px solid #b3b0b0; \">Start Time</th>");
        writer.print("<th style=\"background: #3092c0; color: white; font-family: serif; border: 1px solid #b3b0b0; \">End Time</th>");
        writer.print("<th style=\"background: #3092c0; color: white; font-family: serif; border: 1px solid #b3b0b0; \">Total Time<br /><span style= font-size:10px;>(hh:mm:ss)</span></th>");
        writer.print("<th style=\"background: #3092c0; color: white; font-family: serif; border: 1px solid #b3b0b0; \">Included Groups</th>");
        writer.print("<th style=\"background: #3092c0; color: white; font-family: serif; border: 1px solid #b3b0b0; \">Excluded Groups</th>");
        writer.println("</tr>");

        int testIndex = 0;
        for(SuiteResult suiteResult : suiteResults) {
            writer.print("<tr><th colspan=\"9\" style=\"border: 1px solid #dee2e6;\">");
            writer.println("<center><h5><b><I>"+ Utils.escapeHtml(suiteResult.getSuiteName()) +"<I><b></h6></center>");
            writer.println("</th></tr>");

            for (TestResult testResult : suiteResult.getTestResults()) {
                // int testsCount = testResult.getTestCount();
                int passedTests = testResult.getPassedTestCount();
                int skippedTests = testResult.getSkippedTestCount();
                int failedTests = testResult.getFailedTestCount();
               
                Date startTime = testResult.getTestStartTime();
                Date endTime = testResult.getTestEndTime();
                long duration = testResult.getDuration();

                writer.print("<tr style=\"border: 1px solid #dee2e6;\"");
                if ((testIndex % 2) == 1) {
                    writer.print("class=\"stripe\"");
                }
                writer.print(">");

                buffer.setLength(0);
                writeTableData(buffer.append("<b>").append(Utils.escapeHtml(testResult.getTestName())).append("</b>").toString());
                // writeTableData(integerFormat.format(testsCount), "num");
                writeTableData(integerFormat.format(passedTests), "num");
                writeTableData(integerFormat.format(skippedTests), (skippedTests > 0 ? "num attn" : "num"));
                writeTableData(integerFormat.format(failedTests), (failedTests > 0 ? "num attn" : "num"));
                writeTableData(dateFormat.format(startTime),  "num");
                writeTableData(dateFormat.format(endTime),  "num");
                writeTableData(convertTimeToString(duration), "num");
                
                String include_groups =  testResult.getIncludedGroups().toString().trim();
                if(!include_groups.isEmpty()){
                    writeTableData(testResult.getIncludedGroups());
                }else{
                    writeTableData("Not Found");
                }

                String exclude_groups =  testResult.getExcludedGroups().toString().trim();
                if(!exclude_groups.isEmpty()){
                    writeTableData(testResult.getExcludedGroups());
                }else{
                    writeTableData("Not Found");
                }
                
                writer.println("</tr>");
                // totalTestsCount +=testsCount;
                totalPassedTests += passedTests;
                totalSkippedTests += skippedTests;
                totalFailedTests += failedTests;
                totalDuration += duration;
                testIndex++;
            }
        }
		
        // Print totals if there was more than one test
        if (testIndex > 1) {
            writer.print("<tr style=\"border: 1px solid #dee2e6;\">");
            writer.print("<th>Total</th>");
            // writeTableHeader(integerFormat.format(totalTestsCount), "num"); //total Test
            writeTableHeader(integerFormat.format(totalPassedTests), "num");
            writeTableHeader(integerFormat.format(totalSkippedTests), (totalSkippedTests > 0 ? "num attn" : "num"));
            writeTableHeader(integerFormat.format(totalFailedTests), (totalFailedTests > 0 ? "num attn" : "num"));
            writeTableHeader("-", "num");
            writeTableHeader("-", "num");
            // writer.print("<th colspan=\"2\"></th>");
            writeTableHeader(convertTimeToString(totalDuration), "num");
            writeTableHeader("-", "num");
            writeTableHeader("-", "num");
            writer.println("</tr>");
        }

        writer.println("</table>");
        writer.println("</div>");
    }

    protected void writeReporterMessages(List<String> reporterMessages) {
        writer.print("<div class=\"messages\">");
        Iterator<String> iterator = reporterMessages.iterator();
        assert iterator.hasNext();
        if (Reporter.getEscapeHtml()) {
        	writer.print(Utils.escapeHtml(iterator.next()));
        } else {
        	writer.print(iterator.next());
        }
        while (iterator.hasNext()) {
            writer.print("<br/>");
            if (Reporter.getEscapeHtml()) {
            	writer.print(Utils.escapeHtml(iterator.next()));
            } else {
            	writer.print(iterator.next());
            }
        }
        writer.print("</div>");
    }

    protected void writeStackTrace(Throwable throwable) {
        writer.print("<div class=\"stacktrace\">");
        writer.print(Utils.shortStackTrace(throwable, true));
        writer.print("</div>");
    }

    /**
     * Writes a TH element with the specified contents and CSS class names.
     * @param html the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no classes to apply
     */
    protected void writeTableHeader(String html, String cssClasses) {
        writeTag("th", html, cssClasses);
    }

    /**
     * Writes a TD element with the specified contents.
     * @param html the HTML contents
     */
    protected void writeTableData(String html) {
        writeTableData(html, null);
    }

    /**
     * Writes a TD element with the specified contents and CSS class names.
     * @param html the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no classes to apply
     */
    protected void writeTableData(String html, String cssClasses) {
        writeTag("td", html, cssClasses);
    }

    /**
     * Writes an arbitrary HTML element with the specified contents and CSS
     * class names.
     * @param tag the tag name
     * @param html the HTML contents
     * @param cssClasses the space-delimited CSS classes or null if there are no classes to apply
     */
    protected void writeTag(String tag, String html, String cssClasses) {
        writer.print("<");
        writer.print(tag);
        if (cssClasses != null) {
            writer.print(" class=\"\"");
            writer.print(cssClasses);
            writer.print("");
        }
        writer.print(">");
        writer.print(html);
        writer.print("</");
        writer.print(tag);
        writer.print(">");
    }

    /**
     * Groups {@link TestResult}s by suite.
     */
    protected static class SuiteResult {

        private final String suiteName;
        private final List<TestResult> testResults = Lists.newArrayList();

        public SuiteResult(ISuite suite) {
            suiteName = suite.getName();
            for (ISuiteResult suiteResult : suite.getResults().values()) {
                testResults.add(new TestResult(suiteResult.getTestContext()));
            }
        }

        public String getSuiteName() {
            return suiteName;
        }

        /**
         * @return the test results (possibly empty)
         */
        public List<TestResult> getTestResults() {
            return testResults;
        }
    }

    /**
     * Groups {@link ClassResult}s by test, type (configuration or test), and
     * status.
     */
    protected static class TestResult {
        /**
         * Orders test results by class name and then by method name (in lexicographic order).
         */
        protected static final Comparator<ITestResult> RESULT_COMPARATOR = new Comparator<ITestResult>() {
            @Override
            public int compare(ITestResult o1, ITestResult o2) {
                int result = o1.getTestClass().getName().compareTo(o2.getTestClass().getName());
                if (result == 0) {
                    result = o1.getMethod().getMethodName().compareTo(o2.getMethod().getMethodName());
                }
                return result;
            }
        };

        private final String testName;
        private final Date testStartTime;
        private final Date testEndTime;
        private final List<ClassResult> failedConfigurationResults;
        private final List<ClassResult> failedTestResults;
        private final List<ClassResult> skippedConfigurationResults;
        private final List<ClassResult> skippedTestResults;
        private final List<ClassResult> passedTestResults;
        private final int failedTestCount;
        private final int skippedTestCount;
        private final int passedTestCount;
        private final int testCount;
        private final long duration;
        private final String includedGroups;
        private final String excludedGroups;

        public TestResult(ITestContext context) {
            testName = context.getName();

            Set<ITestResult> failedConfigurations = context.getFailedConfigurations().getAllResults();
            Set<ITestResult> failedTests = context.getFailedTests().getAllResults();
            Set<ITestResult> skippedConfigurations = context.getSkippedConfigurations().getAllResults();
            Set<ITestResult> skippedTests = context.getSkippedTests().getAllResults();
            Set<ITestResult> passedTests = context.getPassedTests().getAllResults();

            failedConfigurationResults = groupResults(failedConfigurations);
            failedTestResults = groupResults(failedTests);
            skippedConfigurationResults = groupResults(skippedConfigurations);
            skippedTestResults = groupResults(skippedTests);
            passedTestResults = groupResults(passedTests);
            
            testStartTime = context.getStartDate();
            testEndTime = context.getEndDate();

            failedTestCount = failedTests.size();
            skippedTestCount = skippedTests.size();
            passedTestCount = passedTests.size();
            testCount = context.getAllTestMethods().length;
            duration = context.getEndDate().getTime() -context.getStartDate().getTime();
            includedGroups = formatGroups(context.getIncludedGroups());
            excludedGroups = formatGroups(context.getExcludedGroups());
        }

        /**
         * Groups test results by method and then by class.
         */
        protected List<ClassResult> groupResults(Set<ITestResult> results) {
            List<ClassResult> classResults = Lists.newArrayList();
            if (!results.isEmpty()) {
                List<MethodResult> resultsPerClass = Lists.newArrayList();
                List<ITestResult> resultsPerMethod = Lists.newArrayList();

                List<ITestResult> resultsList = Lists.newArrayList(results);
                Collections.sort(resultsList, RESULT_COMPARATOR);
                Iterator<ITestResult> resultsIterator = resultsList.iterator();
                assert resultsIterator.hasNext();

                ITestResult result = resultsIterator.next();
                resultsPerMethod.add(result);

                String previousClassName = result.getTestClass().getName();
                String previousMethodName = result.getMethod().getMethodName();
                while (resultsIterator.hasNext()) {
                    result = resultsIterator.next();

                    String className = result.getTestClass().getName();
                    if (!previousClassName.equals(className)) {
                        // Different class implies different method
                        assert !resultsPerMethod.isEmpty();
                        resultsPerClass.add(new MethodResult(resultsPerMethod));
                        resultsPerMethod = Lists.newArrayList();

                        assert !resultsPerClass.isEmpty();
                        classResults.add(new ClassResult(previousClassName,resultsPerClass));
                        resultsPerClass = Lists.newArrayList();

                        previousClassName = className;
                        previousMethodName = result.getMethod().getMethodName();
                    } else {
                        String methodName = result.getMethod().getMethodName();
                        if (!previousMethodName.equals(methodName)) {
                            assert !resultsPerMethod.isEmpty();
                            resultsPerClass.add(new MethodResult(resultsPerMethod));
                            resultsPerMethod = Lists.newArrayList();
                            previousMethodName = methodName;
                        }
                    }
                    resultsPerMethod.add(result);
                }
                assert !resultsPerMethod.isEmpty();
                resultsPerClass.add(new MethodResult(resultsPerMethod));
                assert !resultsPerClass.isEmpty();
                classResults.add(new ClassResult(previousClassName, resultsPerClass));
            }
            return classResults;
        }

        public String getTestName() {
            return testName;
        }
        
        public Date getTestStartTime() {
            return testStartTime;
          }
        
        public Date getTestEndTime() {
            return testEndTime;
          }
        

        /**
         * @return the results for failed configurations (possibly empty)
         */
        public List<ClassResult> getFailedConfigurationResults() {
            return failedConfigurationResults;
        }

        /**
         * @return the results for failed tests (possibly empty)
         */
        public List<ClassResult> getFailedTestResults() {
            return failedTestResults;
        }

        /**
         * @return the results for skipped configurations (possibly empty)
         */
        public List<ClassResult> getSkippedConfigurationResults() {
            return skippedConfigurationResults;
        }

        /**
         * @return the results for skipped tests (possibly empty)
         */
        public List<ClassResult> getSkippedTestResults() {
            return skippedTestResults;
        }

        /**
         * @return the results for passed tests (possibly empty)
         */
        public List<ClassResult> getPassedTestResults() {
            return passedTestResults;
        }

        public int getFailedTestCount() {
            return failedTestCount;
        }

        public int getSkippedTestCount() {
            return skippedTestCount;
        }

        public int getPassedTestCount() {
            return passedTestCount;
        }

        public long getDuration() {
            return duration;
        }

        public String getIncludedGroups() {
            return includedGroups;
        }

        public String getExcludedGroups() {
            return excludedGroups;
        }
        
        public int getTestCount() {
            return testCount;
        }

        /**
         * Formats an array of groups for display.
         */
        protected String formatGroups(String[] groups) {
            if (groups.length == 0) {
                return "";
            }

            StringBuilder builder = new StringBuilder();
            builder.append(groups[0]);
            for (int i = 1; i < groups.length; i++) {
                builder.append(", ").append(groups[i]);
            }
            return builder.toString();
        }
    }

    /**
     * Groups {@link MethodResult}s by class.
     */
    protected static class ClassResult {
        private final String className;
        private final List<MethodResult> methodResults;

        /**
         * @param className the class name
         * @param methodResults the non-null, non-empty {@link MethodResult} list
         */
        public ClassResult(String className, List<MethodResult> methodResults) {
            this.className = className;
            this.methodResults = methodResults;
        }

        public String getClassName() {
            return className;
        }

        /**
         * @return the non-null, non-empty {@link MethodResult} list
         */
        public List<MethodResult> getMethodResults() {
            return methodResults;
        }
    }

    /**
     * Groups test results by method.
     */
    protected static class MethodResult {
        private final List<ITestResult> results;

        /**
         * @param results
         *            the non-null, non-empty result list
         */
        public MethodResult(List<ITestResult> results) {
            this.results = results;
        }

        /**
         * @return the non-null, non-empty result list
         */
        public List<ITestResult> getResults() {
            return results;
        }
    }
    
	
	/* Convert long type milliseconds to format hh:mm:ss */
	public String convertTimeToString(long miliSeconds) {
		int hrs = (int) TimeUnit.MILLISECONDS.toHours(miliSeconds) % 24;
		int min = (int) TimeUnit.MILLISECONDS.toMinutes(miliSeconds) % 60;
		int sec = (int) TimeUnit.MILLISECONDS.toSeconds(miliSeconds) % 60;
		return String.format("%02d:%02d:%02d", hrs, min, sec);
	}
}