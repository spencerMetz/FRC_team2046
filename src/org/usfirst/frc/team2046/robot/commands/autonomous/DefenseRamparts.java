package org.usfirst.frc.team2046.robot.commands.autonomous;

import org.usfirst.frc.team2046.robot.commands.DefenceDefeaterCommand;
import org.usfirst.frc.team2046.robot.commands.DrivePrecisely;
import org.usfirst.frc.team2046.robot.util.TripleSolenoid.Position;

import edu.wpi.first.wpilibj.command.CommandGroup;
 
public class DefenseRamparts extends CommandGroup{
	private final double EXTRA_DISTANCE = 1.5;
	public DefenseRamparts(double distance, double speed) {

		super("Ramparts");
		addParallel(new DefenceDefeaterCommand(Position.MID));
		addSequential(new DrivePrecisely(distance+EXTRA_DISTANCE, speed));
		addSequential(new DefenceDefeaterCommand(Position.EXTEND));
	}

	public DefenseRamparts() {
		this(12.5, 0.5);
	}
}
