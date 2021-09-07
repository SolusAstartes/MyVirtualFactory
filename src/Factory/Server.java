package Factory;

import Factory.utils.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server {


    public static void main(String argv[]) throws Exception {
        ServerDatabase serverDB = new ServerDatabase();
        serverDB.userDatabase.put("isot", "123");
        serverDB.userDatabase.put("dogukan", "123");
        serverDB.userDatabase.put("samed", "123");

        ServerSocket welcomeSocket = new ServerSocket(54000);
        ExecutorService pool = Executors.newFixedThreadPool(5);

        while (true) {
            try {
                pool.execute(new ClientHandler(welcomeSocket.accept(), serverDB));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }


        }
    }


}

class ClientHandler implements Runnable {
    Socket clientSocket;
    Work workOrderFromClient;
    Dissector messageDissector;
    Assembler messageAssembler;
    DataInputStream inFromClient;
    DataOutputStream outToClient;
    boolean userExistsInDatabase;
    Machine checkedInMachine;
    boolean userLoggedIn;
    ServerDatabase serverDB;

    public ClientHandler(Socket SocketFromServer, ServerDatabase serverDB) {
        this.clientSocket = SocketFromServer;
        this.messageDissector = new Dissector();
        this.messageAssembler = new Assembler();
        this.serverDB = serverDB;
    }

    public boolean authenticateUser(String[] userCredentials) {
        //UserCredentials[1] contains password and checks it if it same with password stored in hasmap db
        return userCredentials[1].equals(serverDB.userDatabase.get(userCredentials[0]));
    }

    public boolean checkLoggedInUsers(String[] userCredentials) {
        return serverDB.activeUsers.contains(userCredentials[0]);
    }

    public void processInput(DataInputStream InputFromClients) throws IOException {
        int messageType = InputFromClients.readInt();
        System.out.println(messageType);
        switch (messageType) {
            case 0://CLIENT WANTS TO LOGIN AND SENDS AUTH TYPE MESSAGE WITH DATA
                String[] userCredentials = messageDissector.dissectAuthMessage(InputFromClients);
                userExistsInDatabase = authenticateUser(userCredentials);
                userLoggedIn = checkLoggedInUsers(userCredentials);
                if (userExistsInDatabase & !userLoggedIn) {
                    outToClient = messageAssembler.assembleAuthAcceptedMessage(outToClient);
                    serverDB.activeUsers.add(userCredentials[0]);
                } else if (!userExistsInDatabase) {
                    outToClient = messageAssembler.assembleAuthRejectedMessage(outToClient);
                } else if (userExistsInDatabase & userLoggedIn) {
                    outToClient = messageAssembler.assembleAlreadyLoggedInMessage(outToClient);
                }
                outToClient.flush();
                break;
            case 3://LOGOUT REQUEST FROM CLIENT
                String loggingOutUser = messageDissector.dissectLogOutMessage(InputFromClients);
                serverDB.activeUsers.remove(loggingOutUser);
                break;
            case 10://WORK ORDER FROM CLIENT TO SERVER
                workOrderFromClient = messageDissector.dissectWorkOrder(InputFromClients);
                outToClient = messageAssembler.assemleWorkOrderAcceptedMessage(outToClient, workOrderFromClient.getWorkId());
                outToClient.flush();
                processWorkOrder(workOrderFromClient);
                checkAndAssignWorkOrdersToMachine();
                break;
            case 20://STATUS REQUEST FROM CLIENT TO SERVER
                String machineId = messageDissector.dissectStatusRequestFromClient(InputFromClients);
                String statusOfMachine = getMachineStatus(machineId);
                String workDoneByThatMachine = getWorkDoneByMachine(machineId);
                outToClient = messageAssembler.assembleStatusResponseFromServer(outToClient, statusOfMachine, workDoneByThatMachine);
                outToClient.flush();

                break;
            case 21://STATUS RESPONSE FROM SERVER TO CLIENT
                messageDissector.dissectStatusResponseFromServer(InputFromClients);
                break;

            case 40://LIST REQUEST FROM PLANNER CLIENT TO SERVER FOR LIST OF MACHINES BY TYPE --NO BODY
                String listOfMachinesByType = getListOfMachinesByType();
                outToClient = messageAssembler.assembleListOfMachinesByType(outToClient, listOfMachinesByType);
                outToClient.flush();
                break;
            case 41://LIST REQUEST FROM PLANNER CLIENT TO SERVER FOR LIST OF WAITING ORDERS BY TYPE -- NO BODY
                String listOfWaitingOrdersByType = getListOfWaitingOrdersByType();
                outToClient = messageAssembler.assembleListOfMachinesByType(outToClient, listOfWaitingOrdersByType);
                outToClient.flush();
                break;
            case 61://WORK RESULT FROM MACHINE TO SERVER
                String machineStatus = messageDissector.dissectWorkResultFromMachine(InputFromClients);
                checkedInMachine.setMachineStatus(machineStatus);
                addToWorksDoneByMachine(checkedInMachine);
                checkAndAssignWorkOrdersToMachine();
                break;
            case 90://CHECK IN BY MACHINE TO SERVER
                checkedInMachine = messageDissector.dissectCheckInByMachine(InputFromClients);
                checkedInMachine.setMachineSocket(outToClient);
                serverDB.activeMachines.add(checkedInMachine);
                outToClient = messageAssembler.assembleCheckInAckFromServer(outToClient);
                outToClient.flush();
                checkAndAssignWorkOrdersToMachine();
                break;
        }
    }

