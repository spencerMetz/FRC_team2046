package org.usfirst.frc.team2046.robot.commands.autonomous;

import org.usfirst.frc.team2046.robot.commands.DefenceDefeaterCommand;
import org.usfirst.frc.team2046.robot.commands.DrivePrecisely;
import org.usfirst.frc.team2046.robot.util.TripleSolenoid.Position;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class DefenseMoat extends CommandGroup {
	private final double EXTRA_DISTANCE = 2.5;
	public DefenseMoat(double distance, double speed) {
		
		super("Moat");

		addParallel(new DefenceDefeaterCommand(Position.MID));
		addSequential(new DrivePrecisely(distance+EXTRA_DISTANCE, 0.9));
		addSequential(new DefenceDefeaterCommand(Position.EXTEND));
	}

	public DefenseMoat() {
		this(12.5, 0.6);
	}
}
