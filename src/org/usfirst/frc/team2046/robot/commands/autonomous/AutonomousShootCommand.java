package org.usfirst.frc.team2046.robot.commands.autonomous;

import org.usfirst.frc.team2046.robot.commands.RaiseAndSpinupCommand;
import org.usfirst.frc.team2046.robot.commands.ShootAndLowerCommand;
import org.usfirst.frc.team2046.robot.commands.ShootLowerGoalCommand;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class AutonomousShootCommand extends CommandGroup {
	public enum GoalType{
		LOW,
		HIGH
	}
	
	public AutonomousShootCommand(GoalType type){
		switch(type){
		case HIGH:
			addSequential(new RaiseAndSpinupCommand());
    		addSequential(new WaitCommand(1.0));
    		addSequential(new ShootAndLowerCommand());
			break;
		case LOW:
			addSequential(new ShootLowerGoalCommand());
			break;
		default:
			break;
		}
	}
}
