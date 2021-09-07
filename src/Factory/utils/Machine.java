package Factory.utils;

import java.io.DataOutputStream;
import java.net.Socket;

public class Machine {

    private String machineId;
    private String machineName;
    private String machineType;
    private float machineSpeed;
    private String machineStatus;
    private DataOutputStream machineOutputStream;

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getMachineType() {
        return machineType;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }

    public float getMachineSpeed() {
        return machineSpeed;
    }

    public void setMachineSpeed(float machineSpeed) {
        this.machineSpeed = machineSpeed;
    }

    public String getMachineStatus() {
        return machineStatus;
    }

    public void setMachineStatus(String machineStatus) {
        this.machineStatus = machineStatus;
    }

    public DataOutputStream getMachineSocket() {
        return machineOutputStream;
    }

    public void setMachineSocket(DataOutputStream machineSocket) {
        this.machineOutputStream = machineSocket;
    }
}
