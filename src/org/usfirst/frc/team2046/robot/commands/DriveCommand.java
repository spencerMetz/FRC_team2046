package org.usfirst.frc.team2046.robot.commands;

import org.usfirst.frc.team2046.robot.OI;
import org.usfirst.frc.team2046.robot.operator.XboxController;
import org.usfirst.frc.team2046.robot.subsystems.Chassis;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;

public class DriveCommand extends Command {
	private static final double Trigger_Threshold = 0.8;
	OI oi = OI.getInstance();
	private double rightTriggerValue;
	private double lastRightTriggerValue;
	private Chassis chassis;
	
	public DriveCommand() {
		chassis = Chassis.getInstance();
		// Use requires() here to declare subsystem dependencies
		requires(chassis);
	}

	// Called just before this Command runs the first time
	protected void initialize() {
	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute()
    {
		double horizontalPower = chassis.getHorizontalPowerCurve();
		double forwardPower =  chassis.getForwardPowerCurve();
		double lJoystick = oi.getDriverJoystick().getAxisValue(XboxController.LEFT_LONGITUDIAL_AXIS);
		double rJoystick = oi.getDriverJoystick().getAxisValue(XboxController.RIGHT_LONGITUDIAL_AXIS);
		double halfSum = 0.5*(lJoystick+rJoystick);
		double halfDifference = 0.5*(lJoystick-rJoystick);
		double forward = Math.pow(Math.abs(halfSum),forwardPower-1.0) * halfSum;
		double horizontal = Math.pow(Math.abs(halfDifference),horizontalPower-1.0) * halfDifference;
    	double lpower = forward+horizontal;
    	double rpower = forward-horizontal;
    	Chassis.getInstance().setSpeed(lpower, rpower);
    	
    	rightTriggerValue = OI.getInstance().getDriverJoystick().getAxisValue(XboxController.RIGHT_TRIGGER_AXIS);

    	if (lastRightTriggerValue <= Trigger_Threshold && rightTriggerValue > Trigger_Threshold) {
			Scheduler.getInstance().add(new ShiftGearCommand(true));
		}else if(lastRightTriggerValue > Trigger_Threshold && rightTriggerValue <= Trigger_Threshold){
			Scheduler.getInstance().add(new ShiftGearCommand(false));
		}
		
    	lastRightTriggerValue = rightTriggerValue;
    }

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return false;
	}

	// Called once after isFinished returns true
	protected void end() {
		Chassis.getInstance().setSpeed(0.0, 0.0);
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
		end();
	}
}