    public String getMachineStatus(String machineId) {
        String MachineStatus="There is no machine with that ID";

        if (serverDB.activeMachines.size() == 0) {
            MachineStatus = "There is no active machine at the moment";

        } else {
            for (int i = 0; i < serverDB.activeMachines.size(); i++) {
                if (serverDB.activeMachines.get(i).getMachineId().equals(machineId) ) {
                    MachineStatus = "Status of the machine is : "+serverDB.activeMachines.get(i).getMachineStatus();
                }
            }
        }
        return MachineStatus;
    }

    public String getWorkDoneByMachine(String machineId) {
        List<Work> workDoneByMachine = serverDB.worksDoneByMachines.get(machineId);
        if (serverDB.worksDoneByMachines.size() == 0) {
            return "There is no work done by that machine \n" + "-------------------------\n";
        } else {
            String worksofMachine = "WORKS DONE BY MACHINE WITH ID OF : " + machineId + " \n";
            String cncWorks = "ORDERS WITH TYPE OF CNC \n";
            String paintWorks = "ORDERS WITH TYPE OF PAINT \n";
            String cutWorks = "ORDERS WITH TYPE OF CUT \n";
            for (Work unassignedWork : workDoneByMachine) {
                String workType = unassignedWork.getWorkType();
                if (workType.equals("CNC")) {
                    cncWorks += "Work Id: " + unassignedWork.getWorkId() + "\n";
                    cncWorks += "Work Quantity: " + unassignedWork.getWorkQuantity() + "\n";
                    cncWorks += "-------------------------\n";


                } else if (workType.equals("PAINT")) {
                    paintWorks += "Work Id: " + unassignedWork.getWorkId() + "\n";
                    paintWorks += "Work Quantity: " + unassignedWork.getWorkQuantity() + "\n";
                    paintWorks += "-------------------------\n";

                } else if (workType.equals("CUT")) {
                    cutWorks += "Work Id: " + unassignedWork.getWorkId() + "\n";
                    cutWorks += "Work Quantity: " + unassignedWork.getWorkQuantity() + "\n";
                    cutWorks += "-------------------------\n";

                }
            }
            return worksofMachine + cncWorks + paintWorks + cutWorks;
        }


    }

    public String getListOfWaitingOrdersByType() {
        if (serverDB.unAssignedWorks.size() == 0) {
            return "There is no waiting order at the moment \n" + "-------------------------\n";
        } else {
            String cncWorks = "ORDERS WITH TYPE OF CNC \n";
            String paintWorks = "ORDERS WITH TYPE OF PAINT \n";
            String cutWorks = "ORDERS WITH TYPE OF CUT \n";
            for (Work unassignedWork : serverDB.unAssignedWorks) {
                String workType = unassignedWork.getWorkType();
                if (workType.equals("CNC")) {
                    cncWorks += "Work Id: " + unassignedWork.getWorkId() + "\n";
                    cncWorks += "Work Quantity: " + unassignedWork.getWorkQuantity() + "\n";
                    cncWorks += "-------------------------\n";


                } else if (workType.equals("PAINT")) {
                    paintWorks += "Work Id: " + unassignedWork.getWorkId() + "\n";
                    paintWorks += "Work Quantity: " + unassignedWork.getWorkQuantity() + "\n";
                    paintWorks += "-------------------------\n";

                } else if (workType.equals("CUT")) {
                    cutWorks += "Work Id: " + unassignedWork.getWorkId() + "\n";
                    cutWorks += "Work Quantity: " + unassignedWork.getWorkQuantity() + "\n";
                    cutWorks += "-------------------------\n";

                }
            }
            return cncWorks + paintWorks + cutWorks;
        }
    }

