package org.usfirst.frc.team2046.robot.commands.autonomous;

import org.usfirst.frc.team2046.robot.commands.DefenceDefeaterCommand;
import org.usfirst.frc.team2046.robot.commands.DrivePrecisely;
import org.usfirst.frc.team2046.robot.util.TripleSolenoid.Position;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class DefenseRockWall extends CommandGroup {
	private final double EXTRA_DISTANCE = 1.66;
	public DefenseRockWall() {
		this(17.5,0.5);
	}
	public DefenseRockWall(double distance, double speed){
		super("Rock Wall");
		addParallel(new DefenceDefeaterCommand(Position.MID));
		addSequential(new DrivePrecisely(distance+EXTRA_DISTANCE, speed));
		addSequential(new DefenceDefeaterCommand(Position.EXTEND));
	}
}