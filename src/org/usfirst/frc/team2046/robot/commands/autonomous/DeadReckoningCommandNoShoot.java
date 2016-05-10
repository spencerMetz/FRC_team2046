package org.usfirst.frc.team2046.robot.commands.autonomous;

import org.usfirst.frc.team2046.robot.commands.DefenceDefeaterCommand;
import org.usfirst.frc.team2046.robot.commands.ShiftGearCommand;
import org.usfirst.frc.team2046.robot.util.TripleSolenoid.Position;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class DeadReckoningCommandNoShoot extends CommandGroup {
	public DeadReckoningCommandNoShoot(){
		addSequential(new ShiftGearCommand(false));
		addSequential(new DefenceDefeaterCommand(Position.EXTEND));
		addSequential(new DriveForwardCommand(17.4, 0.6));
		addSequential(new TurnCommand(60));
		addSequential(new DriveForwardCommand(7.75, 0.5));
		addParallel(new DriveForwardCommand(5, 0.25));
//		addSequential(new RaiseAndSpinupCommand());
//		addSequential(new ShootAndLowerCommand());
	}
}
