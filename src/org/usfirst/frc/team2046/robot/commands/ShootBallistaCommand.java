package org.usfirst.frc.team2046.robot.commands;

import org.usfirst.frc.team2046.robot.subsystems.Ballista;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class ShootBallistaCommand extends CommandGroup {

	public ShootBallistaCommand() {
		requires(Ballista.getInstance());
		addSequential(new SetShooterRPMCommand(4500));
		addSequential(new FirePistonCommand());
		addSequential(new WaitCommand(0.5));
		addSequential(new ShoulderCommand(0.0));
		addSequential(new SetShooterRPMCommand(0));
	}
}
