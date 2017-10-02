package de.monticore.lang.embeddedmontiarc.embeddedmontiarc.types;

/**
 * @author Sascha Schneiders
 */
public class StringInt {
    protected String string;
    protected int integer;

    public StringInt() {

    }

    public StringInt(String string, int integer) {
        this.string = string;

        this.integer = integer;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public int getInteger() {
        return integer;
    }

    public void setInteger(int integer) {
        this.integer = integer;
    }
}
