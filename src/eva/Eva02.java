package eva;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.port.*;
import lejos.hardware.port.UARTPort;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.utility.Delay;
import sensors.SensorManagerThread;

import javax.swing.event.TreeExpansionEvent;

import engines.Transit;
public class Eva02 {
	/**
	 * The Eva02 class represents the main control logic for the robot's actions, including
	 * searching for and picking up palets, moving towards the end zone, and handling emergency stops.
	 * It relies on the SensorManagerThread and Transit classes for sensor and motor control.
	 * 
	 * The Eva02 class includes methods for palet search, picking up palets, moving towards the end zone,
	 * and handling emergency stops. It also defines constants for detected object types and palet positions.
	 * 
	 * @author Alain, Pedro, Mohamed, Mohtadi 
	 * @version 2.0
	 */
	
	
	static SensorManagerThread smt=new SensorManagerThread( );
	static Transit tr=new Transit();
	static int paletcount=0;
	/**
     * Constructor for the Eva02 class. Initializes the urgent stop thread.
     */
	public Eva02() {
		// TODO Auto-generated constructor stub
		//Urgent stop thread
		
		
		Thread stopThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// Attendez que le bouton soit pressé
				Button.DOWN.waitForPress();

				// Arrêtez les threads ou ajoutez ici la logique d'arrêt appropriée.
				stopAllThreads(); // Méthode que vous devrez implémenter.
			}
		});

		// Démarrez le thread pour surveiller le bouton
		stopThread.start();

	}
	

	
	/**
     * Searches for palets using the lidar method and adjusts the robot's position
     * accordingly. Once a palet is found, it stops and picks up the palet.
     * 
     * @return True if a palet is successfully picked up, false otherwise.
     */
	public static boolean searchPalet() {
		//Automat of palet search
		tr.setRotationSpeed();
		boolean palet=false;
		while(!palet) {
			int distancePlusProche=lidar(180);
			if(tr.isMoving()==false) 
				tr.openGrip();

			tr.forward(distancePlusProche,true);
			while( tr.isMoving()) {

				if( smt.isTouching()==true) {
					 tr.stop();
					 tr.asyncCloseGrip();
					 tr.rotateCompass(0);
					return true;
				}

				if ( smt.getDistance()<200 ) 
					 tr.asyncRotate(180);

				emergencyStop();
				Delay.msDelay(10);
			}
			if( tr.isMoving()==false)
				 tr.closeGrip();
		}
		return false;
	}
	
	/**
     * Moves towards the palet, checking for obstacles. If an obstacle is detected
     * (possibly a robot), it launches emergencyAvoidance. Once the palet is picked up,
     * it goes straight to the end zone.
     */
	public static void pickPalet() {
		 tr.asyncOpenGrip();
		 tr.moveForward();
		while( smt.isObstacle()==true) {
			if ( smt.isObstacle()==true) {
				 tr.stop();
				System.out.println("robot");
				 tr.moveForward();
				 Delay.msDelay(3000);
			}
			emergencyStop();
			Delay.msDelay(10);
		}
		 tr.stop();
		 tr.asyncCloseGrip();
	}
	
	/**
     * Moves towards the end zone, checking for obstacles. If an obstacle is detected,
     * it launches emergencyAvoidance. Once at the end zone, it opens the grip and updates
     * the palet count and position.
     */
	public static void toEndZone() {
		// Automat of goal reaching
		 tr.rotate( tr.getCompass());
		 tr.setMaxSpeed();
		 tr.moveForward();
		//trying to not use color checking
		float distanceInitial= smt.getDistance();
		float distanceUpdate= smt.getDistance();

		while(distanceInitial>20 && distanceUpdate>20 && tr.getCompass()==0) {//this two distances are supposed to be taken in a enought difference of time to let an other robopass and have a wall comming 
			distanceInitial= smt.getDistance();
			if( smt.isObstacle()) {
				 tr.asyncRotate(-45);
				 tr.forward(300,false);
				 tr.asyncRotate(45);
				//distanceUpdate= smt.getDistance();
			}
			//distanceUpdate= smt.getDistance();
			emergencyStop();
			Delay.msDelay(50);
			distanceInitial= smt.getDistance();
		}
		//be shurr the postions are welle taken
		 tr.stop();
		 tr.asyncOpenGrip();
		 tr.asyncBackward(100);
		paletcount++;
		double XMesure= smt.getDistance();
		 tr.asyncCloseGrip();
		 tr.asyncRotate(90);
		double YMeusre= smt.getDistance();

		 tr.setPosX(XMesure);
		 tr.setPosY(YMeusre);

	}
	
	 /**
     * Starts the robot's operation by picking up the first palet and moving towards the end zone.
     */
	public static void Start() {
		// Automat of the firest palet
		pickPalet();
		toEndZone();
	}
	
	/**
     * Handles emergency stops by checking if the ENTER button is pressed.
     */
	public static void emergencyStop() {
		if (Button.ENTER.isDown()==true) {
			 tr.stop();
			while(true) {
				Delay.msDelay(10);
				break;
			}
		}
	}
	
	/**
     * Uses the lidar method to perform a 180-degree scan for obstacles and returns
     * the distance to the nearest obstacle.
     * 
     * @param angle The scanning angle (180 degrees).
     * @return The distance to the nearest obstacle in millimeters.
     */
	public static int lidar(int angle) {
		double valeurPlusPetite = 10;
		double indiceAngle = 0;
		 tr.rotate(angle/2);
		 tr.setCompass( tr.getCompass()+angle/2);
		 tr.rotate(-angle,true);
		 tr.setCompass( tr.getCompass()+angle);
		while( tr.isMoving()) {
			double valRuning =  smt.getDistance();
			if(valRuning < valeurPlusPetite && valRuning>20) {
				valeurPlusPetite = valRuning;
				indiceAngle =  tr.getMovement().getAngleTurned();
			}
			Delay.msDelay(5);
		}
		 tr.rotate(angle+indiceAngle, false);
		 tr.setCompass( tr.getCompass()+angle+indiceAngle);
		return (int) (valeurPlusPetite);

	}


	// urgent stoping method	
	/**
	 * Stops all threads and terminates the program. This method is called in response
	 * to the DOWN button being pressed. It prints a system stopping message to the console
	 * and then exits the program with a status code of 0.
	 */
	private void stopAllThreads() {
		System.out.println("Systhème stoping....");
		System.exit(0); 
	}

	/**
     * Main method that executes the robot's operation for a specified number of palets.
     * Continuously calls the Start method, searches for palets, picks them up, and handles
     * emergency stops until the specified number of palets is reached.
     * 
     * @param args Command-line arguments (not used).
     */
	public static void main(String[] args) {
		int paletCounter=0;
		while(paletCounter<6) {
			Start();
			if(searchPalet())
				pickPalet();
			emergencyStop();
		}
	}
	 
}
