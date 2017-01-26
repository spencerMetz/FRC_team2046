package org.usfirst.frc.team2046.robot.commands;

import org.usfirst.frc.team2046.robot.subsystems.Ballista;

import edu.wpi.first.wpilibj.command.Command;


/**
 *
 */
public class SetShooterRPMCommand extends Command {
	private int RPM;
	private boolean waitForSpinup;
	public SetShooterRPMCommand(int RPM, boolean waitForSpinup) {
		this.RPM = RPM;
		this.waitForSpinup = waitForSpinup;
	}
	
	public SetShooterRPMCommand(int RPM){
		this(RPM, false);
	}
	
	public SetShooterRPMCommand(){
		this(Ballista.SHOOTING_RPM);
	}

	protected void initialize() {
		Ballista.getInstance().setRate(RPM);
	}

	protected void execute() {
	}

	protected boolean isFinished() {
		if (RPM == 0 || !waitForSpinup){
			return true;
		}
		else{
			return Ballista.getInstance().shooterOnTarget();
		}
	}

	protected void end() {		
	}

	protected void interrupted() {
	}
}
