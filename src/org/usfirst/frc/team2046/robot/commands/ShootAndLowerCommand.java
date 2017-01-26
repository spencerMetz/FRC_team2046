package org.usfirst.frc.team2046.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class ShootAndLowerCommand extends CommandGroup {
	public ShootAndLowerCommand() {
		addSequential(new FirePistonCommand());
		addSequential(new WaitCommand(0.5));
		addSequential(new ShoulderCommand(0.0));
		addSequential(new SetShooterRPMCommand(0));
	}
}
