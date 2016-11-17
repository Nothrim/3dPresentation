package com.nothrim;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

import java.util.*;

/**
 * Created by Notrim on 17.11.2016.
 */
public class ColorPicker {
    public static List<Color> colors = Arrays.asList(Color.BLACK,Color.BLUE,Color.BROWN,Color.FIREBRICK,Color.FOREST,
            Color.WHITE,Color.YELLOW,Color.VIOLET,Color.TEAL,Color.TAN,Color.SLATE,Color.SKY,Color.SCARLET,Color.SALMON
            ,Color.ROYAL);
    private static Map<ColorAttribute,Integer> colorReceivers = new HashMap<>();
    public static void clear(){
        colorReceivers = new HashMap<>();
    }
    public static void paint(ColorAttribute colorAttribute){
        if(!colorReceivers.containsKey(colorAttribute))
            colorReceivers.put(colorAttribute,0);
        int index = colorReceivers.get(colorAttribute);
        colorAttribute.color.set(colors.get(index));
        colorReceivers.put(colorAttribute,(index+1)%colors.size());
    }
}
