package ch.heap.bukkit.epilog.event;

public enum DoFarmType {
    PLANT("plant"),
    HARVEST("harvest");

    public final String type;
    private DoFarmType(String type) {
        this.type = type;
    }
}
