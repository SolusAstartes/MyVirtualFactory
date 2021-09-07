package Factory;
import Factory.utils.Assembler;
import Factory.utils.Dissector;
import Factory.utils.Machine;

import java.awt.*;
import java.io.*;
import java.net.*;
public class PlannerClient {


    public static void main(String argv[]) throws Exception
    {
        Socket clientSocket = new Socket("127.0.0.1", 54000);

        try {
            new  ServerHandlerForPlannerClient(clientSocket).run();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}

class ServerHandlerForPlannerClient  implements Runnable{
    String userName;
    String password;
    String workId;
    String workType;
    String machineId;
    float workQuantity;
    Socket socketOfClient;//socket of a machine which talks to a server
    Dissector messageDissector;
    Assembler messageAssembler;
    BufferedReader inputFromUser;
    DataInputStream inputFromServer;
    DataOutputStream outToServer;
    Machine machine;
    Boolean loggedIn=false;
    public ServerHandlerForPlannerClient(Socket socketOfClient) {
        this.socketOfClient = socketOfClient;
        this.messageDissector = new Dissector();
        this.messageAssembler=new Assembler();
    }
    @Override
    public void run(){
        try {
             inputFromUser = new BufferedReader(new InputStreamReader(System.in));
            inputFromServer=new DataInputStream(new BufferedInputStream(socketOfClient.getInputStream()));
            outToServer = new DataOutputStream(new BufferedOutputStream(socketOfClient.getOutputStream()));
            while(true) {
                if (loggedIn == false) {
                    requestCredentials();
                } else {
                    printMenu();
                }
            }
        }
        catch (IOException e) {
            System.out.println("Error from Clients ServerHandler: " + e);
        }
    }

    public void requestCredentials() throws IOException {
        System.out.println("Welcome, please provide your credentials");
        System.out.println("-------------------------\n");
        System.out.println("1 -Enter your username : ");
        userName=inputFromUser.readLine();
        System.out.println("2 - enter your password : ");
        password=inputFromUser.readLine();
        processSelection(0);
    }

    public void printMenu() throws IOException{
        int selection;
        System.out.println("Welcome, select your operation from menu");
        System.out.println("-------------------------\n");
        System.out.println("1 - Create a work order");
        System.out.println("2 - Request status of a machine and work done by that machine");
        System.out.println("3 - Request list of the machines by type");
        System.out.println("4 - Request list of the waiting orders by type ");
        System.out.println("5 - Logout");
        selection=Integer.parseInt(inputFromUser.readLine());
        processSelection(selection);
    }

    public void processSelection(int selection)throws IOException{
        messageAssembler=new Assembler();
        switch (selection){
            case 0:
                outToServer=messageAssembler.assembleAuthMessage(outToServer,userName,password);
                outToServer.flush();
                processMessagesFromServer(inputFromServer);
                break;
            case 1:
                System.out.println("Please provide necessary information for work order");
                System.out.println("-------------------------\n");
                System.out.println("Work Type");
                workType=inputFromUser.readLine();
                System.out.println("Work Quantity -Enter As  Number-");
                workQuantity= Float.parseFloat(inputFromUser.readLine());
                System.out.println("Work Id");
                workId=inputFromUser.readLine();
                outToServer=messageAssembler.assembleWorkOrder(outToServer,workType,workQuantity,workId);
                outToServer.flush();
                processMessagesFromServer(inputFromServer);
                break;

            case 2:
                System.out.println("Please provide necessary information for machine to learn its status and previous works done by that machine");
                System.out.println("-------------------------\n");
                System.out.println("Enter Id of the machine");
                machineId=inputFromUser.readLine();
                outToServer=messageAssembler.assembleStatusRequestFromClient(outToServer,machineId);
                outToServer.flush();
                processMessagesFromServer(inputFromServer);
                break;
            case 3:

                //BUNU KOYDUM AMA DOĞRUDAN SERVER HANDLERA COMMAND ID PASLANACAK
                System.out.println("Requesting list of the machines by type");
                System.out.println("-------------------------\n");
                outToServer=messageAssembler.assembleListOfMachinesRequest(outToServer);
                outToServer.flush();
                processMessagesFromServer(inputFromServer);
                break;
            case 4:

                //BUNU KOYDUM AMA DOĞRUDAN REQUEST HANDLERA COMMAND ID PASLANACAK
                System.out.println("Requesting list of the waiting orders by type");
                System.out.println("-------------------------\n");
                outToServer=messageAssembler.assembleListOfWaitingOrdersRequest(outToServer);
                outToServer.flush();
                processMessagesFromServer(inputFromServer);

                break;
            case 5:
                outToServer=messageAssembler.assembleLogOutMessage(outToServer,userName);
                System.out.println("Logged out");
                loggedIn=false;
                //LOGOUT COMMANDLA USERNAME GÖNDERİLECEĞİ İÇİN SERVER HANDLERA USERNAME İLE COMMAND ID PASLA
                break;


        }

    }

    public void processMessagesFromServer(DataInputStream inputFromServer) throws IOException {
        int messageType = inputFromServer.readInt();
        switch (messageType) {
            case 1://RESPONSE FROM SERVER AUTH ACCEPTED WITH NO BODY OR DATA --NO BODY
        loggedIn=true;
                break;
            case 2://RESPONSE FROM SERVER WRONG CREDENTIAL ERROR --NO BODY
                loggedIn=false;
                System.out.println("Wrong Credentials");


                break;
            case 4://RESPONSE FROM SERVER ALREADY LOGGED IN --NO BODY
                System.out.println("already logged in");
                break;


            case 11://WORK ORDER ACCEPTED MESSAGE FROM SERVER TO CLIENT
            String acceptanceMessage=messageDissector.dissectStatusRequestFromClient(inputFromServer);
            System.out.println(acceptanceMessage);
                break;
            case 20://STATUS REQUEST FROM CLIENT TO SERVER

                break;
            case 21://STATUS RESPONSE FROM SERVER TO CLIENT
                String[] machineStatusWithWorks= messageDissector.dissectStatusResponseFromServer(inputFromServer);
                System.out.println(machineStatusWithWorks[0]);
                System.out.println(machineStatusWithWorks[1]);
                break;
            case 30://STATUS REQUEST FROM SERVER TO MACHINE -- NO BODY
                break;
            case 31://STATUS RESPONSE FROM MACHINE TO SERVER

                break;
            case 40://LIST REQUEST FROM PLANNER CLIENT TO SERVER FOR LIST OF MACHINES BY TYPE --NO BODY
                break;
            case 41://LIST REQUEST FROM PLANNER CLIENT TO SERVER FOR LIST OF MACHINES BY TYPE -- NO BODY
                break;
            case 50://LIST RESPONSE FROM SERVER TO PLANNER CLIENT WITH LIST OF MACHINES BY TYPE
               String listOfMachinesByType= messageDissector.dissectListOfMachinesByType(inputFromServer);
               System.out.println(listOfMachinesByType);
                break;
            case 51://LIST RESPONSE FROM SERVER TO PLANNER CLIENT WITH LIST OF WAITING ORDERS BY TYPE
                String listOfWaitingOrdersByType= messageDissector.dissectListOfWaitingOrdersByType(inputFromServer);
                System.out.println(listOfWaitingOrdersByType);
                break;
            case 60://WORK ORDER TO MACHINE FROM SERVER
                messageDissector.dissectWorkOrderToMachine(inputFromServer);
                break;
            case 61://WORK RESULT FROM MACHINE TO SERVER
                messageDissector.dissectWorkOrderToMachine(inputFromServer);
                break;
            case 90://CHECK IN BY MACHINE TO SERVER
                messageDissector.dissectCheckInByMachine(inputFromServer);
                outToServer=messageAssembler.assembleCheckInAckFromServer(outToServer);
                break;



        }
    }
}
