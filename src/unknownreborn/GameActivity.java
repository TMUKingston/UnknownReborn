/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package unknownreborn;

import java.awt.Point;
import java.awt.image.BufferedImage;

/**
 *
 * The Interface for an Activity (or State) in the game
 * 
 */
public interface GameActivity {

    /**
     *
     * @param in das BufferedImage über das drüber gemalt werden soll (damit die größe schon feststeht)
     * @return
     */
    public abstract BufferedImage render(BufferedImage in);
    public abstract void onEnter();
    public abstract void onExit();
    
    /**
     * Eine default-activity, komplet schwarz
     */
    public static final GameActivity ACTIVITY_EMPTY = new GameActivity() {
        @Override
        public BufferedImage render(BufferedImage in) {
            return in;
        }

        @Override
        public void onEnter() {
        }

        @Override
        public void onExit() {
        }
    };
}
