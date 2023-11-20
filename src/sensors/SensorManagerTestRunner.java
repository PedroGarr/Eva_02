package sensors;

import java.sql.Time;

import lejos.hardware.port.SensorPort;
//import lejos.robotics.Color;

public class SensorManagerTestRunner {

    public static void main(String[] args) {
    	long start = System.currentTimeMillis();
        // Initialize SensorManager with the ports where the sensors are connected
    	SensorManagerThread sensorManager = new SensorManagerThread(SensorPort.S4, SensorPort.S3, SensorPort.S2); // Replace with the actual sensor port

        // Test Color Sensor
        /*
        Color detectedColor = sensorManager.getColor();
        System.out.println("Detected color RGB: " +
                detectedColor.getRed() + ", " +
                detectedColor.getGreen() + ", " +
                detectedColor.getBlue());

        String colorName = sensorManager.getDetectedColor();
        System.out.println("Detected color name: " + colorName);
        */
    	
        // Test Ultrasonic Sensor
        float distance = sensorManager.getDistance();
        System.out.println("Detected distance: " + distance + " mm");

        // Test Touch Sensor
        boolean touch = sensorManager.isTouching();
        System.out.println("Touch sensor pressed: " + (touch == true ? "Yes" : "No"));

        // Test for obstacle detection
        boolean obstacleDetected = sensorManager.isObstacle();
        System.out.println("Is there an obstacle: " + (obstacleDetected ? "Yes" : "No"));
       
        for(int i=0;i<10;i++) {
        	
        	System.out.println("mesuring");
        	System.out.println("distance = " + distance);
        	
        }
        long finish = System.currentTimeMillis();
    	System.err.println("Time "+(finish - start)+" ms");
    	//this part of  code takes about 868ms to be runed
        
    	// Test for interference (this is somewhat dependent on your sensor's capabilities)
        //float interference = sensorManager.getInterference();
       // System.out.println("Interference level: " + interference);

      
    	
    }
    /* 
     * the whole code takes, to be runed :
     * 
     * 1 6212 ms
     * 2 4701 ms
     * 3 4613 ms
     * 4 5163 ms
     * 5 6273 ms
     * 6 6565 ms
     *error
     * 7 5351 ms
     * 8 5348 ms
     * disconection
     * error
     * 9 6336 ms
     *10 6054 ms
     *Mead = 5661,6 ms = 5,7 s 
    */
    
}