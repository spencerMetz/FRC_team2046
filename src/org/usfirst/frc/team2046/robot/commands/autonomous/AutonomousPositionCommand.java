package org.usfirst.frc.team2046.robot.commands.autonomous;

import org.usfirst.frc.team2046.robot.commands.DefenceDefeaterCommand;
import org.usfirst.frc.team2046.robot.util.TripleSolenoid;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class AutonomousPositionCommand extends CommandGroup {
	public enum Position{
		FAR_LEFT, MID_LEFT, MIDDLE, MID_RIGHT, FAR_RIGHT
	}
	
	public AutonomousPositionCommand(Position position){
		switch (position){
		case FAR_LEFT:
			addSequential(new DriveForwardCommand(3.5,0.4));
			addSequential(new TurnCommand(63.0));
			addSequential(new DriveForwardCommand(8.25,0.3));
			break;
		case FAR_RIGHT:
			addSequential(new DriveForwardCommand(7.5,0.6));
			addSequential(new TurnCommand(-61.0,0.7));
			addSequential(new DriveForwardCommand(2.5,0.4));
			break;
		case MIDDLE:
			break;
		case MID_LEFT:
			addSequential(new TurnCommand(-10.0,0.7));
    		addSequential(new WaitCommand(0.3));
    		addSequential(new DriveForwardCommand(2,0.5));
    		addSequential(new TurnCommand(10,0.7));
    		addSequential(new WaitCommand(0.2));
    		addSequential(new DefenceDefeaterCommand(TripleSolenoid.Position.EXTEND));
    		addSequential(new DriveForwardCommand(4,0.3));
			break;
		case MID_RIGHT:
			break;
		default:
			break;
	
		}
	}
}
