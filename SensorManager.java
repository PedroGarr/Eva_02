package sensors;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.MindsensorsDistanceSensorV2;
import lejos.hardware.sensor.NXTTouchSensor;
import lejos.hardware.port.Port;

import java.awt.desktop.UserSessionListener;

import lejos.hardware.BrickFinder;
import lejos.hardware.ev3.EV3;
import lejos.robotics.Color;
import lejos.robotics.SampleProvider;
import lejos.hardware.motor.*;


public class SensorManager {
	//color fields 
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
    
    //ultrasonic fields 
    private EV3UltrasonicSensor uSDistance;
	private EV3UltrasonicSensor uSListen;
	private SampleProvider uSDSampleProvider;// for distance 
	private SampleProvider uSLSampleProvider;// for interference
	private float[] uSDSample; // for distence
	private float[] uSLSample; // for interference
	
    //touch
	private NXTTouchSensor touch;
	private SampleProvider tSampleProvider;

    // Constructor that initializes the color sensor
    public SensorManager(Port s4, Port s3, Port s2) {
       // setting port
    	if(s3==null && s2==null && s4==null)
    		throw new IllegalArgumentException();
    	//ultrasonic 
    	else if(s4.getName()=="S4") {
    		uSDistance= new EV3UltrasonicSensor(s4); // Create uSDistance instance connected to s4 port
    		uSListen = uSDistance; // Create uSListen instance and connected to s4 
    	}
    	//color
    	else if(s3.getName()=="S3")
    		this.colorSensor = new EV3ColorSensor(s3); // Create a new color sensor instance
    	//touch
    	else if(s2.getName()=="S2") {
    		touch = new NXTTouchSensor(s2);// Create touche instance
    	}
    	//camera
    	//TODO camera setting
    	
    	//mode attribution
    	this.uSDSampleProvider = uSDistance.getDistanceMode();// Set the distance mode
    	this.uSLSampleProvider = uSListen.getListenMode();// Set the listen mode    	
   //**************************
    	//this lines of code got problems on testing 
    	//TODO Debug this
    	//this.colorSampleProvider = colorSensor.getRGBMode(); // Set the sensor mode to RGB
        //this.tSampleProvider = touch.getTouchMode();// Set the touch sensor mode
        
        //Data maintain --> need tobe stord in an external storage file
        //this.colorSample = new float[colorSampleProvider.sampleSize()]; // Create an array to hold color data
   //***********************
    	this.uSDSample = new float[uSDSampleProvider.sampleSize()];
    }

    
    /*
     * ultrasonic management
     * */
    //return distance in milimeters 
    
    public float getDistance() { 
    	uSDSampleProvider.fetchSample(uSDSample, 0);
        return uSDSample[0] * 1000; // Convert the distance to milimeters
    	
	}
    /*
     * retourne l'interference en float
     * 0
     * */ 
    //TODO debug 
    public float getInterference() {
    	
    	
        if (uSLSampleProvider == null) {
        	throw new NullPointerException();
        } 
        
        else {
        	 uSLSampleProvider.fetchSample(uSLSample, 0);
             
             return uSLSample[0];
        }
    }
    
    /*
     * Touch sensor
     * */
    
    public float isTouching() {
        // Assurez-vous que le capteur tactile est correctement initialisé.
        if (tSampleProvider != null) {
            // Créez un tableau pour stocker l'échantillon du capteur tactile.
            float[] tSample = new float[tSampleProvider.sampleSize()];

            // Obtenez l'échantillon du capteur tactile.
            tSampleProvider.fetchSample(tSample, 0);

            // Si le capteur tactile détecte un contact (pression), renvoyez 1, sinon renvoyez 0.
            if (tSample[0] > 0.0) {
                return 1.0f; // Un contact est détecté.
            } else {
                return 0.0f; // Aucun contact n'est détecté.
            }
        } else {
            // Le capteur tactile n'est pas initialisé, renvoyez une valeur par défaut ou gérez l'erreur comme nécessaire.
            return -1.0f; // Par exemple, retournez -1 pour indiquer une valeur non disponible.
        }
    }
    
    /*
     * Color sensor management 
     * */
    
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
    
    /*
     * détection d'obstacle
     * Cet methode détecte la présence d'obstacle 
     * et une autre methode verifiera le type de l'obstacle s'il y en a un.
     * distance limite théorique  2.30 (m)
     * */
    public boolean isObstacle() {
    	float d = getDistance();
    	//code comented is usefull only if interference work and is used other ways
    	
    	//float i = getInterference();
    	//&& i==0
    	if(d>2200.0 ) {
    		return false;
    	}
    	return true;
    }
    
    
    /*
     * 
     * */
    public boolean isBlocked() {
    	return false;
    			
    }

/*v1 get colrs
 * 
 *  // Method to read and return the color detected by the sensor
    public Color getColor() {
        colorSampleProvider.fetchSample(colorSample, 0); // Read color values into the colorSample array
        int red = (int) (colorSample[0] * 255); // Scale the red value to 8-bit range (0-255)
        int green = (int) (colorSample[1] * 255); // Scale the green value to 8-bit range (0-255)
        int blue = (int) (colorSample[2] * 255); // Scale the blue value to 8-bit range (0-255)
        return new Color(red, green, blue); // Create a Color object with scaled RGB values
    }

 * */

    public static void main(String[] args) {
       //note pour teste trouver un moyens pour stocker les informations dans un doccument à part et le relire pour la reconaissance d'objet dans une boucle
        
    }
}