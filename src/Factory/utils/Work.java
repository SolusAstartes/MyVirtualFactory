package Factory.utils;

public class Work {
    private String workId;
    private int MachineId;
    private String workType;
    private float workQuantity;
    private boolean isAcccepted=false;
    private boolean isItDone=false;
/*
    public Work(String workId,int workType,float workQuantity){
        this.setWorkId(workId);
        this.setWorkType(workType);
        this.setWorkQuantity(workQuantity);
    }

*/
    public String getWorkId() {
        return workId;
    }

    public void setWorkId(String workId) {
        this.workId = workId;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public float getWorkQuantity() {
        return workQuantity;
    }

    public void setWorkQuantity(float workQuantity) {
        this.workQuantity = workQuantity;
    }

    public boolean isAcccepted() {
        return isAcccepted;
    }

    public void setAcccepted(boolean acccepted) {
        isAcccepted = acccepted;
    }

    public int getMachineId() {
        return MachineId;
    }

    public void setMachineId(int machineId) {
        MachineId = machineId;
    }

    public boolean isItDone() {
        return isItDone;
    }

    public void setItDone(boolean itDone) {
        isItDone = itDone;
    }
}
