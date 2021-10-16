package ch.heap.bukkit.epilog.meevent;

public enum MansionPuzzleType {
    LEVER_DOOR("lever_door"),
    SHARED_DOOR("shared_door"),
    HIDDEN_BARREL("hidden_barrel"),
    PIN("pin");

    public final String type;
    private MansionPuzzleType(String type) {
        this.type = type;
    }
    
}
