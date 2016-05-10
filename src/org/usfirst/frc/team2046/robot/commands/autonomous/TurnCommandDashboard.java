package org.usfirst.frc.team2046.robot.commands.autonomous;

import org.usfirst.frc.team2046.robot.subsystems.Chassis;

import edu.wpi.first.wpilibj.PIDControllerTest;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class TurnCommandDashboard extends Command {
	double angle = 0.0;
	double speed = 0.5;
	private PIDControllerTest anglePIDController;
	boolean isTimeoutSet = false;
	public TurnCommandDashboard(double angle, double speed){
		this.angle = angle;
		this.speed = speed;
    	requires(Chassis.getInstance());
        SmartDashboard.putNumber("Angle_to_turn", 90.0);
        SmartDashboard.putNumber("testSpeed", speed);
	}
    public TurnCommandDashboard(double angle) {
    	this.angle = angle;
    	requires(Chassis.getInstance());
        SmartDashboard.putNumber("Angle_to_turn", 90.0);
    }
    public TurnCommandDashboard(double angle, double speed, double timeout){
    	super(timeout);
    	isTimeoutSet = true;
    	this.angle = angle;
		this.speed = speed;
    	requires(Chassis.getInstance());
        SmartDashboard.putNumber("Angle_to_turn", 90.0);
        SmartDashboard.putNumber("testSpeed", speed);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	this.angle = SmartDashboard.getNumber("Angle_to_turn");
    	this.speed = SmartDashboard.getNumber("testSpeed");
    	System.out.printf("TurnCommandDashboard.initialize(): %f,%f\n",this.angle,this.speed);
    	PIDSource anglePIDSource;
    	PIDOutput anglePIDOutput;
    	Chassis.getInstance().resetGyro();
    	
		anglePIDSource = new PIDSource() {
			@Override
			public void setPIDSourceType(PIDSourceType pidSource) {
			}
			
			@Override
			public double pidGet() {
				return Chassis.getInstance().getChassisAngle();
			}
			
			@Override
			public PIDSourceType getPIDSourceType() {
				return PIDSourceType.kDisplacement;
			}
		};
		anglePIDOutput = new PIDOutput() {
			
			@Override
			public void pidWrite(double output) {
				Chassis.getInstance().setSpeed(output, -output);
			}
		};
		anglePIDController = new PIDControllerTest(0.16, 0.01, 0.15, anglePIDSource, anglePIDOutput,0.02);
		anglePIDController.setSetpoint(angle);
		anglePIDController.setOutputRange(-speed, speed);
		anglePIDController.setAbsoluteTolerance(0.5);
		anglePIDController.setToleranceBuffer(5);
		anglePIDController.enable();
	
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	SmartDashboard.putNumber("angle left to go", angle-Chassis.getInstance().getChassisAngle());
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	System.out.printf("Current angle:%f\n",Chassis.getInstance().getChassisAngle());
        return  anglePIDController.onTarget()||(isTimeoutSet&&isTimedOut());
    }

    // Called once after isFinished returns true
    protected void end() {
    	if (isTimedOut()) {
    		System.out.println("TurnCommand timedout");
    	}
    	anglePIDController.disable();
    	Chassis.getInstance().setSpeed(0.0, 0.0);
    	System.out.printf("Final Angle:%f\n",Chassis.getInstance().getChassisAngle());
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}