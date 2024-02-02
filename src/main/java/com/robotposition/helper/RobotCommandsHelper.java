package com.robotposition.helper;


import com.robotposition.exception.IllegalMoveException;
import com.robotposition.model.RobotPosition;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import static com.robotposition.model.RobotPositionCommandEnum.TURN_RIGHT;
import static com.robotposition.model.RobotPositionEnum.EAST;
import static com.robotposition.model.RobotPositionEnum.MAX_X;
import static com.robotposition.model.RobotPositionEnum.MAX_Y;
import static com.robotposition.model.RobotPositionEnum.MIN_X;
import static com.robotposition.model.RobotPositionEnum.MIN_Y;
import static com.robotposition.model.RobotPositionEnum.NORTH;
import static com.robotposition.model.RobotPositionEnum.SOUTH;
import static com.robotposition.model.RobotPositionEnum.WEST;


/**
 * @author Hari
 * Helper class methods to handle the robot movement commands: MOVE, RIGHT and LEFT.
 * The logic is invoked when updating the robot position (PUT)
 */
@Component
public class RobotCommandsHelper {

    Predicate<RobotPosition> westPredicate = (c)-> c.getFacingdir().equals(WEST.getDirection());
    Predicate<RobotPosition> eastPredicate = (c)-> c.getFacingdir().equals(EAST.getDirection());
    Predicate<RobotPosition> northPredicate = (c)-> c.getFacingdir().equals(NORTH.getDirection());
    Predicate<RobotPosition> southPredicate = (c)-> c.getFacingdir().equals(SOUTH.getDirection());

    Predicate<String> rightTurnPredicate = c -> c.equals(TURN_RIGHT.getCommand());
    BiConsumer<RobotPosition, String> biConsumer = RobotPosition::setFacingdir;

    public RobotPosition updateRobotPositionBasedOnCommands(final RobotPosition currentRobotPosition,
                                                            final String robotPositionCommands){

        if(robotPositionCommands != null) {
            String[] commands = robotPositionCommands.split(" ");

            for (String command : commands) {
                switch (command) {
                    case "MOVE" -> handleMove(currentRobotPosition);
                    case "LEFT", "RIGHT" -> handleTurn(currentRobotPosition, command);
                }
            }
        }
        return currentRobotPosition;
    }

    private void handleTurn(final RobotPosition currentRobotPosition, final String command) {
        /*Change the direction of the robot when turning right. One turn is 90deg. Calculate the new direction accordingly.*/
       if(rightTurnPredicate.test(command)) {
           if (westPredicate.test(currentRobotPosition))
               biConsumer.accept(currentRobotPosition, NORTH.getDirection());
           else if (eastPredicate.test(currentRobotPosition)) {
               biConsumer.accept(currentRobotPosition, SOUTH.getDirection());
           } else if (northPredicate.test(currentRobotPosition)) {
               biConsumer.accept(currentRobotPosition, EAST.getDirection());
           } else if (southPredicate.test(currentRobotPosition)) {
               biConsumer.accept(currentRobotPosition, WEST.getDirection());
           }
       } else {/* This else is for LEFT command. If not RIGHT, must be LEFT. One turn is 90deg. Calculate the new direction accordingly.*/
           if (westPredicate.test(currentRobotPosition))
               biConsumer.accept(currentRobotPosition, SOUTH.getDirection());
           else if (eastPredicate.test(currentRobotPosition)) {
               biConsumer.accept(currentRobotPosition, NORTH.getDirection());
           } else if (northPredicate.test(currentRobotPosition)) {
               biConsumer.accept(currentRobotPosition, WEST.getDirection());
           } else if (southPredicate.test(currentRobotPosition)) {
               biConsumer.accept(currentRobotPosition, EAST.getDirection());
           }
       }
    }

    private void handleMove(final RobotPosition currentRobotPosition) {

        if (westPredicate.test(currentRobotPosition)){
            handleWestMove(currentRobotPosition);
        } else if (eastPredicate.test(currentRobotPosition)){
            handleEastMove(currentRobotPosition);
        } else if (northPredicate.test(currentRobotPosition)){
            handleNorthMove(currentRobotPosition);
        } else if (southPredicate.test(currentRobotPosition)){
            handleSouthMove(currentRobotPosition);
        }

    }
    private void handleNorthMove(final RobotPosition currentRobotPosition) {
        /*The north-facing robot cannot fall off the table when attempted to move*/
        if (currentRobotPosition.getYpos() == MAX_Y.getValue()){
            throw new IllegalMoveException("north");
        }
        else {
            /*The north-facing robot moves y+1*/
            currentRobotPosition.setYpos(currentRobotPosition.getYpos()+1);
        }
    }

    private void handleSouthMove(final RobotPosition currentRobotPosition)  {
        /*The south-facing robot cannot fall off the table when attempted to move*/
        if (currentRobotPosition.getYpos() == MIN_Y.getValue()){
            throw new IllegalMoveException("south");
        }
        else {
            /*The south-facing robot moves y-1*/
            currentRobotPosition.setXpos(currentRobotPosition.getYpos()-1);
        }
    }
    private void handleEastMove(final RobotPosition currentRobotPosition)  {
        /*The east-facing robot cannot fall off the table when attempted to move*/
        if (currentRobotPosition.getXpos() == MAX_X.getValue()){
            throw new IllegalMoveException("east");
        }
        else {
            /*The east-facing robot moves x+1*/
            currentRobotPosition.setXpos(currentRobotPosition.getXpos()+1);
        }
    }

    private void handleWestMove(final RobotPosition currentRobotPosition) {
        /*The west-facing robot cannot fall off the table when attempted to move*/
        if (currentRobotPosition.getXpos() == MIN_X.getValue()){
            throw new IllegalMoveException("west");
        }
        else {
            /*The west-facing robot moves x+1*/
            currentRobotPosition.setXpos(currentRobotPosition.getXpos()-1);
        }
    }
}
