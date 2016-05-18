package org.usfirst.frc.team2046.robot.commands;

import org.usfirst.frc.team2046.robot.subsystems.Chassis;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class SetLights extends Command {
	
	private final boolean on;
	
	public SetLights(boolean on) {
    	this.on = on;
    }

    protected void initialize() {
    	Chassis.getInstance().setLights(on);
    }

    protected void execute() {
    }

    protected boolean isFinished() {
        return true;
    }
    protected void end() {
    }

    protected void interrupted() {
    }
}
