package org.usfirst.frc.team2046.robot;

import org.usfirst.frc.team2046.robot.autonomous.AutonomousChooser;
import org.usfirst.frc.team2046.robot.commands.autonomous.TurnCommandDashboard;
import org.usfirst.frc.team2046.robot.subsystems.Ballista;
import org.usfirst.frc.team2046.robot.subsystems.Chassis;
import org.usfirst.frc.team2046.robot.subsystems.Climber;
import org.usfirst.frc.team2046.robot.subsystems.DefenceDefeater;
import org.usfirst.frc.team2046.robot.subsystems.Interlock;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends IterativeRobot {
	public OI oi;
	public Chassis chassis;
	public Ballista ballista;
	public DefenceDefeater defenceDefeater;
	public Climber climber;
	private Command autonomousCommand;
	
	private final AutonomousChooser autonomousChooser = new AutonomousChooser();
	
	public void robotInit() {

		chassis = Chassis.getInstance();
		ballista = Ballista.getInstance();
		defenceDefeater = DefenceDefeater.getInstance();
		climber = Climber.getInstance();	
		oi = OI.getInstance();
		
		autonomousChooser.initialize();
		SmartDashboard.putData(new TurnCommandDashboard(0.0, 0.0));
		Chassis.getInstance().recalibrateGyro();
	}

	public void disabledInit() {
	}

	public void disabledPeriodic() {
		Chassis.getInstance().updateAllTheTime();
		Ballista.getInstance().updateDashboard();
		Interlock.getInstance().updateDashboard();
		Scheduler.getInstance().run();
	}

	public void autonomousInit() {
		if (autonomousCommand != null) {
			autonomousCommand.cancel();
			autonomousCommand = null;
		}

		autonomousCommand = autonomousChooser.getAutonomousCommand();
		
		if (autonomousCommand != null) {
			autonomousCommand.start();
		}
	}

	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
		Chassis.getInstance().updateDashboard();
	}

	public void teleopInit() {
		if (autonomousCommand != null) {
			autonomousCommand.cancel();
			autonomousCommand = null;
		}
	}

	public void teleopPeriodic() {
		Scheduler.getInstance().run();

		Ballista.getInstance().updateDashboard();
		Chassis.getInstance().updateDashboard();
		//Climber.getInstance().updateDashboard();
		Interlock.getInstance().updateDashboard();
	}

	public void testInit() {
		Chassis.getInstance().recalibrateGyro();
	}

	public void testPeriodic() {
	}
}