package org.usfirst.frc.team2046.robot.commands;

import org.usfirst.frc.team2046.robot.subsystems.Climber;

import edu.wpi.first.wpilibj.command.Command;

public class DeployHookCommand extends Command {
	private boolean upwards;
	private boolean isToggle = false;

	public DeployHookCommand(boolean upwards) {
		this.upwards = upwards;
	}

	public DeployHookCommand() {
		isToggle = true;
	}

	@Override
	protected void initialize() {
		if (isToggle) {
			Climber.getInstance().actuateClimber(upwards);
			upwards = !upwards;
		} else {
			Climber.getInstance().actuateClimber(upwards);
		}
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
