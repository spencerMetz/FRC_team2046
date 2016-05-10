package org.usfirst.frc.team2046.robot.commands.autonomous;

import org.usfirst.frc.team2046.robot.commands.DefenceDefeaterCommand;
import org.usfirst.frc.team2046.robot.util.TripleSolenoid.Position;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class DefenseLowBar extends CommandGroup {
	private final double EXTRA_DISTANCE = 0.0;
	public DefenseLowBar() {
		this(10.0, 0.6);
	}
	
	public DefenseLowBar(double distance, double speed) {		
		super("Low Bar");
		addSequential(new DefenceDefeaterCommand(Position.EXTEND));
		addSequential(new DriveForwardCommand(distance+EXTRA_DISTANCE, speed));

	}
}
