package tk.xhuoffice.sessbilinfo;

import java.io.Serializable;

/**
 * All information about Bilibili should implement this interface.
 */


public interface Bilinfo extends Serializable {
    
    public String toJson();

}