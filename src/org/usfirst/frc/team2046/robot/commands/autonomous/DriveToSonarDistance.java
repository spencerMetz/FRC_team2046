package org.usfirst.frc.team2046.robot.commands.autonomous;

import org.usfirst.frc.team2046.robot.commands.DrivePrecisely;
import org.usfirst.frc.team2046.robot.subsystems.Chassis;

import edu.wpi.first.wpilibj.command.Command;

public class DriveToSonarDistance extends Command {
	private final double sonarDistance;
	private final Chassis chassis = Chassis.getInstance();
	private final Command drive;
	private boolean hasTimeout = false;
	
	public DriveToSonarDistance(double distance, double sonarDistance, double speed) {
		this.drive = new DrivePrecisely(distance, speed);
		this.sonarDistance = sonarDistance;
	}
	public DriveToSonarDistance(double distance, double sonarDistance, double speed, double timeout) {
		super(timeout);
		this.drive = new DrivePrecisely(distance, speed);
		this.sonarDistance = sonarDistance;
		hasTimeout = true;
	}

	@Override
	protected void initialize() {
		drive.start();
	}

	@Override
	protected void execute() {
	}

	@Override
	protected boolean isFinished() {
		return (chassis.getSonarDistance() <= sonarDistance)||(hasTimeout&&isTimedOut());
	}

	@Override
	protected void end() {
		drive.cancel();
	}

	@Override
	protected void interrupted() {
		end();
	}
}
