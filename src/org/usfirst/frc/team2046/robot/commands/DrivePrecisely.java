package org.usfirst.frc.team2046.robot.commands;

import org.usfirst.frc.team2046.robot.subsystems.Chassis;

import edu.wpi.first.wpilibj.command.Command;

public class DrivePrecisely extends Command {

	private final Chassis chassis = Chassis.getInstance();
	
	private final double distance, angle, maxSpeed;
	
	public DrivePrecisely(double distance, double angle, double maxSpeed) {
		requires(chassis);
		this.distance = distance;
		this.angle = angle;
		this.maxSpeed = maxSpeed;
	}
	
	public DrivePrecisely(double distance, double angle, double maxSpeed, double timeout) {
		super(timeout);
		requires(chassis);
		this.distance = distance;
		this.angle = angle;
		this.maxSpeed = maxSpeed;
	}	
	public DrivePrecisely(double distance, double maxSpeed) {
		this(distance, 0, maxSpeed);
	}

	@Override
	protected void initialize() {
		chassis.setSetpoints(distance, angle, maxSpeed);
		System.out.println("initialized");
	}

	@Override
	protected void execute() {		
		if (chassis.isLinearControllerEnabled() && chassis.distanceOnTarget()) {
			chassis.disableLinearControllers();
		}
	}

	@Override
	protected boolean isFinished() {
		//System.out.println("time: " + timeSinceInitialized());
		if ((!chassis.isLinearControllerEnabled() || chassis.distanceOnTarget()) && chassis.angleOnTarget()) {
			System.out.println("finished");
			return true;
		} else {
			return isTimedOut();
		}
	}

	@Override
	protected void end() {
		if (isTimedOut()) {
			System.out.println("timed out");
		}
		chassis.disableControllers();
	}

	@Override
	protected void interrupted() {
		end();
	}

}
