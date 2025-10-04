package io.github.reserveword.imblocker.common.jnastructs;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;

@FieldOrder({"left", "top", "right", "bottom"})
public class RECT extends Structure {
    public int left;
    public int top;
    public int right;
    public int bottom;
    
    @Override
    protected List<String> getFieldOrder() {
    	return Arrays.asList("left", "top", "right", "bottom");
    }
}
