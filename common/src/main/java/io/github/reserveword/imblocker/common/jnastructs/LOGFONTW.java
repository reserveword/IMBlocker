package io.github.reserveword.imblocker.common.jnastructs;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;
import com.sun.jna.Structure.FieldOrder;
import com.sun.jna.win32.W32APITypeMapper;

@FieldOrder({"lfHeight", "lfWidth", "lfEscapement", "lfOrientation",
            "lfWeight", "lfItalic", "lfUnderline", "lfStrikeOut",
            "lfCharSet", "lfOutPrecision", "lfClipPrecision",
            "lfQuality", "lfPitchAndFamily", "lfFaceName"})
public class LOGFONTW extends Structure {
	
    public int lfHeight;
    public int lfWidth;
    public int lfEscapement;
    public int lfOrientation;
    public int lfWeight;
    public byte lfItalic;
    public byte lfUnderline;
    public byte lfStrikeOut;
    public byte lfCharSet;
    public byte lfOutPrecision;
    public byte lfClipPrecision;
    public byte lfQuality;
    public byte lfPitchAndFamily;
    public char[] lfFaceName = new char[32];

    public LOGFONTW() {
        super(W32APITypeMapper.UNICODE);
    }

    @Override
    protected List<String> getFieldOrder() {
        return Arrays.asList(
            "lfHeight", "lfWidth", "lfEscapement", "lfOrientation",
            "lfWeight", "lfItalic", "lfUnderline", "lfStrikeOut",
            "lfCharSet", "lfOutPrecision", "lfClipPrecision",
            "lfQuality", "lfPitchAndFamily", "lfFaceName"
        );
    }
}