package engines;

import lejos.hardware.Button;
import lejos.robotics.navigation.MovePilot;
import lejos.robotics.navigation.MovePilot.*;
import lejos.hardware.Sound;
import lejos.hardware.motor.*;
import lejos.hardware.port.*;
import lejos.utility.Delay;
import lejos.hardware.BrickFinder;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.port.MotorPort;
import lejos.robotics.EncoderMotor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.chassis.Chassis;
import lejos.robotics.chassis.Wheel;
import lejos.robotics.chassis.WheeledChassis;
import lejos.robotics.chassis.WheeledChassis.Modeler;
import lejos.utility.Delay;

/**
 * The Transit class represents a robot with movement capabilities.
 */
public class Transit extends MovePilot {

    // Motor instances for the left, right, and gripping mechanisms
    private static RegulatedMotor leftMotor = new EV3LargeRegulatedMotor(MotorPort.A);
    private static RegulatedMotor gripMotor = new EV3MediumRegulatedMotor(MotorPort.B);
    private static RegulatedMotor rightMotor = new EV3LargeRegulatedMotor(MotorPort.C);

    // Wheels for the left and right sides of the chassis
    private static Wheel leftWheel = WheeledChassis.modelWheel(leftMotor, 56).offset(60.5);
    private static Wheel rightWheel = WheeledChassis.modelWheel(rightMotor, 56).offset(-60.5);

    // Chassis instance representing the robot's movement system
    private static Chassis myChassis = new WheeledChassis(new Wheel[]{leftWheel, rightWheel}, WheeledChassis.TYPE_DIFFERENTIAL);

    // Robot's current position and compass direction
    protected double compass;
    protected double posX;
    protected double posY;

    /**
     * Constructor for the Transit class.
     */
    public Transit() {
        super(myChassis);
        this.posX = 0;
        this.posY = 0;
        this.compass = 0;
    }

    /*
     * Getters and Setters for X and Y coordinates.
     */
    public double getPosX() {
        return this.posX;
    }

    public void setPosX(double x) {
        this.posX = x;
    }

    public double getPosY() {
        return this.posY;
    }

    public void setPosY(double y) {
        this.posY = y;
    }

    public double getCompass() {
        return this.compass;
    }

    public void setCompass(double c) {
        this.compass = c;
    }

    /**
     * Move the robot backward asynchronously.
     *
     * @param d Distance to be covered by the robot.
     */
    public void asyncBackward(double d) {
        super.travel(-d);
    }

    /**
     * Move the robot forward.
     *
     * @param b If b=true: synchronous
     * @param d Distance to be covered by the robot.
     */
    public void forward(double d, boolean b) {
        super.travel(d, b);
    }

    /**
     * Move the robot forward asynchronously.
     *
     * @param d Distance to be covered by the robot.
     */
    public void asyncForward(double d) {
        super.travel(d);
    }

    /**
     * Move the robot forward indefinitely.
     */
    public void moveForward() {
        super.forward();
    }

    // Methods for gripping mechanisms

    /**
     * Close the robot's grip synchronously.
     */
    public void closeGrip() {
        gripMotor.setSpeed((int) gripMotor.getMaxSpeed());
        gripMotor.rotate(-1200, true);
    }

    /**
     * Close the robot's grip asynchronously.
     */
    public void asyncCloseGrip() {
        gripMotor.setSpeed((int) gripMotor.getMaxSpeed());
        gripMotor.rotate(-1200);
    }

    /**
     * Open the robot's grip synchronously.
     */
    public void openGrip() {
        gripMotor.setSpeed((int) gripMotor.getMaxSpeed());
        gripMotor.rotate(1200, true);
    }

    /**
     * Open the robot's grip asynchronously.
     */
    public void asyncOpenGrip() {
        gripMotor.setSpeed((int) gripMotor.getMaxSpeed());
        gripMotor.rotate(600);
    }

    /**
     * Set a constant speed for the robot's rotations.
     */
    public void setRotationSpeed() {
        super.setAngularSpeed(110);
    }

    /**
     * Set the maximum speed of the robot. Subtracting -100 for straight-line travel.
     */
    public void setMaxSpeed() {
        super.setLinearSpeed(super.getMaxLinearSpeed() - 100);
    }

