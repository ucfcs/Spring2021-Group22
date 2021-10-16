package ch.heap.bukkit.epilog.meevent;

public enum SpecialItemActions {
    USE_ESCAPE_ROPE("use_escape_rope"),
    USE_REVEAL_PLAYERS("use_reveal_players"),
    USE_GLOW_PATH("use_glow_path")
    ;

    public final String action;
    private SpecialItemActions(String action) {
        this.action = action;
    }
}
