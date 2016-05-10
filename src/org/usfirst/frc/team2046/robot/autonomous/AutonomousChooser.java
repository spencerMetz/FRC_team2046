package org.usfirst.frc.team2046.robot.autonomous;

import org.usfirst.frc.team2046.robot.commands.RaiseAndSpinupCommand;
import org.usfirst.frc.team2046.robot.commands.ShiftGearCommand;
import org.usfirst.frc.team2046.robot.commands.ShootAndLowerCommand;
import org.usfirst.frc.team2046.robot.commands.VisionAlignCommand;
import org.usfirst.frc.team2046.robot.commands.autonomous.AutoShootLowerGoalCommand;
import org.usfirst.frc.team2046.robot.commands.autonomous.DeadReckoningCommand;
import org.usfirst.frc.team2046.robot.commands.autonomous.DeadReckoningVisionCommand;
import org.usfirst.frc.team2046.robot.commands.autonomous.DriveForwardCommand;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.PrintCommand;
import edu.wpi.first.wpilibj.command.WaitCommand;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutonomousChooser {
	
	private final PositionChooser positionChooser = new PositionChooser();
	private final DefenseChooser defenseChooser = new DefenseChooser();
	private final SendableChooser autoCommandChooser = new SendableChooser();

	private enum AutoType {
		NONE, DEFENSE_ONLY, NO_SHOOT, LOW_GOAL, HIGH_GOAL, OLD, OLD_WITH_VISION;
	}

	public void initialize() {
		positionChooser.initialize();
		defenseChooser.initialize();
		
		autoCommandChooser.addDefault("High Goal", 		AutoType.HIGH_GOAL);
		autoCommandChooser.addObject("Low Goal", 		AutoType.LOW_GOAL);
		autoCommandChooser.addObject("No Shoot", 		AutoType.NO_SHOOT);
		autoCommandChooser.addObject("Defense Only", 	AutoType.DEFENSE_ONLY);
		autoCommandChooser.addObject("No Auto ",	 	AutoType.NONE);
		autoCommandChooser.addObject("Old Dead Reckoning",	 	AutoType.OLD);
		autoCommandChooser.addObject("Vision Dead Reckoning",	 	AutoType.OLD_WITH_VISION);
		SmartDashboard.putData("Autonomous Command", autoCommandChooser);
	}

	public Command getAutonomousCommand() {

		AutoType type = (AutoType) autoCommandChooser.getSelected();
		Command defenseCommand = defenseChooser.getDefenseCommand();
		Command positionCommand = positionChooser.getPositionCommand();
		
		Command finalDrive = new DriveForwardCommand(2.91, 0.5);
		CommandGroup autoCommand = new CommandGroup();
		
		// Low goal invalid for center goal.
		if (positionChooser.isCenterGoal() && type == AutoType.LOW_GOAL){
			type = AutoType.HIGH_GOAL;
		}
		
		switch (type) {
		case OLD_WITH_VISION:
			autoCommand = new DeadReckoningVisionCommand();
			break;
			
		case OLD:
			autoCommand = new DeadReckoningCommand();
			break;
			
		case HIGH_GOAL:
			autoCommand.addSequential(new ShiftGearCommand(false));
			autoCommand.addSequential(defenseCommand);
			autoCommand.addSequential(positionCommand);
			autoCommand.addSequential(new PrintCommand("---Position End---"));
			
			autoCommand.addSequential(new VisionAlignCommand());
			autoCommand.addSequential(new PrintCommand("---Align End---"));
						
			autoCommand.addSequential(new DriveForwardCommand(2.91, 0.5));
			autoCommand.addSequential(new PrintCommand("---Drive 2.91 End---"));

			autoCommand.addParallel(new DriveForwardCommand(2, 0.2));
			
			autoCommand.addSequential(new RaiseAndSpinupCommand());
			autoCommand.addSequential(new WaitCommand(1.0));
			autoCommand.addSequential(new PrintCommand("---Raise End---"));

			
			autoCommand.addSequential(new ShootAndLowerCommand());
			autoCommand.addSequential(new PrintCommand("---Shoot End---"));
			break;
		
		case LOW_GOAL:
			autoCommand.addSequential(new ShiftGearCommand(false));
			autoCommand.addSequential(defenseCommand);
			autoCommand.addSequential(positionCommand);
			autoCommand.addSequential(new VisionAlignCommand());
			autoCommand.addSequential(finalDrive);
			autoCommand.addSequential(new AutoShootLowerGoalCommand());
			break;
		
		case NO_SHOOT:
			autoCommand.addSequential(new ShiftGearCommand(false));
			autoCommand.addSequential(defenseCommand);
			autoCommand.addSequential(positionCommand);
			autoCommand.addSequential(new VisionAlignCommand());
			autoCommand.addSequential(finalDrive);
			break;
		
		case DEFENSE_ONLY:
			autoCommand.addSequential(new ShiftGearCommand(false));
			autoCommand.addSequential(defenseCommand);
			break;
		
		case NONE:
		default:
			return null;
		}
		
		return autoCommand;
	}
}
