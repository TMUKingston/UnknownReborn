/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import unknownreborn.Map;
import util.DoublePoint;

/**
 *
 * @author Erik Brendel
 */
public class PlayerEntity extends Entity {

    public static PlayerEntity createNew() {
        PlayerEntity pe = new PlayerEntity();
        pe.setCollisionComponent(new CollisionComponent() {

            @Override
            public DoublePoint getCollisionBoxStart() {
                return new DoublePoint(-0.495, -0.495);
            }

            @Override
            public DoublePoint getCollisionBoxEnd() {
                return new DoublePoint(0.495, 0.2);
            }

        });
        return pe;
    }

    public PlayerEntity() {
        setLocation(new DoublePoint(9, 9));
    }

    @Override
    public BufferedImage getImage() {
        BufferedImage img = new BufferedImage(32, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 20, 32, 12);
        g.fillRect(12, 0, 8, 32);

        return img;
    }

    @Override
    public DoublePoint getDimensions() {
        return new DoublePoint(1, 2);
    }

    private int walkingDirection = 10;
    private static final double moveSizePerTick = 0.005d;
    private static final double moveSizePerTickLittle = Math.sqrt((moveSizePerTick * moveSizePerTick) / 2);

    /**
     * ändert die Bewegung des Spielers
     *
     * @param newDirection 0 ist up, 2 ist rechst, 4 ist unten und 6 ist
     * links,.... die werte dazwischen sind für allgemeine drehungen für
     * objekte.... ich wollte halt die richtungsangaben programmweit einheitlich
     * lassen
     * @param startNotStop start heißt loslaufen, wenn false dannn anhalten
     * @param activeMap t check the map for obstacles
     */
    public void move(int newDirection, boolean startNotStop, final Map activeMap) {
        final PlayerEntity me = this;

        if (startNotStop) {
            pressedDirections.add(newDirection);
        } else {
            pressedDirections.remove(newDirection);
        }
        walkingDirection = 0;
        for (Object o : pressedDirections.toArray()) {
            walkingDirection += (int) o;
        }
        if (pressedDirections.isEmpty() || pressedDirections.size() > 2
                || (pressedDirections.contains(0) && pressedDirections.contains(4))
                || (pressedDirections.contains(2) && pressedDirections.contains(6))) {
            walkingDirection = 10;
        } else {
            if (pressedDirections.contains(0) && pressedDirections.contains(6)) {
                walkingDirection = 7;
            } else {
                walkingDirection = walkingDirection / pressedDirections.size();
            }
        }

        if (!moveThreadRunning && startNotStop) {
            moveThreadRunning = true;
            //start moving thread
            new Thread() {
                public void run() {
                    do {
                        DoublePoint oldPlayerLocation = new DoublePoint(getLocation());
                        switch (walkingDirection) {
                            case 0:
                                getLocation().y -= moveSizePerTick;
                                break;
                            case 1:
                                getLocation().y -= moveSizePerTickLittle;
                                getLocation().x += moveSizePerTickLittle;
                                break;
                            case 2:
                                getLocation().x += moveSizePerTick;
                                break;
                            case 3:
                                getLocation().y += moveSizePerTickLittle;
                                getLocation().x += moveSizePerTickLittle;
                                break;
                            case 4:
                                getLocation().y += moveSizePerTick;
                                break;
                            case 5:
                                getLocation().y += moveSizePerTickLittle;
                                getLocation().x -= moveSizePerTickLittle;
                                break;
                            case 6:
                                getLocation().x -= moveSizePerTick;
                                break;
                            case 7:
                                getLocation().y -= moveSizePerTickLittle;
                                getLocation().x -= moveSizePerTickLittle;
                                break;
                        }

                        ArrayList<Entity> exceptList = new ArrayList<>();
                        exceptList.add(me);
                        
                        //all 4 edges of player collision box shouldnt be in walls
                        ArrayList<DoublePoint> playerEdges = new ArrayList<>();
                        playerEdges.add(new DoublePoint(getLocation().x + getCollisionComponent().getCollisionBoxStart().x, getLocation().y + getCollisionComponent().getCollisionBoxStart().y));
                        playerEdges.add(new DoublePoint(getLocation().x + getCollisionComponent().getCollisionBoxEnd().x, getLocation().y + getCollisionComponent().getCollisionBoxStart().y));
                        playerEdges.add(new DoublePoint(getLocation().x + getCollisionComponent().getCollisionBoxStart().x, getLocation().y + getCollisionComponent().getCollisionBoxEnd().y));
                        playerEdges.add(new DoublePoint(getLocation().x + getCollisionComponent().getCollisionBoxEnd().x, getLocation().y + getCollisionComponent().getCollisionBoxEnd().y));
                        
                        //add some more border points
                        playerEdges.add(new DoublePoint(playerEdges.get(0).x * 0.5d + playerEdges.get(1).x * 0.5d, playerEdges.get(0).y * 0.5d + playerEdges.get(1).y * 0.5d));
                        playerEdges.add(new DoublePoint(playerEdges.get(2).x * 0.5d + playerEdges.get(3).x * 0.5d, playerEdges.get(2).y * 0.5d + playerEdges.get(3).y * 0.5d));
                        playerEdges.add(new DoublePoint(playerEdges.get(0).x * 0.5d + playerEdges.get(2).x * 0.5d, playerEdges.get(0).y * 0.5d + playerEdges.get(2).y * 0.5d));
                        playerEdges.add(new DoublePoint(playerEdges.get(1).x * 0.5d + playerEdges.get(3).x * 0.5d, playerEdges.get(1).y * 0.5d + playerEdges.get(3).y * 0.5d));

                        for (DoublePoint checkLocation : playerEdges) {
                            if (!activeMap.isWalkable(checkLocation, exceptList)) {
                                setLocation(oldPlayerLocation);
                            }
                        }

                        try {
                            Thread.sleep(1);
                        } catch (Exception ex) {

                        }
                    } while (moveThreadRunning);
                }
            }.start();
        }
    }
    private boolean moveThreadRunning = false;
    private HashSet<Integer> pressedDirections = new HashSet<>();

}
