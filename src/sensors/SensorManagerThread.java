package sensors;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.MindsensorsDistanceSensorV2;
import lejos.hardware.sensor.NXTTouchSensor;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.robotics.Color;
import lejos.robotics.SampleProvider;
import lejos.hardware.motor.*;
import lejos.hardware.Button;

/**
 * The `SensorManagerThread` class provides a threaded interface for managing various sensors
 * on a robotic platform. It includes support for an ultrasonic sensor, touch sensor, and potential
 * color sensor (commented out in the current implementation). The class initializes the sensors,
 * sets up their modes, and runs threads for monitoring distance, touch, and potential interference.
 * The distance and touch sensor threads provide continuous updates, and the class includes methods
 * to check for obstacles, detect touches, and get distance readings.
 *
 * The class is designed to be used in conjunction with a robotic system where threaded sensor monitoring
 * is essential for real-time decision-making. The ultrasonic sensor is used for distance measurement,
 * the touch sensor for detecting physical contact, and the potential color sensor for color detection
 * (currently commented out). The class also includes methods for handling obstacles and potential
 * interference, though the interference method is currently commented out.
 *
 * The class implements a basic structure for threading and continuous monitoring of sensors,
 * providing a foundation for integrating additional sensors and behaviors into a robotic system.
 * Additionally, the class includes a method for checking if the robot is close to a certain object.
 * 
 * @author Alain, Mohtadi
 */

public class SensorManagerThread {
	//oject detection distance 
	double minD=2;
	Port port;
	//color fields 
	/*
	private EV3ColorSensor colorSensor;
	private SampleProvider colorSampleProvider;
	private float[] colorSample;
	static int[] RED = {34,8,6};
	static int[] BLUE = {7,9,16}; 
	static int[] GREEN = {16,26,9};
	static int[] YELLOW = {65,45,11};
	static int[] GREY = {25,22,20}; 
	static int[] WHITE = {75,57,48}; 
	static int[] BLACK = {6,6,6};
	*/
	Port port4=(Port) SensorPort.S4;
	Port port3=(Port) SensorPort.S3;
	Port port2=(Port) SensorPort.S2;
	//ultrasonic fields 
	private EV3UltrasonicSensor uSDistance;
	private SampleProvider uSDSampleProvider;// for distance 
	private float[] uSDSample; // for distence
	

	//touch
	private NXTTouchSensor touch;
	private SampleProvider tSampleProvider;
	private float[] tSample;

	 /**
     * Constructor that initializes the color sensor, ultrasonic sensor, and touch sensor.
     * It sets up the necessary ports, modes, and threading for continuous monitoring.
     */
	public SensorManagerThread() {
		// setting port
		if(port3==null && port2==null && port4==null)
			throw new IllegalArgumentException();
		//ultrasonic 
		else if(port4.getName().equals("S4")) {
			uSDistance= new EV3UltrasonicSensor(port4); // Create uSDistance instance connected to s4 port
		}
		//color
		/*
		else if(s3.getName()=="S3")
			this.colorSensor = new EV3ColorSensor(s3); // Create a new color sensor instance
		*/
		//touch
		else if(port2.getName().equals("S2")) {
			touch = new NXTTouchSensor(port2);// Create touche instance
		}
		//camera
		//TODO camera setting

		//mode attribution
		this.uSDSampleProvider = uSDistance.getDistanceMode();// Set the distance mode
		//**************************
		//this lines of code got problems on testing 
		//TODO Debug thisa
		//this.colorSampleProvider = colorSensor.getRGBMode(); // Set the sensor mode to RGB
		//this.tSampleProvider = touch.getTouchMode();// Set the touch sensor mode

		//Data maintain --> need tobe stord in an external storage file
		//this.colorSample = new float[colorSampleProvider.sampleSize()]; // Create an array to hold color data
		//***********************
		this.uSDSample = new float[uSDSampleProvider.sampleSize()];

		// Threading
		//distance
		Thread distanceThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					float distance = getDistance();
					// Vous pouvez ajouter ici la logique de réaction basée sur la distance.
						
					// Attendez un certain temps entre les lectures pour éviter de lire en continu.
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});

		//touch
		Thread touchThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (isTouching()) {
						// Le capteur tactile détecte un contact, vous pouvez ajouter ici la logique de réaction.
						System.out.println("Contact détecté!");
					}

					// Attendez un certain temps entre les lectures pour éviter de lire en continu.
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});

		//interferance
		//TODO debug interferance mth 
		/*
		 Thread interferenceThread = new Thread(new Runnable() {
	            @Override
	            public void run() {
	                while (true) {
	                    float interference = getInterference();

	                    System.out.println("interferance = "+interference );
	                    try {
	                        Thread.sleep(100);
	                    } catch (InterruptedException e) {
	                        e.printStackTrace();
	                    }
	                }
	            }
	        });
		 */


		// Démarrez le thread
		touchThread.start();
		distanceThread.start();
		//interferenceThread.start();


		
	}


	 /**
     * Returns the distance measured by the ultrasonic sensor in millimeters.
     *
     * @return The distance measured by the ultrasonic sensor in millimeters.
     */

	public float getDistance() { 
		// Assurez-vous que le capteur ultrasonique est correctement initialisé.
		if (uSDSampleProvider != null) {
			// Obtenez l'échantillon de distance du capteur ultrasonique.
			uSDSampleProvider.fetchSample(uSDSample, 0);

			// Convertissez la distance en millimètres et retournez-la.
			return uSDSample[0] * 1000;
		} else {
			// Le capteur ultrasonique n'est pas initialisé, retournez une valeur par défaut ou gérez l'erreur.
			return -1.0f; // Par exemple, retournez -1 pour indiquer une valeur non disponible.
		}

	}
	/*
	 * retourne l'interference en float
	 * 0
	 * */ 
	//TODO debug in case of use 
	/*
	public float getInterference() {


		if (uSLSampleProvider == null) {
			throw new NullPointerException();
		} 

		else {
			uSLSampleProvider.fetchSample(uSLSample, 0);

			return uSLSample[0];
		}
	}
	 */
	
	/**
	 * Checks whether the touch sensor detects a physical contact or pressure.
	 *
	 * @return True if the touch sensor detects a contact (pressure), false otherwise.
	 *         If the touch sensor is not properly initialized, it returns false by default.
	 */
	public boolean isTouching() {
		// Assurez-vous que le capteur tactile est correctement initialisé.
		if (tSampleProvider != null) {
			// Obtenez l'échantillon du capteur tactile.
			tSampleProvider.fetchSample(tSample, 0);

			// Si le capteur tactile détecte un contact (pression), retournez true, sinon retournez false.
			
			return tSample[0] > 0.0;
		} else {
			// Le capteur tactile n'est pas initialisé.
			return false;
		}
	}

	/*
	 * Color sensor management 
	 * not used 
	 * */
