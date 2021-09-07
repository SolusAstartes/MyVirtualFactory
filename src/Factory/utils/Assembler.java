package Factory.utils;

import java.io.DataOutputStream;
import java.io.IOException;

public class Assembler {
    DataOutputStream outputMessage;
    public DataOutputStream assembleAuthMessage(DataOutputStream outputMessage,String userName,String password)  throws IOException {
        outputMessage.writeInt(0);
        outputMessage.writeUTF(userName);
        outputMessage.writeUTF(password);
        return  outputMessage;
    }
    public DataOutputStream assembleAuthAcceptedMessage(DataOutputStream outputMessage)  throws IOException {
        outputMessage.writeInt(1);
        return  outputMessage;
    }
    public DataOutputStream assembleAuthRejectedMessage(DataOutputStream outputMessage)  throws IOException {
        outputMessage.writeInt(2);
        return  outputMessage;
    }

    public DataOutputStream assembleLogOutMessage(DataOutputStream outputMessage,String userName)  throws IOException {
        outputMessage.writeInt(3);
        outputMessage.writeUTF(userName);
        return  outputMessage;
    }
    public DataOutputStream assembleAlreadyLoggedInMessage(DataOutputStream outputMessage)  throws IOException {
        outputMessage.writeInt(4);
        return  outputMessage;
    }
    public DataOutputStream assembleWorkOrder(DataOutputStream outputMessage,String workType,float workQuantity,String workId)  throws IOException{
        outputMessage.writeInt(10);
        outputMessage.writeUTF(workType);
        outputMessage.writeFloat(workQuantity);
        outputMessage.writeUTF(workId);
        return  outputMessage;

    }
    public DataOutputStream assemleWorkOrderAcceptedMessage(DataOutputStream outputMessage,String workId)  throws IOException{
        outputMessage.writeInt(11);
        outputMessage.writeUTF( "Work order: "+workId+" accepted by server, it will be executed in first available machine");
        return  outputMessage;

    }

    public DataOutputStream assembleStatusRequestFromClient(DataOutputStream outputMessage,String machineId)  throws IOException{
        outputMessage.writeInt(20);
        outputMessage.writeUTF(machineId);
        return  outputMessage;

    }
    public DataOutputStream assembleStatusResponseFromServer(DataOutputStream outputMessage,String machineStatus,String workDoneByMachine)  throws IOException{
        outputMessage.writeInt(21);
        outputMessage.writeUTF(machineStatus);
        outputMessage.writeUTF(workDoneByMachine);
        return  outputMessage;

    }

    public DataOutputStream assembleStatusResponseFromMachine(DataOutputStream outputMessage,String machineStatus)  throws IOException{
        outputMessage.writeInt(31);
        outputMessage.writeUTF(machineStatus);
        return  outputMessage;

    }

    public DataOutputStream assembleListOfMachinesRequest(DataOutputStream outputMessage)  throws IOException{
        outputMessage.writeInt(40);
        return  outputMessage;

    }
    public DataOutputStream assembleListOfWaitingOrdersRequest(DataOutputStream outputMessage)  throws IOException{
        outputMessage.writeInt(41);
        return  outputMessage;

    }

    public DataOutputStream assembleListOfMachinesByType(DataOutputStream outputMessage,String listOfMachinesByType)  throws IOException{
        outputMessage.writeInt(50);

        outputMessage.writeUTF(listOfMachinesByType);
        return  outputMessage;

    }
    public DataOutputStream assembleListOfWaitingOrdersByType(DataOutputStream outputMessage,String listOfWaitingOrdersByType)  throws IOException{
        outputMessage.writeInt(51);

        outputMessage.writeUTF(listOfWaitingOrdersByType);
        return  outputMessage;

    }
    public DataOutputStream assembleWorkOrdersToMachine(DataOutputStream outputMessage,float work_quantity)  throws IOException{
        outputMessage.writeInt(60);
        outputMessage.writeFloat(work_quantity);
        return  outputMessage;

    }

    public DataOutputStream assembleWorkResultFromMachine(DataOutputStream outputMessage,String machineStatus)  throws IOException{
        outputMessage.writeInt(61);
        outputMessage.writeUTF(machineStatus);
        return  outputMessage;

    }
    public DataOutputStream assembleCheckInByMachine(DataOutputStream outputMessage,String machineId,String machineName,String machineType,float machineSpeed)  throws IOException{
        outputMessage.writeInt(90);
        outputMessage.writeUTF(machineId);
        outputMessage.writeUTF(machineName);
        outputMessage.writeUTF(machineType);
        outputMessage.writeFloat(machineSpeed);
        return  outputMessage;

    }

    public DataOutputStream assembleCheckInAckFromServer(DataOutputStream outputMessage)  throws IOException{
        outputMessage.writeInt(91);
        return  outputMessage;

    }
}
