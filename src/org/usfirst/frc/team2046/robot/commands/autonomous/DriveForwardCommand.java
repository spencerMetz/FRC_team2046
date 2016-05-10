package org.usfirst.frc.team2046.robot.commands.autonomous;

import org.usfirst.frc.team2046.robot.subsystems.Chassis;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveForwardCommand extends Command {
	private double distance;
	private double throttle;
	private PIDController anglePIDController;
	private PIDSource anglePIDSource;
	private PIDOutput anglePIDOutput;
	public DriveForwardCommand(double distance, double throttle){
		requires(Chassis.getInstance());
		this.distance = distance;
		this.throttle = throttle;
		anglePIDSource = new PIDSource() {
			@Override
			public void setPIDSourceType(PIDSourceType pidSource) {
			}
			
			@Override
			public double pidGet() {
				return Chassis.getInstance().getChassisAngleFromEncoders();
			}
			
			@Override
			public PIDSourceType getPIDSourceType() {
				return PIDSourceType.kDisplacement;
			}
		};
		anglePIDOutput = new PIDOutput() {
			
			@Override
			public void pidWrite(double output) {
				Chassis.getInstance().setSpeed(throttle+output, throttle-output);
			}
		};
		anglePIDController = new PIDController(0.05, 0.0, 0.0, anglePIDSource, anglePIDOutput);
		anglePIDController.setSetpoint(0.0);
		
	}
	
	public DriveForwardCommand(){
		this(10.0,0.6);
	}
	
	
	protected void initialize() {
		System.out.println("DriveForwardCommand: "+ this.distance +","+this.throttle);
//		distance = SmartDashboard.getNumber("DriveStraightDistance");
		Chassis.getInstance().resetSensors();
		anglePIDController.enable();
	}

	protected void execute() {
		SmartDashboard.putNumber("gyro value",Chassis.getInstance().getChassisAngle());
		SmartDashboard.putNumber("leftDistance", Chassis.getInstance().getLeftDistance());
		SmartDashboard.putNumber("leftDistance", Chassis.getInstance().getRightDistance());
	}

	protected boolean isFinished() {
		return Chassis.getInstance().getAverageDistance() >= distance;
	}

	protected void end() {
		anglePIDController.disable();
		Chassis.getInstance().setSpeed(0.0, 0.0);
		System.out.printf("DriveForwardCommanded ended: %f",timeSinceInitialized());
	}

	protected void interrupted() {
		System.out.println("DRIVE FORWARD INTERRUPTED");
		end();
	}

}
