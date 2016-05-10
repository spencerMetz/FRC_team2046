package org.usfirst.frc.team2046.robot.commands.autonomous;

import org.usfirst.frc.team2046.robot.commands.DefenceDefeaterCommand;
import org.usfirst.frc.team2046.robot.util.TripleSolenoid.Position;

import edu.wpi.first.wpilibj.command.CommandGroup;


public class DefensePortcullis extends CommandGroup {
	private final double EXTRA_DISTANCE = 0.0;
	public DefensePortcullis() {
		this(12.5, 0.35);
	}

	public DefensePortcullis(double distance, double speed) {
		super("Portcullis");
		addParallel(new DefenceDefeaterCommand(Position.EXTEND));
		addSequential(new DriveForwardCommand(distance + EXTRA_DISTANCE, speed));
	}
}
