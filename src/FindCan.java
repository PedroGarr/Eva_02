package engines;


//package *add package name here*;

import lejos.hardware.Button;


public class FindCan extends EV3Skeleton {

 /**
  * Main function of program.
  */
 public static void main(String[] args) {
     initRobot();
     initPilot();

     System.out.println("Press any button to start!");
     Button.waitForAnyPress();

     // Add your code below here
     
     // Rotate slowly so the ultrasonic sensor has time to react
     pilot.setAngularSpeed(60);
     pilot.rotateRight();

     // Wait for a can to be seen
     while (distanceSensor.getDistance() > 0.6) {
         // Do nothing
     }
     
     // Drive to the can
     pilot.travel(distanceSensor.getDistance());
     
     // Grip the can
     clawMotor.rotate(-90); 
 }
}