    /**
     * Rotate the robot by a certain angle synchronously.
     *
     * @param x Angle of rotation.
     */
    public void rotate(double x) {
        this.setRotationSpeed();
        super.rotate(x, true);
        compass = (compass + x) % 360;
    }

    /**
     * Rotate the robot by a certain angle asynchronously.
     *
     * @param d Angle of rotation.
     */
    public void asyncRotate(double d) {
        this.setRotationSpeed();
        super.rotate(d);
        compass = compass + d % 360;
    }

    /**
     * Orient the robot to a specific angle based on its initial orientation.
     *
     * @param angle Angle to which the robot should orient itself based on its compass direction.
     */
    public void rotateCompass(double angle) {
        double compass2;
        double angle2;

        // Check if the robot is already at the desired angle
        if (angle == this.compass) {
            return;
        }

        // Handle special cases for angles at 180 degrees
        if (Math.abs(angle) == Math.abs(this.compass) && Math.abs(angle) == 180) {
            return;
        }

        // Normalize compass and angle values
        if (this.compass < 0) {
            compass2 = 360 + this.compass;
        } else {
            compass2 = this.compass;
        }
        if (angle < 0) {
            angle2 = 360 + angle;
        } else {
            angle2 = angle;
        }

        // Rotate the robot based on the shortest path to the desired angle
        if (angle2 > compass2) {
            if (angle2 - compass2 > 180) {
                this.asyncRotate((angle2 - 360) - compass2);
            } else {
                this.asyncRotate(angle2 - compass2);
            }
        } else {
            if (compass2 - angle2 < 180) {
                this.asyncRotate(angle2 - compass2);
            } else {
                this.asyncRotate((360 - compass2) + angle2);
            }
        }

        this.compass = angle;
    }

    /**
 * Moves the object to specified coordinates with a certain orientation.
 *
 * @param x      The target x-coordinate.
 * @param y      The target y-coordinate.
 * @param left A flag indicating the direction (true for left, false for right).
 */
public void goToCoord(double x, double y, boolean left) {
    // Check if the object is already at the target position
    if (y == this.posY && x == this.posX) {
        return; // No movement needed if already at the target position
    }

    // Set the angular speed for the movement
    this.setAngularSpeed(200);

    // Calculate the angle based on the target coordinates
    double angle = calculateAngle(x, y);

    // Rotate the object based on the specified direction
    if (left) {
        rotateCompass(getRotationAngle(x, y, this.posX, this.posY));
    } else {
        rotateCompass(getRotationAngle(this.posX, this.posY, x, y) + (left ? 180 : 0));
    }

    // Rotate the object asynchronously to the calculated angle
    this.asyncRotate(angle);

    // Move the object forward to the target coordinates
    forward(Math.sqrt(Math.pow(Math.abs(this.posX - x), 2) + Math.pow(Math.abs(this.posY - y), 2)), false);

    // Update the current position to the target coordinates
    this.posY = y;
    this.posX = x;

    // Adjust the angular speed for the right movement
    if (!left) {
        this.setAngularSpeed(240);
    }
}

/**
 * Calculates the angle based on the target coordinates.
 *
 * @param targetX The target x-coordinate.
 * @param targetY The target y-coordinate.
 * @return The calculated angle in degrees.
 */
private double calculateAngle(double targetX, double targetY) {
    // Handle special cases for vertical and horizontal lines
    if (targetY - this.posY == 0) {
        return 0;
    } else if (targetX - this.posX == 0) {
        return 90;
    } else {
        // Calculate the angle using atan function
        return Math.toDegrees(Math.atan((targetY - this.posY) / (targetX - this.posX)));
    }
}

/**
 * Calculates the rotation angle based on the start and end coordinates.
 *
 * @param startX The starting x-coordinate.
 * @param startY The starting y-coordinate.
 * @param endX   The ending x-coordinate.
 * @param endY   The ending y-coordinate.
 * @return The calculated rotation angle in degrees.
 */
private double getRotationAngle(double startX, double startY, double endX, double endY) {
    // Handle special cases for vertical and horizontal lines
    if (endY - startY == 0) {
        return 0;
    } else if (endX - startX == 0) {
        return 90;
    } else {
        // Calculate the rotation angle using atan function
        return Math.toDegrees(Math.atan((endY - startY) / (endX - startX)));
    }
}

}