    public String getListOfMachinesByType() {
        if (serverDB.activeMachines.size() == 0) {
            return "There is no active machine at the moment. \n" + "-------------------------\n";
        } else {
            String cncMachines = "MACHINES WITH TYPE OF CNC \n";
            String paintMachines = "MACHINES WITH TYPE OF PAINT \n";
            String cutMachines = "MACHINES WITH TYPE OF CUT \n";
            for (Machine activeMachine : serverDB.activeMachines) {
                String machineType = activeMachine.getMachineType();
                if (machineType.equals("CNC")) {
                    cncMachines += "Machine Id: " + activeMachine.getMachineId() + "\n";
                    cncMachines += "Machine Name: " + activeMachine.getMachineName() + "\n";
                    cncMachines += "Machine Speed: " + activeMachine.getMachineSpeed() + "\n";
                    cncMachines += "-------------------------\n";
                } else if (machineType.equals("PAINT")) {
                    paintMachines += "Machine Id: " + activeMachine.getMachineId() + "\n";
                    paintMachines += "Machine Name: " + activeMachine.getMachineName() + "\n";
                    paintMachines += "Machine Speed: " + activeMachine.getMachineSpeed() + "\n";
                    paintMachines += "-------------------------\n";
                } else if (machineType.equals("CUT")) {
                    cutMachines += "Machine Id: " + activeMachine.getMachineId() + "\n";
                    cutMachines += "Machine Name: " + activeMachine.getMachineName() + "\n";
                    cutMachines += "Machine Speed: " + activeMachine.getMachineSpeed() + "\n";
                    cutMachines += "-------------------------\n";
                }
            }
            return cncMachines + paintMachines + cutMachines;
        }
    }

    public void processWorkOrder(Work workOrderFromClient) {
        serverDB.unAssignedWorks.add(workOrderFromClient);
    }

    public void checkAndAssignWorkOrdersToMachine() throws IOException {
        DataOutputStream outToMachine;
        ArrayList<Work> tempWork = new ArrayList<>();
        if (serverDB.unAssignedWorks.size() != 0 && serverDB.activeMachines.size() != 0) {
            tempWork = copyList(serverDB.unAssignedWorks);
            for (Work unassignedWork : tempWork) {
                for (Machine activeMachine : serverDB.activeMachines) {

                    if (activeMachine.getMachineStatus().equals("EMPTY")) {
                        if (unassignedWork.getWorkType().equals(activeMachine.getMachineType())) {
                            activeMachine.setMachineStatus("BUSY");
                            serverDB.unAssignedWorks.remove(unassignedWork);
                            serverDB.workAssignedToMachine.put(activeMachine, unassignedWork);
                            outToMachine = messageAssembler.assembleWorkOrdersToMachine(activeMachine.getMachineSocket(), unassignedWork.getWorkQuantity());
                            outToMachine.flush();
                        }
                    }
                }
            }
        }
    }

    public ArrayList<Work> copyList(ArrayList<Work> unAssignedWorks) {
        ArrayList<Work> tempWork = new ArrayList<>();
        for (int i = 0; i < unAssignedWorks.size(); i++) {
            tempWork.add(unAssignedWorks.get(i));
        }
        return tempWork;
    }

    public void addToWorksDoneByMachine(Machine checkedInMachine) {
        List<Work> worksDoneByMachine = serverDB.worksDoneByMachines.get(checkedInMachine.getMachineId());
        Work WorkAssignedToMachine = serverDB.workAssignedToMachine.get(checkedInMachine);
        if (worksDoneByMachine == null) {
            worksDoneByMachine = new ArrayList<Work>();

        }
        worksDoneByMachine.add(WorkAssignedToMachine);
        serverDB.worksDoneByMachines.put(checkedInMachine.getMachineId(), worksDoneByMachine);
        serverDB.workAssignedToMachine.remove(checkedInMachine);
    }

    @Override
    public void run() {

        try {
            inFromClient = new DataInputStream((clientSocket.getInputStream()));
            outToClient = new DataOutputStream(clientSocket.getOutputStream());
            while (true) {
                processInput(inFromClient);
            }
        } catch (IOException e) {
            System.out.println("Error from ClientHandler: " + e);
        }
    }


}
