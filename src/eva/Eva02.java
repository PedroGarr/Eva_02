package eva;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.BrickFinder;
import lejos.hardware.Button;
import lejos.utility.Delay;

public class Eva02 {

	public Eva02() {
		// TODO Auto-generated constructor stub
		//Urgent stop thread
				Thread stopThread = new Thread(new Runnable() {
					@Override
					public void run() {
						// Attendez que le bouton soit pressé
						Button.waitForAnyPress();

						// Arrêtez les threads ou ajoutez ici la logique d'arrêt appropriée.
						stopAllThreads(); // Méthode que vous devrez implémenter.
					}
				});

				// Démarrez le thread pour surveiller le bouton
				stopThread.start();

	}
	//**************************************
	//dev comment not supposed to be keeped for production
	// Doccumentation : 
	//TODO getDirection
	//TODO searchPalet
	//TODO pickPlaet
	//TODO toENdZone
	//TODO Start
	//TODO isObject
	//TODO isEva
	//TODO emergencyStop
	//TODO paletPosition
	//TODO setNorth can be seted by defaullt by lejos
	//TODO stopAllTHread
	// Compleate this methods : 
	//TODO getDirection
	//TODO searchPalet
	//TODO pickPlaet
	//TODO toENdZone
	//TODO Start
	//TODO isObject
	//TODO isEva
	//TODO emergencyStop
	//TODO paletPosition
	//TODO setNorth
	//TODO stopAllThread 
	//**************************************

	// detected object type
	static int ISEMPTY = 0;
	static int ISWALL = 1;
	static int ISPALET = 2;
	static int ISROBOT = 3;
	
	//plaet positoin degreas|distance-to 
	// /!\ this arrays can provoke an error when manipulating it, the directions are initially registered by intager
	// after peaking this values be sur you operations gives a good reslult and not miss aproximating the values
	float[][] paletPosition=new float[9][9];
	
	
	/*
	 * @return the direction (i.e orientation) in degrees in respect to north position, seted by paose privide interface  
	 * */
	public int getDirection(){
		return 0;
	}
	/*
	 * Automate that search palet by executiong folowing methods :
	 * - isObject : in oder to recognise palents
	 * - lidar :  to make the robot rotate like a lidar and register values to use in isObject
	 * When shearching each palet will be stored in paletPosition array with it's direction and distance.
	 * */
	public void searchPalet() {
		//Automat
	}
	// possibly searchPalet and 
	/*
	 * this methode will make the robot go to the palet strate.
	 * it needs to verrify ther is other obstacls than the plaet, as a robot
	 * 	if ther is a robot it lunch emergencyAvoidence().
	 * 	if not it goese strate to the palet
	 *  as the palet is took and isTouching pickPalet lunch toEndZone.
	 * */
	public void pickPalet() {
		
	}
	/*
	 * this method turn to the north wich is the end zone and go strate to the end zone.
	 * if ther is anny object, it lenches emergencyAvoidence
	 * */
	public void toEndZone() {
		// Automat
	}
	public void Start() {
		// Automat
	}
	private int isObject() {
		return 0;
		// Automat reconait wall/robot/palet
	}
	private boolean isEva(int[] positions ){
		return false;
		// retourne la position de Eva
	}
	public void emergencyAvoidence() {
		
	}
	public void emergencyStop() {

	}
	public int lidar() {
		return 0;

	}
	public float paletPosition(int x,int y) {
		return y;

	}

	// urgent stoping method	
		private void stopAllThreads() {
			System.out.println("Systhème stoping....");
			System.exit(0); // Vous pouvez également appeler System.exit(0) pour arrêter l'application.
		}
	public static void main(String[] args) {
		GraphicsLCD g = BrickFinder.getDefault().getGraphicsLCD();
		g.drawString("hello world", 0, 0, GraphicsLCD.VCENTER | GraphicsLCD.LEFT);
		Delay.msDelay(5000);
	}
}
