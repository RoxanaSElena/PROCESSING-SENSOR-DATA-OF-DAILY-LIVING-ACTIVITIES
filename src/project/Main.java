package project;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    static final String inputFile = "Activities.txt";
    static final String outputFile = "Statistics.txt";
    static final Path inPath = Paths.get(inputFile);
    static final Path outPath = Paths.get(outputFile);
    static List<MonitoredData> dataList = null;

    private List<MonitoredData> generateList(){
        try {
            return Files.lines(inPath)
                    .map(s -> s.split("		"))
                    .map(s -> {
                        s[0] = s[0].replace(" ", "T");
                        s[1] = s[1].replace(" ", "T");
                        MonitoredData d = new MonitoredData(LocalDateTime.parse(s[0].replace(" ", "T")), LocalDateTime.parse(s[1]), s[2].trim());
                        return d;
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int numberOfMonitoredDays() {
        int nrOfDays = 0;
        nrOfDays = dataList.stream()
                .collect(Collectors.mapping(
                        MonitoredData::getDate,
                        Collectors.toSet())).size();
        return nrOfDays;
    }

    private Map<String, Integer> numberOfActivities() {
        Map<String, Integer> map = new HashMap<>();
        map = dataList.stream()
                .collect(
                        Collectors.groupingBy(
                                MonitoredData::getActivity,
                                Collectors.collectingAndThen(
                                        Collectors.mapping(e->{return e.getActivity();}, Collectors.counting()), Long::intValue)));
        return map;
    }

    private Map<String, Map<String, Integer>> numberOfActivitiesPerDay(){
        Map<String, Map<String, Integer>> map = dataList.stream()
                .collect(
                        Collectors.groupingBy(
                                MonitoredData::getDate,
                                Collectors.groupingBy(
                                        MonitoredData::getActivity,
                                        Collectors.collectingAndThen(
                                                Collectors.mapping(MonitoredData::getActivity, Collectors.counting()), Long::intValue))));
        return map;
    }

    private List<String> activityDuration(){
        List<String> map = dataList.stream()
                .map(e -> {
                    return e.toString() + " " + e.getDuration();
                })
                .collect(Collectors.toList());
        return map;
    }

    private Map<String, Integer> entireActivityDuration(){
        Map<String, Integer> map = dataList.stream()
                .collect(
                        Collectors.groupingBy(
                                MonitoredData::getActivity,
                                Collectors.collectingAndThen(
                                        Collectors.mapping(MonitoredData::getDuration, Collectors.summingInt(e -> e.intValue())), e-> e.intValue())
                        ))
                .entrySet().stream()
                .filter(e-> e.getValue().intValue() < 600)
                .collect(Collectors.toMap(x->x.getKey(), x->x.getValue()));
        return map;
    }

    private List<String> longActivities(){
        List<String> list = dataList.stream()
                .collect(Collectors.groupingBy(
                        MonitoredData::getActivity,
                        Collectors.toSet())).entrySet().stream()
                .map(activity -> {
                    int underFive = 0;
                    for(MonitoredData data: activity.getValue()) {
                        if(data.getDuration() < 5) underFive++;
                    }
                    if(underFive > activity.getValue().size()*90/100) return activity.getKey();
                    return null;
                })
                .filter(e -> e != null)
                .collect(Collectors.toList());
        return list;
    }

    public static void main(String[] args) {
        Main main = new Main();
        dataList  = main.generateList();
        int nrOfMonitoredDays = main.numberOfMonitoredDays();
        Map<String, Integer> eachActivityMap = main.numberOfActivities();
        Map<String, Map<String, Integer>> eachDayActivityMap = main.numberOfActivitiesPerDay();
        List<String> activityDurationMap = main.activityDuration();
        Map<String, Integer> totalActivityDurationMap = main.entireActivityDuration();
        List<String> longActivitiesList = main.longActivities();
        try {
            BufferedWriter writer = Files.newBufferedWriter(outPath, StandardCharsets.UTF_8);

            writer.write("-----------------------------\nNumber of monitored days: \n" + nrOfMonitoredDays + "\n");

            writer.write("------------------------------\nHow many times each activity is performed: \n");
            eachActivityMap.forEach((str, integer) -> {
                try {
                    writer.write(str + ": " + integer);
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.newLine();

            writer.write("-------------------------\nHow many times each activity is performed each day: \n");
            eachDayActivityMap.forEach((dayStr, activityMap) -> {
                try {
                    writer.write("				" + dayStr + "\n");
                    activityMap.forEach((activityStr, integer) -> {
                        try {
                            writer.write(activityStr + ": " + integer);
                            writer.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
            writer.newLine();

            writer.write("-------------------------\nDuration of each recorded activity: \n");
            for(String str: activityDurationMap) {
                writer.write(str);
                writer.newLine();
            }
            writer.newLine();

            writer.write("-------------------------\nTotal duration of each activity that was performed under 10 hrs: \n");
            totalActivityDurationMap.forEach((activityStr, integer) ->{
                try {
                    writer.write(activityStr + ": " + integer);
                    writer.newLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.newLine();

            writer.write("-------------------------\nList of activities that lasted less than 5 min in 90% of the cases: \n");
            for(String s: longActivitiesList) {
                writer.write(s + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
