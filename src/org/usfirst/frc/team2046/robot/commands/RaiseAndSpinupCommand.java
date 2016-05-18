package org.usfirst.frc.team2046.robot.commands;

import org.usfirst.frc.team2046.robot.subsystems.Ballista;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class RaiseAndSpinupCommand extends CommandGroup {
	public RaiseAndSpinupCommand(){
		this(true);
	}
	
	public RaiseAndSpinupCommand(boolean waitForSpinup){
		this(true, Ballista.ANGLE_UP, Ballista.SHOOTING_RPM);
	}
	
	public RaiseAndSpinupCommand(boolean waitForSpinup, double angle, int rpm){

		addSequential(new ShoulderCommand(angle));
		addSequential(new WaitCommand(0.5));
		addSequential(new SetShooterRPMCommand(rpm, waitForSpinup));
	}
}
