package org.usfirst.frc.team2046.robot.commands;

import org.usfirst.frc.team2046.robot.subsystems.Ballista;
import org.usfirst.frc.team2046.robot.subsystems.DefenceDefeater;

import edu.wpi.first.wpilibj.command.Command;

public class CollectCommand extends Command {

	private final double speed;
	private final boolean toggle;

	public CollectCommand(double speed, boolean toggle) {
		this.speed = speed;
		this.toggle = toggle;
	}
	
	public CollectCommand(double speed){
		this(speed, false);
	}

	@Override
	protected void end() {
	}

	@Override
	protected void execute() {
	}

	@Override
	protected void initialize() {
		double tmp = speed;
//		System.out.println(DefenceDefeater.getInstance().getSpeed() + " " + speed);
//		if (DefenceDefeater.getInstance().getSpeed() == speed) {
//			tmp = 0.0;
//		}
		Ballista.getInstance().setSpeed(-tmp, toggle);
		DefenceDefeater.getInstance().setSpeed(tmp, toggle);
//		System.out.println(tmp);
	}

	@Override
	protected void interrupted() {
	}

	@Override
	protected boolean isFinished() {
		return true;
	}

}
