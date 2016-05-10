package org.usfirst.frc.team2046.robot.commands.autonomous;

import org.usfirst.frc.team2046.robot.commands.DefenceDefeaterCommand;
import org.usfirst.frc.team2046.robot.util.TripleSolenoid.Position;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class DefenseChevalDeFrise extends CommandGroup {
	private final double DEFENSE_DISTANCE = 3.7;
	private final double DEFENSE_SPEED = 0.6;
	private final double CROSSING_SPEED_DIVISOR = 1;
	private final double EXTRA_DISTANCE = 0.0;

	public DefenseChevalDeFrise(double distance, double speed) {
		super("ChevalDeFrise");
		addSequential(new DriveForwardCommand(DEFENSE_DISTANCE, DEFENSE_SPEED));
		addSequential(new DefenceDefeaterCommand(Position.EXTEND));
		addSequential(new WaitCommand(1));
		addSequential(new DriveForwardCommand(distance - DEFENSE_DISTANCE + EXTRA_DISTANCE, speed / CROSSING_SPEED_DIVISOR));
	}

	public DefenseChevalDeFrise() {
		this(12.1, 0.5);

	}
}