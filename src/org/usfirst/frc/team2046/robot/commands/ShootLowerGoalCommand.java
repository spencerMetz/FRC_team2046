package org.usfirst.frc.team2046.robot.commands;

import org.usfirst.frc.team2046.robot.util.TripleSolenoid;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class ShootLowerGoalCommand extends CommandGroup {
	public ShootLowerGoalCommand(){
		addSequential(new DefenceDefeaterCommand(TripleSolenoid.Position.MID));
		addSequential(new WaitCommand(0.5));
		addSequential(new CollectCommand(1.0));
		addSequential(new WaitCommand(0.5));
		addSequential(new FirePistonCommand());
		addSequential(new WaitCommand(1.0));
		addSequential(new CollectCommand(0.0));
		addSequential(new DefenceDefeaterCommand(TripleSolenoid.Position.EXTEND));
	}
}
