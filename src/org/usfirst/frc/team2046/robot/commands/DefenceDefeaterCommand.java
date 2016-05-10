package org.usfirst.frc.team2046.robot.commands;

import org.usfirst.frc.team2046.robot.subsystems.DefenceDefeater;
import org.usfirst.frc.team2046.robot.util.TripleSolenoid.Position;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class DefenceDefeaterCommand extends Command {
	private Position dir;

	public DefenceDefeaterCommand(Position direction) {
		// Use requires() here to declare subsystem dependencies
		// requires(DefenceDefeater.getInstance());
		dir = direction;
	}

	// Called just before this Command runs the first time
	protected void initialize() {
		 System.out.println(dir.toString());
		 DefenceDefeater.getInstance().setWedge(dir);
//		if (dir == TripleSolenoid.Position.EXTEND)
//			DefenceDefeater.getInstance().setWedge(Value.kForward);
//		else if (dir == TripleSolenoid.Position.RETRACT)
//			DefenceDefeater.getInstance().setWedge(Value.kReverse);

	}

	// Called repeatedly when this Command is scheduled to run
	protected void execute() {
	}

	// Make this return true when this Command no longer needs to run execute()
	protected boolean isFinished() {
		return true;
	}

	// Called once after isFinished returns true
	protected void end() {
		// DefenceDefeater.getInstance().getWedge();
	}

	// Called when another command which requires one or more of the same
	// subsystems is scheduled to run
	protected void interrupted() {
		end();
	}
}