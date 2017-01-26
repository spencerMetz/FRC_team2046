package org.usfirst.frc.team2046.robot.commands;


import org.usfirst.frc.team2046.robot.subsystems.Chassis;

import edu.wpi.first.wpilibj.command.Command;

public class RotateCommand extends Command {

	private final Chassis chassis = Chassis.getInstance();
	
	private final double angle;
	
	private final double maxSpeed;
	private boolean hasTimeout = false;
	
	public RotateCommand(double angle) {
		this(angle, 0.5);
	}

	public RotateCommand(double angle, double maxSpeed) {
		this.angle = angle;
		this.maxSpeed = maxSpeed;
		requires(chassis);
	}
	public RotateCommand(double angle, double maxSpeed, double timeout) {
		super(timeout);
		this.angle = angle;
		this.maxSpeed = maxSpeed;
		requires(chassis);
		hasTimeout = true;
	}
	
	@Override
	protected void initialize() {
		chassis.setRotationalSetpoint(angle, maxSpeed);
	}

	@Override
	protected void execute() {
	}

	@Override
	protected boolean isFinished() {
		return chassis.angleOnTarget()||(hasTimeout&&isTimedOut());
	}

	@Override
	protected void end() {
		chassis.disableControllers();
	}

	@Override
	protected void interrupted() {
		end();
	}

}
