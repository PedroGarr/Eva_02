import lejos.hardware.motor.Motor;
import lejos.utility.Delay;

public class FirstTry {
    public static void main(String[] args) {
    	
    	
        Motor.B.setSpeed(400); 
        Motor.D.setSpeed(400); 

        // Start both motors to move forward
        Motor.B.backward();
        Motor.D.backward();

        // Sleep for 3 seconds (3000 milliseconds)
        Delay.msDelay(3000);

        // Stop both motors
        Motor.B.stop();
        Motor.D.stop();

    }
}
