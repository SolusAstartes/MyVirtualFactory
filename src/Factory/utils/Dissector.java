package Factory.utils;

import java.io.*;


public class Dissector {
    Work workInstance;
    Machine machineInstance;
    public Dissector(){
        this.workInstance=new Work();
        this.machineInstance=new Machine();
    }
    // TEK TEK THROWS IOexception YAZMAK YERİNE ALTERNATİF BİR İŞLEM VAR MI?
    public String[] dissectAuthMessage(DataInputStream InputMessage)  throws IOException{
        String userName=InputMessage.readUTF();
        String password=InputMessage.readUTF();
        String[] userCredentials=new String[]{userName,password};
        return userCredentials;
    }

    public String dissectLogOutMessage(DataInputStream InputMessage)  throws IOException{
        String userName=InputMessage.readUTF();
        return userName;
    }

    public String dissectWorkOrderAcceptedMessage(DataInputStream InputMessage)  throws IOException{
        String acceptanceMessage=InputMessage.readUTF();
        return acceptanceMessage;
    }

    public Work dissectWorkOrder(DataInputStream InputMessage)  throws IOException{
        String workType=InputMessage.readUTF();
        Float workQuantity=InputMessage.readFloat();
        String workId=InputMessage.readUTF();
        workInstance.setWorkType(workType);
        workInstance.setWorkQuantity(workQuantity);
        workInstance.setWorkId(workId);
        return workInstance;

    }

    public String dissectStatusRequestFromClient(DataInputStream InputMessage)  throws IOException{
        String workId=InputMessage.readUTF();
        return workId;
    }
    public String[] dissectStatusResponseFromServer(DataInputStream InputMessage)  throws IOException{
        String machineStatus=InputMessage.readUTF();
        String workDoneByMachine= InputMessage.readUTF();
        String[] machineStatusWithWorks=new String[]{machineStatus,workDoneByMachine};
        return machineStatusWithWorks;
    }

    public String[] dissectStatusResponseFromMachine(DataInputStream InputMessage)  throws IOException{
        String machineStatus=InputMessage.readUTF();
        String workDoneByMachine= InputMessage.readUTF();
        String[] machineStatusWithWorks=new String[]{machineStatus,workDoneByMachine};
        return machineStatusWithWorks;

    }

    public String dissectListOfMachinesByType(DataInputStream InputMessage)  throws IOException{
        String listOfMachinesByType= InputMessage.readUTF();
        return listOfMachinesByType;
    }
    public String dissectListOfWaitingOrdersByType(DataInputStream InputMessage)  throws IOException{
        String listOfWaitingOrdersByType= InputMessage.readUTF();
        return listOfWaitingOrdersByType;
    }
    public Work dissectWorkOrderToMachine(DataInputStream InputMessage)  throws IOException{
        Work incomingWork=new Work();
        incomingWork.setWorkQuantity(InputMessage.readFloat());
        return incomingWork;
    }

    public String dissectWorkResultFromMachine(DataInputStream InputMessage)  throws IOException{
        String machineStatus= InputMessage.readUTF();
        return machineStatus;
    }
    public Machine dissectCheckInByMachine(DataInputStream InputMessage)  throws IOException{
        String machineId=InputMessage.readUTF();
        String machineName=InputMessage.readUTF();
        String machineType=InputMessage.readUTF();
        float machineSpeed=InputMessage.readInt();
        machineInstance.setMachineId(machineId);
        machineInstance.setMachineName(machineName);
        machineInstance.setMachineType(machineType);
        machineInstance.setMachineSpeed(machineSpeed);
        machineInstance.setMachineStatus("EMPTY");

        return  machineInstance;
    }


}
