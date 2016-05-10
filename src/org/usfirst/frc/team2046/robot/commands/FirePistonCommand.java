package org.usfirst.frc.team2046.robot.commands;

import org.usfirst.frc.team2046.robot.subsystems.Ballista;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class FirePistonCommand extends Command {

	public FirePistonCommand() {
	}

	@Override
	protected void initialize() {
		Ballista.getInstance().firePiston();
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
