package org.usfirst.frc.team2046.robot.commands;

import org.usfirst.frc.team2046.robot.subsystems.Ballista;
import org.usfirst.frc.team2046.robot.util.TripleSolenoid;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class ClimbCommand extends CommandGroup {
	public ClimbCommand(){
		addSequential(new DeployHookCommand(true));
		addParallel(new ShoulderCommand(Ballista.ANGLE_CLIMBING_UP));
		addSequential(new WaitCommand(0.4));
		addSequential(new DefenceDefeaterCommand(TripleSolenoid.Position.RETRACT));
//		addSequential(new ExtendHookCommand(0.8));
//		addSequential(new WaitCommand(1.0));
//		addSequential(new ExtendHookCommand(-0.8));
//		addSequential(new WaitCommand(1.0));
//		addSequential(new DeployHookCommand(false));
//		addSequential(new WinchCommand(1.0));
//		addSequential(new WaitCommand(1.0));
//		addSequential(new WinchCommand(0.0));
	}
}
