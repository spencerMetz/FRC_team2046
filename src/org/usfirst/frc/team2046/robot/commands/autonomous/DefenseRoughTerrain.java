package org.usfirst.frc.team2046.robot.commands.autonomous;

import org.usfirst.frc.team2046.robot.commands.DefenceDefeaterCommand;
import org.usfirst.frc.team2046.robot.commands.DrivePrecisely;
import org.usfirst.frc.team2046.robot.util.TripleSolenoid.Position;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class DefenseRoughTerrain extends CommandGroup {
	private final double EXTRA_DISTANCE = .9166;
	public DefenseRoughTerrain(double distance, double speed) {

		super("Rough Terrain");
		addParallel(new DefenceDefeaterCommand(Position.MID));
		addSequential(new DrivePrecisely(distance+EXTRA_DISTANCE, speed));
		addSequential(new DefenceDefeaterCommand(Position.EXTEND));
	}

	public DefenseRoughTerrain() {
		this(17.5, 0.5);
	}
}
