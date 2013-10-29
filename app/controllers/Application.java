package controllers;

import models.CommandEnum;
import models.DirectionEnum;
import play.mvc.*;

import java.util.ArrayList;
import java.util.List;

public class Application extends Controller {

    static int currentFloor;
    static CommandEnum previousCommand;
    static DirectionEnum direction;
    static boolean doorIsClosed;
    static boolean[] callUp = new boolean[6];
    static boolean[] callDown = new boolean[6];
    static boolean[] floorToStop = new boolean[6];


    public static void index() {
        render();
    }

    public static void reset(String cause) {

        System.out.println("********reset " + cause);
        currentFloor = 0;
        direction = DirectionEnum.UP;
        previousCommand = CommandEnum.CLOSE;
        callDown = new boolean[6];
        callUp=  new boolean[6];
        floorToStop = new boolean[6];
    }

    public static void nextCommand() {
        CommandEnum betterCommand = getBetterCommand();
        updateElevatorState(betterCommand);
        renderText(betterCommand);
    }

    private static void updateElevatorState(CommandEnum nextCommand) {
        previousCommand = nextCommand;
        switch (nextCommand) {
            case UP:
                currentFloor++;
                direction= DirectionEnum.UP;
                break;
            case DOWN:
                currentFloor--;
                direction= DirectionEnum.DOWN;
                break;
            case OPEN:
                floorToStop[currentFloor]=false;
                callDown[currentFloor]=false;
                callUp[currentFloor]=false;
        }
    }

    public static CommandEnum getBetterCommand() {

        switch (previousCommand) {
            case OPEN:
                floorToStop[currentFloor] = false;
                return CommandEnum.CLOSE;
            case CLOSE:
                if(noMoreFloorToGo()){
                    int floorToGo = getNearestCall();
                    if(currentFloor > floorToGo){
                        return CommandEnum.DOWN;
                    }else if(floorToGo==currentFloor){
                        return CommandEnum.OPEN;
                    }else {
                        return CommandEnum.UP;
                    }
                }
                return shouldGoOrChangeDirection();
            case UP:
            case DOWN:
                if (floorToStop[currentFloor]) {
                    return CommandEnum.OPEN;
                }
                boolean[] callDirection = direction == DirectionEnum.UP ? callUp : callDown;
                if(callDirection[currentFloor]) {
                    callDirection[currentFloor]=false;
                    return CommandEnum.OPEN;
                }
                if(currentFloor==0 && callUp[0]){
                    return CommandEnum.OPEN;
                }
                if(currentFloor==5 && callDown[5]){
                    return CommandEnum.OPEN;
                }
                return shouldGoOrChangeDirection();
            default:
                return CommandEnum.OPEN;
        }

    }

    private static int getNearestCall() {
        if(currentFloor>2){
            for(int i = currentFloor;i<6 ;i++){
                if(callUp[i] || callDown[i]){
                    return i;
                }
            }
            for(int i = currentFloor;i>=0 ;i--){
                if(callUp[i] || callDown[i]){
                    return i;
                }
            }
        }else{
            for(int i = currentFloor;i>=0 ;i--){
                if(callUp[i] || callDown[i]){
                    return i;
                }
            }
            for(int i = currentFloor;i<6 ;i++){
                if(callUp[i] || callDown[i]){
                    return i;
                }
            }
        }
        return  currentFloor;
    }

    private static boolean noMoreFloorToGo() {
        for (boolean isFloorToStop : floorToStop) {
            if(isFloorToStop){
                return false;
            }
        }
        return true;
    }

    private static CommandEnum shouldGoOrChangeDirection() {
        switch (direction) {
            case UP:
                if (currentFloor == 5) {
                    direction = DirectionEnum.DOWN;
                    return CommandEnum.DOWN;
                } else {
                    return CommandEnum.UP;
                }
            case DOWN:
            default:
                if (currentFloor == 0) {
                    direction = DirectionEnum.UP;
                    return CommandEnum.UP;
                } else {
                    return CommandEnum.DOWN;
                }
        }
    }

    public static void call(int atFloor, String to) {
        // Info : j'appelle du 4eme et je veux descendre
        System.out.println(atFloor + " " + to);
        if(to.equals("UP")) {
            callUp[atFloor] = true;
        }else {
            callDown[atFloor] = true;
        }
        renderText("");
    }

    public static void go(int floorToGo) {
        System.out.println("floorToGo :" + floorToGo);
        floorToStop[floorToGo] = true;
        renderText("");
    }

    public static void userHasEntered() {
        System.out.println("userHasEntered");
        renderText("");
    }

    public static void userHasExited() {
        System.out.println("userHasEntered");
        renderText("");
    }

}