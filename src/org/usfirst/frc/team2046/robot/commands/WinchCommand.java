package org.usfirst.frc.team2046.robot.commands;

import org.usfirst.frc.team2046.robot.subsystems.Climber;

import edu.wpi.first.wpilibj.command.Command;

public class WinchCommand extends Command {

	private double speed;
	
	public WinchCommand(double speed){
		this.speed = speed;
	}
	
	@Override
	protected void initialize() {
		Climber.getInstance().setWinchSpeed(speed);
	}

	@Override
	protected void execute() {
	}

	@Override
	protected boolean isFinished() {
		return true;
	}

	@Override
	protected void end() {
	}

	@Override
	protected void interrupted() {
	}

}
