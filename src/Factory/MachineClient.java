package Factory;

import Factory.utils.Assembler;
import Factory.utils.Dissector;
import Factory.utils.Machine;
import Factory.utils.Work;

import java.lang.*;
import java.io.*;
import java.net.*;

public class MachineClient {


    public static void main(String argv[]) throws Exception {
        //   new BufferedReader(new InputStreamReader(System.in));
        Socket clientSocket = new Socket("127.0.0.1", 54000);
        try {
            new ServerHandlerForMachineClient(clientSocket).run();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}

class ServerHandlerForMachineClient implements Runnable {
    Work workOrder;
    Socket socketOfClient;//socket of a machine which talks to a server
    Dissector messageDissector;
    Assembler messageAssembler;
    BufferedReader inputFromUser;
    DataInputStream inputFromServer;
    DataOutputStream outToServer;
    Machine thisMachine;

    public ServerHandlerForMachineClient(Socket socketOfClient) {
        this.socketOfClient = socketOfClient;
        this.messageDissector = new Dissector();
        this.messageAssembler = new Assembler();
        this.thisMachine=new Machine();
    }

    @Override
    public void run() {
        try {
            inputFromUser = new BufferedReader(new InputStreamReader(System.in));
            inputFromServer = new DataInputStream(new BufferedInputStream(socketOfClient.getInputStream()));
            outToServer = new DataOutputStream(new BufferedOutputStream(socketOfClient.getOutputStream()));
            printAndProcessCheckInMenu();
            while (true) { //inputFromServer.available()>0 ile sürekli dönmesi engellenebilir böylece flush metodu swich case dışında çağırılır
                processMessagesFromServer(inputFromServer);
            }
        } catch (IOException e) {
            System.out.println("Error from Machines ServerHandler: " + e);
        }
    }

    public void printAndProcessCheckInMenu() throws IOException {
        System.out.println("Welcome please check in the machine");
        System.out.println("-------------------------\n");
        System.out.println("Machine Id");
        thisMachine.setMachineId(inputFromUser.readLine());
        System.out.println("Machine Name");
        thisMachine.setMachineName(inputFromUser.readLine());
        System.out.println("Machine Type -CNC or PAINT or CUT");
        thisMachine.setMachineType(inputFromUser.readLine());
        System.out.println("Machine Speed -write floats as 1.5-");
        thisMachine.setMachineSpeed(Float.parseFloat(inputFromUser.readLine()));
        thisMachine.setMachineStatus("EMPTY");
        outToServer = messageAssembler.assembleCheckInByMachine(outToServer, thisMachine.getMachineId(), thisMachine.getMachineName(), thisMachine.getMachineType(), thisMachine.getMachineSpeed());
       outToServer.flush();
        processMessagesFromServer(inputFromServer);
    }

    public void processMessagesFromServer(DataInputStream inputFromServer) throws IOException {
        int messageType = inputFromServer.readInt();
        switch (messageType) {

            case 60://WORK ORDER TO MACHINE FROM SERVER
                workOrder = messageDissector.dissectWorkOrderToMachine(inputFromServer);
                try {
                    doWork(workOrder);
                    System.out.println("Work is Done thank you for your patience");
                    outToServer=messageAssembler.assembleWorkResultFromMachine(outToServer,"EMPTY");
                    outToServer.flush();
                } catch (Exception e) {
                    System.out.println("Something Interrupted Machine and Machine gave this error: " + e.toString());
                }
                break;
            case 91://CHECK IN ACKNOWLEDGED BY SERVER
                System.out.println("Check in acknowledged by the server, wait for work orders");
                break;

        }
    }

    public void doWork(Work workOrder) throws InterruptedException {
        thisMachine.setMachineStatus("BUSY");
        System.out.println("Machine is working stand by");
        Float sleepTime = (workOrder.getWorkQuantity() / thisMachine.getMachineSpeed());
        Thread.sleep(sleepTime.longValue()*1000);
    }
}
