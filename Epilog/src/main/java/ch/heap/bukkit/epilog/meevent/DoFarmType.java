package ch.heap.bukkit.epilog.meevent;

public enum DoFarmType {
    PLANT("plant"),
    HARVEST("harvest");

    public final String type;
    private DoFarmType(String type) {
        this.type = type;
    }
}
