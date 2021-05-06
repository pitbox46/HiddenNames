package github.pitbox46.hiddennames.network;

public class BooleanPacket {
    public enum Type {
        SET_ALL
    }
    public final Type type;
    public final boolean bool;
    public BooleanPacket(Type type, boolean bool) {
        this.type = type;
        this.bool = bool;
    }
}
