// RobotBuilder Version: 2.0
//
// This file was generated by RobotBuilder. It contains sections of
// code that are automatically generated and assigned by robotbuilder.
// These sections will be updated in the future when you export to
// Java from RobotBuilder. Do not put any code or make any change in
// the blocks indicating autogenerated code or it will be lost on an
// update. Deleting the comments indicating the section will prevent
// it from being updated in the future.


package frc.robot.subsystems;


import frc.robot.commands.*;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;

import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonFXFeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;

import edu.wpi.first.wpilibj.Joystick;
// BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=IMPORTS


/**
 *
 */
public class Drivetrain extends Subsystem {

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTANTS

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DECLARATIONS


    private double kF = 1.6;
    private double kP = .2;
    private double kI = .1;
    private double kD = -0;

    private double Ldeadband = .15;
    private double Rdeadband = .15;

    private TalonFX leftTalonLead;
    //private TalonFX leftTalonFollower;
    private TalonFX rightTalonLead;
    //private TalonFX rightTalonFollower;

    private double maxRPM = 6000.;

    private final double INVALID_INPUT = -99;

    public Drivetrain() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS

    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CONSTRUCTORS
    
    leftTalonLead = new TalonFX(1);
    //leftTalonFollower = new TalonFX(2);
    rightTalonLead = new TalonFX(3);
    //rightTalonFollower = new TalonFX(4);
    
    leftTalonLead.clearStickyFaults();
    //leftTalonFollower.clearStickyFaults();
    rightTalonLead.clearStickyFaults();
    //rightTalonFollower.clearStickyFaults();

    //leftTalonFollower.follow(leftTalonLead);
    //rightTalonFollower.follow(rightTalonLead);

    leftTalonLead.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, 0, 4000);
    rightTalonLead.configSelectedFeedbackSensor(TalonFXFeedbackDevice.IntegratedSensor, 0, 4000);

    leftTalonLead.config_kF(0,kF,10);
    leftTalonLead.config_kP(0,kP,10);
    leftTalonLead.config_kI(0,kI,10);
    leftTalonLead.config_kD(0,kD,0);

    rightTalonLead.config_kF(0,kF,10);
    rightTalonLead.config_kP(0,kP,10);
    rightTalonLead.config_kI(0,kI,10);
    rightTalonLead.config_kD(0,kD,0);

    /*TODO Research whether talon fx native deadbands are good or not
    rightTalonLead.configNeutralDeadband(Rdeadband);
    leftTalonLead.configNeutralDeadband(Ldeadband);*/

    }

    @Override
    public void initDefaultCommand() {
        // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND


    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=DEFAULT_COMMAND

        // Set the default command for a subsystem here.
        // setDefaultCommand(new MySpecialCommand());
        setDefaultCommand(new DumbDrive());
    }

    @Override
    public void periodic() {
        // Put code here to be run every loop

    }

    // BEGIN AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CMDPIDGETTERS


    // END AUTOGENERATED CODE, SOURCE=ROBOTBUILDER ID=CMDPIDGETTERS


    // Put methods for controlling this subsystem
    // here. Call these from Commands.


    // Converts joystick input adjusted for deadband to current for the motor
    public void dumbDrive(Joystick left, Joystick right) {

        double leftPos = left.getY();
        double rightPos = right.getY();
        double retval = 0.0;

        /*retval = calcMotorPower(leftPos, Ldeadband);
        if(INVALID_INPUT == retval) {
            System.out.println("Invalid left motor input" + leftPos);
        } else {
            leftTalonLead.set(TalonFXControlMode.Current,retval);    
        }

        retval = calcMotorPower(rightPos, Rdeadband);
        if(INVALID_INPUT == retval) {
            System.out.println("Invalid right motor input" + rightPos);
        } else {
            rightTalonLead.set(TalonFXControlMode.Current,retval);    
        }*/
        leftTalonLead.set(TalonFXControlMode.Current,leftPos);   
        rightTalonLead.set(TalonFXControlMode.Current,rightPos);     
    }

    // Converts joystick input adjusted to a RPM for the Falcon's PIDF loop to aim for
    public void velocityDrive(Joystick left, Joystick right){
        double leftPos = left.getY();
        double rightPos = right.getY();

        double retval = 0.0;

        retval = calcMotorPower(leftPos, Ldeadband);
        if(INVALID_INPUT == retval) {
            System.out.println("Invalid left motor input" + leftPos);
        } else {
            leftTalonLead.set(TalonFXControlMode.Velocity,(retval * maxRPM));    
        }

        retval = calcMotorPower(rightPos, Rdeadband);
        if(INVALID_INPUT == retval) {
            System.out.println("Invalid right motor input" + rightPos);
        } else {
            rightTalonLead.set(TalonFXControlMode.Velocity,(retval * maxRPM));
        }
    }


    // Stops motor usually used after the drive command ends to prevent shenanigans
    public void stop() {
        leftTalonLead.set(TalonFXControlMode.Current,0);
        rightTalonLead.set(TalonFXControlMode.Current,0);
    }

    //Calculates the motor power to use based on a given deadband and joystick input from -1 to 1
    //Prevents spikes in motor power by calculating the line to use where 0 is the deadband and 1 is the max
    public double calcMotorPower(double input, double deadband) {
        double retval = 0.0;
        if(Math.abs(input) <= deadband) { //Check if input is inside the deadband
            return 0;
        }

        if((input < -1) || (input > 1)) { //input must be between -1 and 1
            return INVALID_INPUT;
        }
        
        retval = (1/(1 - deadband) * Math.abs(input) - (deadband/(1 - deadband)));

        if(input < 0) {
           return -1 * retval;
        } else {
            return retval;
        }
    }

}

