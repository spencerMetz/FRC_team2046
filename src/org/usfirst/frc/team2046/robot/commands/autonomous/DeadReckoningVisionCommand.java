package org.usfirst.frc.team2046.robot.commands.autonomous;

import org.usfirst.frc.team2046.robot.commands.DefenceDefeaterCommand;
import org.usfirst.frc.team2046.robot.commands.RaiseAndSpinupCommand;
import org.usfirst.frc.team2046.robot.commands.ShiftGearCommand;
import org.usfirst.frc.team2046.robot.commands.ShootAndLowerCommand;
import org.usfirst.frc.team2046.robot.commands.VisionAlignCommand;
import org.usfirst.frc.team2046.robot.util.TripleSolenoid.Position;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class DeadReckoningVisionCommand extends CommandGroup {
	public DeadReckoningVisionCommand(){
		addSequential(new ShiftGearCommand(false));
		addSequential(new DefenceDefeaterCommand(Position.EXTEND));
		addSequential(new DriveForwardCommand(17.4, 0.6));
		addSequential(new TurnCommand(59));
		addSequential(new VisionAlignCommand());
		addSequential(new DriveForwardCommand(8.75, 0.5));
		addParallel(new DriveForwardCommand(5, 0.25));
		addSequential(new RaiseAndSpinupCommand());
		addSequential(new WaitCommand(1.0));
		addSequential(new ShootAndLowerCommand());
	}
}
