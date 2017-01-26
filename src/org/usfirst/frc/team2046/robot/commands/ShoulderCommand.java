package org.usfirst.frc.team2046.robot.commands;

import org.usfirst.frc.team2046.robot.subsystems.Ballista;

import edu.wpi.first.wpilibj.command.Command;

public class ShoulderCommand extends Command {

	private final double angle;
	private final boolean toggle;
	
	public ShoulderCommand(double angle, boolean toggle) {
		this.angle = angle;
		this.toggle = toggle;
	}
	
	public ShoulderCommand(double angle){
		this(angle, false);
	}
	
	public ShoulderCommand(){
		this(Ballista.ANGLE_UP, true);
	}
	
	protected void initialize() {
		Ballista.getInstance().setShoulderAngle(angle, toggle);
	}

	protected void execute() {
	}

	protected boolean isFinished() {
		return true;
	}

	protected void end() {
	}

	protected void interrupted() {
		end();
	}
}
