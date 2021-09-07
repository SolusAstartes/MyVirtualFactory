package Factory.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerDatabase {
    public static ArrayList<Work> unAssignedWorks = new ArrayList<>();
    public static ArrayList<Machine> activeMachines = new ArrayList<>();
    public static Map <String, String> userDatabase = new ConcurrentHashMap<>();
    public static ArrayList<String> activeUsers=new ArrayList<>();
    public static Map <String, List<Work>> worksDoneByMachines = new ConcurrentHashMap<>();
    public static Map <Machine,Work> workAssignedToMachine = new ConcurrentHashMap<>();
}
