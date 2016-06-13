import com.github.timm.cucumber.options.ExtendedRuntimeOptions;
import cucumber.runtime.Runtime;
import cucumber.runtime.io.MultiLoader;
import cucumber.runtime.io.ResourceLoaderClassFinder;
import net.masterthought.cucumber.ReportBuilder;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Parallel01IT {

    private String outputPath = "target/cucumber-parallel/Parallel01IT/Parallel01IT";
    private String glue = "foo, bar";

    @Test
    public void reRun() {

        defaultRun();

        int count = 1;

        switch (count) {
            case 1:
                firstRun();
                break;
            case 2:
                firstRun();
                secondRun();
                break;
            case 3:
                firstRun();
                secondRun();
                thirdRun();
                break;
            case 4:
                firstRun();
                secondRun();
                thirdRun();
                fourthRun();
                break;
            case 5:
                firstRun();
                secondRun();
                thirdRun();
                fourthRun();
                fifthRun();
                break;
            default:
                break;
        }
        GenerateAllRunReports();

    }

    private void defaultRun() {
        List<String> arguments = new ArrayList<String>();
        arguments.add("classpath:features/feature1.feature");
        String[] tags = {"@complete", "@accepted"};
        for (String tag : tags) {
            arguments.add("--tags");
            arguments.add(tag);
        }
        arguments.add("--plugin");
        arguments.add("html:target/cucumber-parallel/Parallel01IT/Parallel01IT.html");
        arguments.add("--plugin");
        arguments.add("json:target/cucumber-parallel/Parallel01IT/Parallel01IT.json");
        arguments.add("--plugin");
        arguments.add("rerun:target/cucumber-parallel/Parallel01IT/Parallel01IT.txt");
        String[] gluepackages = glue.split(",");
        for (String packages : gluepackages) {
            if (!packages.contains("none")) {
                arguments.add("--glue");
                arguments.add(packages);
            }
        }
        final String[] argv = arguments.toArray(new String[0]);
        try {
            executetests(argv);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void firstRun() {
        try {
            if (new File(outputPath + ".txt").exists() && new BufferedReader(new FileReader(outputPath + ".txt")).readLine() != null) {
                //ExecuteReRerun first arg: refactored input file ; second arg:- output json file path for getting result
                ExecuteReRerun("@" + outputPath + ".txt", outputPath + "/cucumber1.json", outputPath + "/rerun1.txt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void secondRun() {
        try {
            if (new File(outputPath + "/rerun1.txt").exists() && new BufferedReader(new FileReader(outputPath + "/rerun1.txt")).readLine() != null) {
                //ExecuteReRerun first arg: refactored input file ; second arg:- output json file path for getting result
                ExecuteReRerun("@" + outputPath + "/rerun1.txt", outputPath + "/cucumber2.json", outputPath + "/rerun2.txt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void thirdRun() {
        try {
            if (new File(outputPath + "/rerun2.txt").exists() && new BufferedReader(new FileReader(outputPath + "/rerun2.txt")).readLine() != null) {
                //ExecuteReRerun first arg: refactored input file ; second arg:- output json file path for getting result
                ExecuteReRerun("@" + outputPath + "/rerun2.txt", outputPath + "/cucumber3.json", outputPath + "/rerun3.txt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fourthRun() {
        try {
            if (new File(outputPath + "/rerun3.txt").exists() && new BufferedReader(new FileReader(outputPath + "/rerun3.txt")).readLine() != null) {
                //ExecuteReRerun first arg: refactored input file ; second arg:- output json file path for getting result
                ExecuteReRerun("@" + outputPath + "/rerun3.txt", outputPath + "/cucumber4.json", outputPath + "/rerun4.txt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void fifthRun() {
        try {
            if (new File(outputPath + "/rerun4.txt").exists() && new BufferedReader(new FileReader(outputPath + "/rerun4.txt")).readLine() != null) {
                //ExecuteReRerun first arg: refactored input file ; second arg:- output json file path for getting result
                ExecuteReRerun("@" + outputPath + "/rerun4.txt", outputPath + "/cucumber5.json", outputPath + "/rerun5.txt");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ExecuteReRerun(String rerunFile, String targetJson, String targetRerun) {
        List<String> arguments = new ArrayList<String>();
        arguments.add(rerunFile);
        arguments.add("--plugin");
        arguments.add("pretty:" + outputPath + "/cucumber-pretty.txt");
        arguments.add("--plugin");
        arguments.add("json:" + targetJson);
        arguments.add("--plugin");
        arguments.add("rerun:" + targetRerun);
        String[] gluepackages = glue.split(",");
        for (String packages : gluepackages) {
            if (!packages.contains("none")) {
                arguments.add("--glue");
                arguments.add(packages);
            }
        }
        final String[] argv = arguments.toArray(new String[0]);
        try {
            executetests(argv);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public byte executetests(final String[] argv) throws InterruptedException, IOException {

        ExtendedRuntimeOptions runtimeOptions = new ExtendedRuntimeOptions(new ArrayList(Arrays.asList(argv)));
        MultiLoader resourceLoader = new MultiLoader(this.getClass().getClassLoader());
        ResourceLoaderClassFinder classFinder = new ResourceLoaderClassFinder(resourceLoader, this.getClass().getClassLoader());
        Runtime runtime = new Runtime(resourceLoader, classFinder, this.getClass().getClassLoader(), runtimeOptions);
        runtime.run();
        System.out.println(runtime.exitStatus());
        return runtime.exitStatus();

    }

    public static void GenerateAllRunReports() {

        try {
            List<File> jsons = finder("target/cucumber-parallel/");
            List<File> defaultRunJSONs = new ArrayList<File>();
            List<File> firstRunJSONs = new ArrayList<File>();
            List<File> secondRunJSONs = new ArrayList<File>();
            List<File> thirdRunJSONs = new ArrayList<File>();
            List<File> fourthRunJSONs = new ArrayList<File>();
            List<File> fifthRunJSONs = new ArrayList<File>();
            for (File f : jsons) {
                if (f.getName().contains("cucumber1") && f.getName().endsWith(".json")) {
                    firstRunJSONs.add(f);
                } else if (f.getName().contains("cucumber2") && f.getName().endsWith(".json")) {
                    secondRunJSONs.add(f);
                } else if (f.getName().contains("cucumber3") && f.getName().endsWith(".json")) {
                    thirdRunJSONs.add(f);
                } else if (f.getName().contains("cucumber4") && f.getName().endsWith(".json")) {
                    fourthRunJSONs.add(f);
                } else if (f.getName().contains("cucumber5") && f.getName().endsWith(".json")) {
                    fifthRunJSONs.add(f);
                } else {
                    defaultRunJSONs.add(f);
                }
            }

            if (defaultRunJSONs.size() != 0) {
                generateRunWiseReport(defaultRunJSONs, "Default_Run");
            }
            if (firstRunJSONs.size() != 0) {
                generateRunWiseReport(firstRunJSONs, "First_Re-Run");
            }
            if (secondRunJSONs.size() != 0) {
                generateRunWiseReport(secondRunJSONs, "Second_Re-Run");
            }
            if (thirdRunJSONs.size() != 0) {
                generateRunWiseReport(thirdRunJSONs, "Third_Re-Run");
            }
            if (fourthRunJSONs.size() != 0) {
                generateRunWiseReport(fourthRunJSONs, "Fourth_Re-Run");
            }
            if (fifthRunJSONs.size() != 0) {
                generateRunWiseReport(fifthRunJSONs, "Fifth_Re-Run");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<File> finder(String dirName) {
        return (List<File>) FileUtils.listFiles(new File(dirName), new String[] {"json"}, true);
    }

    public static void generateRunWiseReport(List<File> jsons, String run) {
        try {
            File rd = new File("target/cucumber-parallel/Consolidated-Report/" + run);
            List<String> jsonReports = new ArrayList<String>();
            for (File json : jsons) {
                jsonReports.add(json.getAbsolutePath());
            }
            //List<String> jsonReports, File reportDirectory, String pluginUrlPath, String buildNumber, String buildProject, boolean skippedFails, boolean undefinedFails, boolean flashCharts, boolean runWithJenkins, boolean artifactsEnabled, String artifactConfig, boolean highCharts
            ReportBuilder reportBuilder = new ReportBuilder(jsonReports, rd, "", run, "cucumber-reporting", true, true, true, false, false, "", false);
            reportBuilder.generateReports();
            System.out.println(run + " consolidated reports are generated under directory target/cucumber-parallel/Consolidated-Report");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