/*
	public Color getColor() {
		// Lire les valeurs de couleur dans le tableau colorSample
		colorSampleProvider.fetchSample(colorSample, 0);

		// Convertir les valeurs de couleur en échelle 8 bits (0-255)
		int red = (int) (colorSample[0] * 255);
		int green = (int) (colorSample[1] * 255);
		int blue = (int) (colorSample[2] * 255);

		return new Color(red, green, blue);
	}

	/*
	 * renvoie la couleur sous forme de string en commparent aux valeurs de référance
	 * /!\ cette methode fait des comparaison avec une calibration possiblement tronqué
	 * */
	/*
	public String getDetectedColor() {

		Color color = getColor();

		// Comparer les valeurs RGB aux couleurs prédéfinies
		if (isColorSimilar(color, RED)) {
			return "Red";
		} else if (isColorSimilar(color, BLUE)) {
			return "Blue";
		} else if (isColorSimilar(color, GREEN)) {
			return "Green";
		} else if (isColorSimilar(color, YELLOW)) {
			return "Yellow";
		} else if (isColorSimilar(color, GREY)) {
			return "Grey";
		} else if (isColorSimilar(color, WHITE)) {
			return "White";
		} else if (isColorSimilar(color, BLACK)) {
			return "Black";
		} else {
			return "Unknown";
		}
	}

	// Méthode pour vérifier la similitude des couleurs en comparant les valeurs RGB
	private boolean isColorSimilar(Color color, int[] referenceColor) {
		int tolerance = 30; // Tolerance to compare RGB values 

		return Math.abs(color.getRed() - referenceColor[0]) <= tolerance
				&& Math.abs(color.getGreen() - referenceColor[1]) <= tolerance
				&& Math.abs(color.getBlue() - referenceColor[2]) <= tolerance;
	}
	*/
	
	/**
     * Checks if an obstacle is detected based on the predefined minimum distance.
     *
     * @return True if an obstacle is detected, false otherwise.
     */
	public boolean isObstacle() {
		float d = getDistance();
		//code comented is usefull only if interference work and is used other ways

		//float i = getInterference();
		//&& i==0
		if(d>minD ) {
			return false;
		}
		return true;
	}
	
	/**
     * Checks if the robot picles is close to an object.
     *
     * @return True if the robot is close to an object, false otherwise.
     */
	public boolean close() {
		uSDSampleProvider.fetchSample(uSDSample,0);
		float distance = uSDSample[0]*1000;
		return distance <10;
	}


	/*
	 * 
	 * */
	public boolean isBlocked() {
		return false;

	}





	
}