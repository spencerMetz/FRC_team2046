package org.usfirst.frc.team2046.robot.commands;

import org.usfirst.frc.team2046.robot.subsystems.Chassis;

import edu.wpi.first.wpilibj.command.Command;

public class ShiftGearCommand extends Command {

	private boolean highGear;
	
	public ShiftGearCommand(boolean highGear) {
		this.highGear = highGear;
	}

	@Override
	protected void end() {}

	@Override
	protected void execute() {}

	@Override
	protected void initialize() {
		Chassis.getInstance().setGear(highGear);
	}

	@Override
	protected void interrupted() {}

	@Override
	protected boolean isFinished() {
		return true;
	}

}
